package encoder.parallelization.tasks;

import encoder.io.BitOutputStream;
import encoder.parallelization.ChunkContext;
import encoder.processing.interfaces.IHuffmanWord;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 15.09.12
 * Time: 13:38
 * TranslateAndWriteTask
 * This task is in charge of two things, translate old data with new coded output words and also write the data to the bitOutputStream out.
 */
public class TranslateAndWriteTask extends TaskBase {
    /**
     * Basic Constructor for TranslateAndWriteTask
     *
     * @param context Object with data to work with
     */
    public TranslateAndWriteTask(ChunkContext context) {
        super(context);
        _context = context;

    }

    @Override
    protected void runInternal() throws IOException {
        //Get params
        int[] chunk = _context.getChunkArray();
        IHuffmanWord[] words = _context.getWordArray();
        BitOutputStream out = _context.getOutputBitStream();
        //iterate over given inputData
        for (int wordIndex : chunk) {
            //Get word
            IHuffmanWord word = words[wordIndex];
            //Write out new outputWord into BitStream
            //But the new outputWord can be longer than input words, so a array is used for it
            out.writeBits(
                    word.getOutputWord()[0],
                    word.getRelevantOutputBits() % 32);
            for (int c = 1; c < word.getOutputWord().length; c++) {
                out.writeBits(
                        word.getOutputWord()[c],
                        32);
            }
        }
    }

    /**
     * Reset data object of task
     *
     * @param newChunkContext new data
     */
    public void reset(ChunkContext newChunkContext) {
        super.reset(newChunkContext);
    }
}