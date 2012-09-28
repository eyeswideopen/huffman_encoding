package encoder.parallelization.tasks;

import encoder.parallelization.ChunkContext;
import encoder.processing.HeaderInfo;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 11.09.12
 * Time: 16:12
 * ProcessHeaderInfoTasks calculates some information based on the steps and information before.
 */
public class ProcessHeaderInfoTask extends TaskBase {

    /**
     * Basic Constructor for ProcessHeaderInfoTask
     *
     * @param context Object with data to work witt
     */
    public ProcessHeaderInfoTask(ChunkContext context) {
        super(context);
    }

    @Override
    protected void runInternal() throws IOException {
        HeaderInfo headerInfo = _context.getHeaderInfo();
        headerInfo.setChunkSize(_context.getChunkSize());
        //do calculation
        headerInfo.postProcess();
    }
}
