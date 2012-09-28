package encoder.parallelization.interfaces;

import encoder.configuration.interfaces.IConfiguration;
import encoder.parallelization.ChunkContext;
import encoder.parallelization.Context;
import encoder.parallelization.tasks.ChunkTaskManagement;
import encoder.parallelization.tasks.TranslateAndWriteTask;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 22.09.12
 * Time: 12:17
 * To change this template use File | Settings | File Templates.
 */
public interface ISchedule {
    TranslateAndWriteTask fillSchedule(ChunkContext context, List<ITask>[] taskLists, IConfiguration conf, ChunkTaskManagement otherManagement);

    String getID();

    String getDescription();
}
