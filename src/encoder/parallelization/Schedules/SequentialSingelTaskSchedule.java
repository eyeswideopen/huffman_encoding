package encoder.parallelization.Schedules;

import encoder.configuration.interfaces.IConfiguration;
import encoder.parallelization.ChunkContext;
import encoder.parallelization.interfaces.ISchedule;
import encoder.parallelization.interfaces.ITask;
import encoder.parallelization.tasks.ChunkTaskManagement;
import encoder.parallelization.tasks.EndTask;
import encoder.parallelization.tasks.SequentialHuffmanAlgorithmTask;
import encoder.parallelization.tasks.TranslateAndWriteTask;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 22.09.12
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public class SequentialSingelTaskSchedule implements ISchedule {
    public TranslateAndWriteTask fillSchedule(ChunkContext context, List<ITask>[] taskLists, IConfiguration conf, ChunkTaskManagement otherManagement) {
        taskLists[0].add(new SequentialHuffmanAlgorithmTask(context));
       taskLists[0].add(new EndTask(context, context.threadPool));
        return null;
    }

    public String getID() {
        return "sots";
    }

    public String getDescription() {
        return "sequential one task schedule: sequential implementation without function splitting into different tasks";
    }
}
