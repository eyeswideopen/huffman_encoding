package encoder.parallelization.Schedules;

import encoder.configuration.interfaces.IConfiguration;
import encoder.parallelization.ChunkContext;
import encoder.parallelization.Dependency;
import encoder.parallelization.EnRunningStates;
import encoder.parallelization.interfaces.ISchedule;
import encoder.parallelization.interfaces.ITask;
import encoder.parallelization.tasks.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 22.09.12
 * Time: 12:41
 * To change this template use File | Settings | File Templates.
 */
public class SequentialSchedule implements ISchedule {
    public TranslateAndWriteTask fillSchedule(ChunkContext context, List<ITask>[] taskLists, IConfiguration conf, ChunkTaskManagement otherManagement) {
        //ReadChunkTask
        SequentialReadChunkTask readChunkTask = new SequentialReadChunkTask(context);
        taskLists[0].add(readChunkTask);

        //initNodeArrayTask
        InitNodeArrayTask initNodeArray = new InitNodeArrayTask(context);
        taskLists[0].add(initNodeArray);

        //initHuffmanArrayTask
        InitHuffmanArrayTask initHuffmanArrayTask = new InitHuffmanArrayTask(context, new Dependency(initNodeArray, EnRunningStates.Completed));
        taskLists[0].add(initHuffmanArrayTask);

        //CountFrequencyTask
        SequentialCountFrequencyTask countFrequencyTask = new SequentialCountFrequencyTask(context, new Dependency(readChunkTask, EnRunningStates.Completed));
        countFrequencyTask.addDependency(new Dependency(initHuffmanArrayTask, EnRunningStates.Completed));
        taskLists[0].add(countFrequencyTask);

        //BuildTreeTask
        BuildTreeTask buildTreeTask = new BuildTreeTask(context, new Dependency(countFrequencyTask, EnRunningStates.Completed));
        taskLists[0].add(buildTreeTask);

        //TraverseTreeTask
        SequentialTraverseTreeTask traverseTreeTask = new SequentialTraverseTreeTask(context);
        traverseTreeTask.addDependency(new Dependency(buildTreeTask, EnRunningStates.Completed));
        taskLists[0].add(traverseTreeTask);

        //ProcessHeaderInfoTask
        ProcessHeaderInfoTask processHeaderInfoTask = new ProcessHeaderInfoTask(context);
        processHeaderInfoTask.addDependency(new Dependency(traverseTreeTask, EnRunningStates.Completed));
        taskLists[0].add(processHeaderInfoTask);

        //HeaderTask
        HeaderTask headerTask = new HeaderTask(context, new Dependency(processHeaderInfoTask, EnRunningStates.Completed));
        taskLists[0].add(headerTask);

        //TranslateAndWriteTask
        TranslateAndWriteTask _translateAndWriteTask = new TranslateAndWriteTask(context);
        _translateAndWriteTask.addDependency(new Dependency(headerTask, EnRunningStates.Completed));
        taskLists[0].add(_translateAndWriteTask);

        //EndTask
        //_endTask = new EndTask(context,_pool);
        //_endTask.addDependency(new Dependency(_otherManagementTask._translateAndWriteTask, EnRunningStates.Completed));
        //taskLists[0].add(endTask);


        taskLists[0].add(otherManagement);
        return _translateAndWriteTask;
    }

    public String getID() {
        return "ss";
    }

    public String getDescription() {
        return "sequential schedule: schedules the tasks used for parallelization sequentially";
    }
}
