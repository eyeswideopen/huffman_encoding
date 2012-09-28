package encoder.parallelization.tasks;

import encoder.parallelization.ChunkContext;
import encoder.parallelization.Dependency;
import encoder.parallelization.EnRunningStates;
import encoder.parallelization.ThreadPool;
import encoder.parallelization.interfaces.ITask;
import helper.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen, valon
 * Date: 06.09.12
 * Time: 11:20
 *
 * The abstact base for each ITask to be processed within a ThreadPool worker thread.
 * Basic components of an ITask are:
 *      - an EnRunningStates state. e.g. EnRunningStates.COMPLETED
 *              represents the current state of the ITask. This is used to maintain the dependencies between
 *              several ITasks and the wait-notify mechanism
 *      - (optional) Dependencies. Representing the dependence on an other ITasks state.
 *      - Context the ITasks gets its input from and saves its results to.
 *
 * The lifetime of an ITask:
 *      - initialization upon an given context
 *          state = not scheduled
 *      - ITask gets pushed to a queue i ThreadPool
 *          state = scheduled
 *      - worker thread pulls ITask out of the queue and starts processing it
 *          beforeRun() gets called:
 *              state = waiting for dependencies
 *              all the Dependency objects get checked.
 *              worker thread waits until all of them are marked as fulfilled or a notifyAll() causes him to recheck them
 *              state = running
 *          runInternal() gets called:
 *              here is the task specific computation. All the work you expect your ITask to do should be put in here
 *          afterRun() gets called:
 *              state = completed
 *
 * whenever the ITasks changes its state all other worker threads receive an notifyAll() to re-check their dependencies.
 *
 * the reset() provides functionality to reuse your ITask objects to prevent unnecessary object creation and destruction.
 *
 */

public abstract class TaskBase implements ITask {

    private volatile EnRunningStates _state;
    private Dependency[] _dependencies;
    private boolean[] _fulfilledDependencies;
    private String _typeName;
    private volatile String _Id;
    private volatile static long _IdCounter = 0;
    private ThreadPool _threadPool;
    private boolean _isLoggingEnabled;
    protected ChunkContext _context;

    public TaskBase(ChunkContext context) {
        _context = context;
        _state = EnRunningStates.NotScheduled;
        _dependencies = new Dependency[0];
        _threadPool = context.threadPool;
        _isLoggingEnabled = context.getConfiguration().isLoggingEnabled();
        synchronized (TaskBase.class) {
            _Id = Long.toString(_IdCounter);
            _IdCounter++;
        }
    }

    /**
     * the basic run method which gets called by the worker thread processing the current ITask
     */
    public void run() {
        beforeRun();

        try {
            runInternal();
        } catch (IOException e) {
            e.printStackTrace();
        }

        afterRun();
    }

    /**
     * Used to log information if logging gis enabled for current job
     */
    private void logInformation() {
        if (_isLoggingEnabled) {
            try {
                Logger.getInstance().log("{" + Thread.currentThread().getId() + "}" + "[" + System.nanoTime() + "]"
                        + getTypeName() + "@" + _context._chunkId + "-" + getID() + ":" + _state.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * when processing starts, this method gets called initially. All the dependence overhead is handled here:
     *      - Changes state to waitingForDependencies
     *      - checks the dependencies
     *      - waits if dependencies are not fulfilled and checks again when wakes up.
     *      - sets the state to running and
     *      - returns to enable the execution of runInternal()
     */
    protected void beforeRun() {
        _state = EnRunningStates.WaitingForDependencies;
        logInformation();
        _threadPool.notifyWaiting();

        boolean fulfilled = false;
        if (_fulfilledDependencies == null || _fulfilledDependencies.length != _dependencies.length) {
            _fulfilledDependencies = new boolean[_dependencies.length];
        } else {
            Arrays.fill(_fulfilledDependencies, false);
        }

        while (!fulfilled) {
            fulfilled = true;
            for (int i = 0, l = _dependencies.length; i < l; i++) {
                if (!_fulfilledDependencies[i]) {
                    _fulfilledDependencies[i] = _dependencies[i].check();
                    if (!(fulfilled &= _fulfilledDependencies[i])) {
                        continue;
                    }
                }
            }
            if (!fulfilled) {
                try {
                    synchronized (_threadPool) {
                        //To Avoid RaceConditions between Dependencies and Notfiy-Mechanism
                        _threadPool.wait(50);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        _state = EnRunningStates.Running;
        logInformation();
        _threadPool.notifyWaiting();
    }

    /**
     * The main method used for the ITasks computation purpose.
     * This method should be overridden with the working part of the ITask.
     * All the management overhead is capsuled in beforeRun() and afterRun()
     *
     * @throws IOException      because i/o operations are handled within several ITasks
     */
    protected abstract void runInternal() throws IOException;

    protected void afterRun() {
        _state = EnRunningStates.Completed;
        logInformation();
        _threadPool.notifyWaiting();
    }

    /**
     * Adds a new Dependency to the ITask.
     * Dependencies are handled in the beforeRun().
     *
     * @param newDependency     Dependency representing the dependence on another ITasks state.
     */
    public void addDependency(Dependency newDependency) {
        Dependency[] newArray = new Dependency[_dependencies.length + 1];
        for (int i = 0, l = _dependencies.length; i < l; i++) {
            newArray[i] = _dependencies[i];
        }
        newArray[_dependencies.length] = newDependency;
        _dependencies = newArray;
    }

    /**
     * Used to add several Dependencies in form of a List<Dependency>.
     *
     * @param newList       the list containing the Dependencies
     */
    public void addDependencyList(List<Dependency> newList) {
        for (Dependency dependency : newList) {
            addDependency(dependency);
        }
    }

    public String getTypeName() {
        if (_typeName == null) {
            _typeName = getClass().getSimpleName();
        }
        return _typeName;
    }

    public String getID() {
        return _Id;
    }

    protected void setTypeName(String name) {
        _typeName = name;
    }

    protected void setID(String id) {
        _Id = id;
    }

    /**
     * @return      the current EnRunningState of the ITask.
     */
    public EnRunningStates getState() {
        return _state;
    }

    public void setState(EnRunningStates state) {
        _state = state;
    }

    public void reset(ChunkContext newChunkContext) {
        setState(EnRunningStates.NotScheduled);
        synchronized (TaskBase.class) {
            _Id = Long.toString(_IdCounter);
            _IdCounter++;
        }
        _context = newChunkContext;
    }
}
