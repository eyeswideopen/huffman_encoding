package encoder.parallelization.Schedules;

import encoder.configuration.interfaces.IConfiguration;
import encoder.parallelization.ChunkContext;
import encoder.parallelization.Dependency;
import encoder.parallelization.EnRunningStates;
import encoder.parallelization.interfaces.ISchedule;
import encoder.parallelization.interfaces.ITask;
import encoder.parallelization.tasks.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 22.09.12
 * Time: 12:16
 *
 **/
public class MirroredParallelSchedule implements ISchedule {


    public TranslateAndWriteTask fillSchedule(ChunkContext context, List<ITask>[] taskLists, IConfiguration conf, ChunkTaskManagement otherManagement) {
        //ReadChunkTask
        ReadChunkTask readChunkTask = new ReadChunkTask(context);
        taskLists[0].add(readChunkTask);

        //initNodeArrayTask
        InitNodeArrayTask initNodeArray = new InitNodeArrayTask(context);
        taskLists[1].add(initNodeArray);

        //initHuffmanArrayTask
        InitHuffmanArrayTask initHuffmanArrayTask = new InitHuffmanArrayTask(context, new Dependency(initNodeArray, EnRunningStates.Completed));
        taskLists[1].add(initHuffmanArrayTask);

        //CountFrequencyTask
        CountFrequencyTask countFrequencyTask = new CountFrequencyTask(context, new Dependency(readChunkTask, EnRunningStates.Running));
        countFrequencyTask.addDependency(new Dependency(initHuffmanArrayTask, EnRunningStates.Completed));
        taskLists[1].add(countFrequencyTask);

        //BuildTreeTask
        BuildTreeTask buildTreeTask = new BuildTreeTask(context, new Dependency(countFrequencyTask, EnRunningStates.Completed));
        taskLists[0].add(buildTreeTask);

        //TraverseTreeTasks
        List<TraverseTreeTask> traverseTreeTasks = Collections.synchronizedList(new LinkedList<TraverseTreeTask>());

        for (int i = conf.getAvailableHardwareThreads(); i > 0; i--) {
            traverseTreeTasks.add(new TraverseTreeTask(context));
        }
        TraverseTreeInitTask traverseTreeInitTask = new TraverseTreeInitTask(context, traverseTreeTasks);
        traverseTreeInitTask.addDependency(new Dependency(buildTreeTask, EnRunningStates.Completed));
        taskLists[0].add(traverseTreeInitTask);

        int tmp = 0;
        List<Dependency> depList = new LinkedList<Dependency>();
        for (TraverseTreeTask task : traverseTreeTasks) {
            depList.add(new Dependency(task, EnRunningStates.Completed));
            task.addDependency(new Dependency(traverseTreeInitTask, EnRunningStates.Completed));
            taskLists[tmp++].add(task);
        }

        //ProcessHeaderInfoTask
        ProcessHeaderInfoTask processHeaderInfoTask = new ProcessHeaderInfoTask(context);
        processHeaderInfoTask.addDependencyList(depList);
        taskLists[0].add(processHeaderInfoTask);

        //HeaderTask
        HeaderTask headerTask = new HeaderTask(context, new Dependency(processHeaderInfoTask, EnRunningStates.Completed));
        taskLists[0].add(headerTask);

        //TranslateAndWriteTask
        TranslateAndWriteTask _translateAndWriteTask = new TranslateAndWriteTask(context);
        _translateAndWriteTask.addDependency(new Dependency(headerTask, EnRunningStates.Completed));
        taskLists[0].add(_translateAndWriteTask);

        taskLists[1].add(otherManagement);
        return _translateAndWriteTask;
    }

    public String getID() {
        return "mps";
    }

    public String getDescription() {
        return "mirrored parallel schedule: uses interleaving for processing of different chunks of data";
    }
}
