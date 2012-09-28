package encoder.parallelization.tasks;

import encoder.configuration.interfaces.IConfiguration;
import encoder.io.BitInputStream;
import encoder.io.BitOutputStream;
import encoder.parallelization.ChunkContext;
import encoder.parallelization.Dependency;
import encoder.parallelization.EnRunningStates;
import encoder.parallelization.Schedules.MirroredParallelSchedule;
import encoder.parallelization.ThreadPool;
import encoder.parallelization.interfaces.ISchedule;
import encoder.parallelization.interfaces.ITask;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen, valon
 * Date: 14.09.12
 * Time: 11:03
 *
 * The ChunkTaskManagementTask is a management task for the schedule. The management task pushes all further tasks into the pool.
 * Remember only at the first call of the management task all other task objects will be build and initialized.
 * For all other chunks these tasks are being reused to minimize the overhead and spare time for class initialization.
 *
 * Depending on the given schedule there will be different tasks created and in a different order.
 *
 */
public class ChunkTaskManagement extends TaskBase {
    private IConfiguration _conf;

    private ThreadPool _pool;
    private List<ITask>[] _threadTaskLists;
    private ChunkTaskManagement _otherManagementTask;
    private EndTask _endTask;
    private TranslateAndWriteTask _translateAndWriteTask;
    private boolean _inverseSchedule;
    private ISchedule _schedule;

    public ChunkTaskManagement(BitInputStream input, BitOutputStream output, ThreadPool pool, ChunkTaskManagement otherManagementTask, IConfiguration conf, ISchedule schedule) {
        super(new ChunkContext(input, output, conf, pool));
        _conf = conf;
        _pool = pool;
        _schedule = schedule;
        _otherManagementTask = otherManagementTask;
        _threadTaskLists = new LinkedList[_conf.getAvailableHardwareThreads()];
        for (int i = 0; i < _threadTaskLists.length; i++) {
            _threadTaskLists[i] = new LinkedList<ITask>();
        }
        _inverseSchedule = (schedule instanceof MirroredParallelSchedule)? true:false;
        initializeTasks();
    }

    public ChunkTaskManagement(BitInputStream input, BitOutputStream output, ThreadPool pool, IConfiguration conf, ISchedule schedule) {
        super(new ChunkContext(input, output, conf, pool));
        _conf = conf;
        _pool = pool;
        _schedule = schedule;
        _otherManagementTask = new ChunkTaskManagement(input, output, pool, this, _conf, schedule);
        _threadTaskLists = new LinkedList[_conf.getAvailableHardwareThreads()];
        for (int i = 0; i < _threadTaskLists.length; i++) {
            _threadTaskLists[i] = new LinkedList<ITask>();
        }
        _inverseSchedule = false;
        initializeTasks();
    }

    private void initializeTasks() {
        _translateAndWriteTask = _schedule.fillSchedule(_context, _threadTaskLists, _conf, _otherManagementTask);
        if(_inverseSchedule){
            List<ITask> tmp = _threadTaskLists[0];
            _threadTaskLists[0] = _threadTaskLists[1];
            _threadTaskLists[1] = tmp;
        }
    }

    @Override
    protected void afterRun() {
        super.afterRun();
        reset(new ChunkContext(_context.getInputBitStream(), _context.getOutputBitStream(), _conf, _pool));
    }

    @Override
    protected void runInternal() throws IOException {
        if (_context.isLastChunk()) {
            _endTask = new EndTask(_context, _pool);
            _endTask.addDependency(new Dependency(_otherManagementTask._translateAndWriteTask, EnRunningStates.Completed));
            _pool.pushTask(_endTask, 0);
        } else {
            //_context = new ChunkContext(_input, _output, _conf, _context.threadPool);

            //reset all tasks with new datacontext
            for (int i = 0; i < _threadTaskLists.length; i++) {
                for (ITask task : _threadTaskLists[i]) {
                    if (!(task instanceof ChunkTaskManagement))
                        task.reset(_context);
                }
            }
            //push tasks into pool
            for (int i = 0; i < _threadTaskLists.length; i++) {
                for (ITask task : _threadTaskLists[i]) {
                    _pool.pushTask(task, i);
                }
            }
        }
    }

    public void reset(ChunkContext newChunkContext) {
        super.reset(newChunkContext);
        setState(EnRunningStates.Completed);
    }
}
