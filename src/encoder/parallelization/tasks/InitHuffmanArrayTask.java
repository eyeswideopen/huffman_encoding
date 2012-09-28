package encoder.parallelization.tasks;

import encoder.parallelization.ChunkContext;
import encoder.parallelization.Dependency;
import encoder.processing.interfaces.IHuffmanNode;
import encoder.processing.interfaces.IHuffmanWord;

/**
 * Created with IntelliJ IDEA.
 * User: valon
 * Date: 11.09.12
 * Time: 14:32
 * The InitHuffmanArray initializes a array with huffman words, based on the initialized NodeArray
 */
public class InitHuffmanArrayTask extends TaskBase {
    /**
     * Basic Constructor for InitHuffmanArrayTask
     *
     * @param context    Object with data to work with
     * @param dependency Adds a dependency for this task
     */
    public InitHuffmanArrayTask(ChunkContext context, Dependency dependency) {
        super(context);
        addDependency(dependency);
    }
    /**
     * Basic Constructor for InitHuffmanArrayTask
     *
     * @param context    Object with data to work with
     */
    public InitHuffmanArrayTask(ChunkContext context) {
        super(context);
    }

    @Override
    protected void runInternal() {
        //Get NodeArray from context
        IHuffmanNode[] nodeArray = _context.getNodeArray();
        //Initialize wordArray of same size.
        IHuffmanWord[] wordArray = new IHuffmanWord[nodeArray.length];
        //Extract each word from nodeArray and put it into array.
        for (int i = 0, l = nodeArray.length; i < l; i++) {
            wordArray[i] = nodeArray[i].getWord();
        }
        //Write back new array
        _context.setWordArray(wordArray);
    }
}
