package encoder.parallelization.tasks;

import encoder.parallelization.ChunkContext;
import encoder.parallelization.Dependency;
import encoder.processing.interfaces.IHuffmanWord;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen
 * Date: 11.09.12
 * Time: 14:03
 * The HeaderTasks writes the HeaderInformation out to the BitOutputStream.
 * This header is used for the decompressing.
 */
public class HeaderTask extends TaskBase {

    /**
     * Basic Constructor for HeaderTask
     *
     * @param context    Object with data to work with
     * @param dependency Adds a dependency for this task
     */
    public HeaderTask(ChunkContext context, Dependency dependency) {
        super(context);
        addDependency(dependency);
    }

    @Override
    protected void runInternal() throws IOException {
        //generation of header 1
        _context.getOutputBitStream().writeBits(
                _context.getHeaderInfo().getLongestCodeInfoInBits(),
                32);    //maximum huffmancode length
        _context.getOutputBitStream().writeBits(
                _context.getHeaderInfo().getLargestCodeGroupInBits(),
                32);   //maximum groupnumber
        _context.getOutputBitStream().writeBits(
                _context.getHeaderInfo().getOriginalWordSizeInBits(),
                32);   //wordsize in current chunk
        _context.getOutputBitStream().writeBits(
                _context.getHeaderInfo().getNumberOfGroups(),
                32);           //number of groups in current chunk
        _context.getOutputBitStream().writeBits(
                _context.getHeaderInfo().getChunkSize() |
                (_context.isLastChunk()? 1<<31: 0),
                32);                //chunksize

        //generation of header 2 for each group
        for (int i = _context.getHeaderInfo().getFirstLevel(); i <= _context.getHeaderInfo().getLastLevel(); i++) {
            //huffman-wordlength of current group sent in x bits required for huffman maxlength
            _context.getOutputBitStream().writeBits(
                    i,
                    _context.getHeaderInfo().getLongestCodeInfoInBits());
            //number of tupel following until next header2
            _context.getOutputBitStream().writeBits(
                    _context.getHeaderInfo().getGroupSizeAtIndex(i),
                    _context.getHeaderInfo().getLargestCodeGroupInBits());

            //tupel of current group
            for (IHuffmanWord word : _context.getHeaderInfo().getWordsPerLevel(i)) {
                //huffmancode of current word
                _context.getOutputBitStream().writeBits(
                        word.getOutputWord()[0],
                        word.getRelevantOutputBits() % 32);
                for (int c = 1; c < word.getOutputWord().length; c++) {
                    _context.getOutputBitStream().writeBits(
                            word.getOutputWord()[c],
                            32);
                }
                //original bits of current word
                _context.getOutputBitStream().writeBits(
                        word.getInputWord(),
                        _context.getHeaderInfo().getWordSize());
            }
        }
    }
}
