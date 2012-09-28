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
public class CountFrequencyTask extends TaskBase {

    /**
     * Basic Constructor for CountFrequencyTask
     *
     * @param context    Object with data to work with
     * @param dependency Adds a dependency for this task
     */
    public CountFrequencyTask(ChunkContext context, Dependency dependency) {
        super(context);
        addDependency(dependency);
    }

    /**
     * This method is used to count the words at the imported data. It uses a "on-the-fly" mechanism.
     * It uses a wait and notify mechanism to process subchunks of the whole chunk.
     * Due to that, the data can be already analyzed while still reading data in.
     *
     * @throws IOException
     */
    @Override
    protected void runInternal() throws IOException {
        int[] chunk = _context.getChunkArray();
        IHuffmanWord[] words = _context.getWordArray();

        //Iterate over all SubChunks
        for (int currentSubChunk = 1;
             currentSubChunk <= _context.lastSubChunk;
             currentSubChunk++) {

            //Waiting for next Subchunk to be completed
            while (currentSubChunk > _context.readSubChunks) {
                try {
                    synchronized (_context.readCountSync) {
                        if (currentSubChunk > _context.readSubChunks)
                            //Wait to be notified from ReadChunkTask
                            _context.readCountSync.wait(50);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            //The real counting mechanism, iterate over SubChunk and count frequency
            //The range depends on the number of the subChunk and the chunkSize. The last subChunk could be smaller.
            for (int i = (currentSubChunk - 1) * _context.subChunkSize,
                         //l = Math.min(currentSubChunk * _context.subChunkSize, _context.getChunkSize());
                         l = currentSubChunk ==
                                 _context.lastSubChunk ?
                                 _context.getChunkSize() :
                                 _context.getConfiguration().getChunkSize();
                 i < l; i++) {
                words[chunk[i]].incrementFrequency();
            }
        }
        //Counting is finished
        synchronized (_context.readCountSync) {
            _context.countingFinished = true;
            _context.readCountSync.notify();
        }
    }
}
