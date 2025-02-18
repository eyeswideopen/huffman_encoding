package encoder.parallelization.tasks;

import encoder.io.BitInputStream;
import encoder.parallelization.ChunkContext;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen, valon
 * Date: 11.09.12
 * Time: 13:06
 * ReadChunkTask imports data from BitInputStream and writes it into an array
 */
public class ReadChunkTask extends TaskBase {
    /**
     * Basic Constructor for ReadChunkTask
     *
     * @param context Object with data to work with
     */
    public ReadChunkTask(ChunkContext context) {
        super(context);
    }

    @Override
    protected void runInternal() throws IOException {
        //Get inputStream from context
        BitInputStream inputStream = _context.getInputBitStream();

        //Init params
        int[] chunk = _context.getChunkArray();
        int wordSize = _context.getWordSize();
        int subChunkCounter = 1;
        int chunkIndex = 0;
        int chunkSize = _context.getChunkSize();

        //Iterate over Chunk, while reaching chunkSize or EOF
        for (; chunkIndex < chunkSize && !inputStream.atEndOfFile(); subChunkCounter++) {
            //Read subChunk and write into array
            for (int l = Math.min(subChunkCounter * _context.subChunkSize, chunkSize);
                 chunkIndex < l && !inputStream.atEndOfFile();
                 chunkIndex++) {
                chunk[chunkIndex] = inputStream.readBits(wordSize);
            }
            if(inputStream.atEndOfFile())
                chunkIndex--;
            //set new available subChunks
            synchronized (_context.readCountSync) {
                _context.readSubChunks = subChunkCounter;
            }

            //set chunk size depending on imported data and lastSubChunk flag
            if (chunkIndex == chunkSize || inputStream.atEndOfFile()) {
                _context.setChunkSize(chunkIndex);
                _context.lastSubChunk = subChunkCounter;
            }
            //notify CountFrequencyTask about new available subChunks
            synchronized (_context.readCountSync) {
                _context.readCountSync.notify();
            }
        }

        //imported chunk was smaller then specified chunkSize
        if (chunkIndex < chunkSize) {
            try {
                //Wait for CountFrequencyTask to be finished
                //Copy of array can only be done when counting finished to avoid collisions
                while (!_context.countingFinished) {
                    synchronized (_context.readCountSync) {
                        if (!_context.countingFinished)
                            _context.readCountSync.wait(50);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            //Copy array to new array with correct size
            _context.setChunkArray(Arrays.copyOfRange(_context.getChunkArray(), 0, chunkIndex));
        }
        //EOF reached, set flag
        if (inputStream.atEndOfFile())
            _context.setLastChunk(true);
    }
}
