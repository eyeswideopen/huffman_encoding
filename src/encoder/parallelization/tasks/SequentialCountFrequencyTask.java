package encoder.parallelization.tasks;

import encoder.parallelization.ChunkContext;
import encoder.parallelization.Dependency;
import encoder.processing.interfaces.IHuffmanWord;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 11.09.12
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 * CountFrequencyTask iterates over the imported chunk array and counts the frequency of each word in it.
 * This Frequency will be used to build the HuffmanTree. Depending on the frequency the less frequent words will be attached to the deepest branches.
 */
public class SequentialCountFrequencyTask extends TaskBase {

    /**
     * Basic Constructor for CountFrequencyTask
     *
     * @param context    Object with data to work with
     * @param dependency Adds a dependency for this task
     */
    public SequentialCountFrequencyTask(ChunkContext context, Dependency dependency) {
        super(context);
        addDependency(dependency);
    }

    /**
     * Basic Constructor for CountFrequencyTask
     *
     * @param context    Object with data to work with
     */
    public SequentialCountFrequencyTask(ChunkContext context) {
        super(context);
    }

    /**
     * @throws java.io.IOException
     */
    @Override
    protected void runInternal() throws IOException {
        int[] chunk = _context.getChunkArray();
        IHuffmanWord[] words = _context.getWordArray();

        for (int i = 0; i < chunk.length; i++) {
            words[chunk[i]].incrementFrequency();
        }
    }
}
