package encoder.parallelization.tasks;

import encoder.parallelization.ChunkContext;
import encoder.parallelization.ThreadPool;

import java.io.IOException;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/14/12
 * © Jacob Schlesinger 2012
 */
public class EndTask extends TaskBase {
    private ThreadPool _threadPool;

    public EndTask(ChunkContext chunkContext, ThreadPool threadPool) {
        super(chunkContext);
        _threadPool = threadPool;
    }

    @Override
    protected void runInternal() throws IOException {
        //if(_chunkContext.isLastChunk())
        try {
            _context.getOutputBitStream().close();
            _context.getInputBitStream().close();
            _threadPool.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
