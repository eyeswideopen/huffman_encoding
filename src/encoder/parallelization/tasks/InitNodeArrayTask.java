package encoder.parallelization.tasks;

import encoder.parallelization.ChunkContext;
import encoder.processing.Node;
import encoder.processing.Word;
import encoder.processing.interfaces.IHuffmanNode;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: valon
 * Date: 11.09.12
 * Time: 14:12
 * Init a array with all possible nodes.
 * The quantity depends on the chosen word length.
 * Depending on the word length all possible permutation will be generated.
 */
public class InitNodeArrayTask extends TaskBase {

    /**
     * Basic Constructor for InitNodeArrayTask
     *
     * @param context Object with data to work with
     */
    public InitNodeArrayTask(ChunkContext context) {
        super(context);
    }

    @Override
    protected void runInternal() throws IOException {
        //Calculate size of node array. 2^wordSize
        int size = (int) Math.pow(2.0, _context.getWordSize());
        //Init array with calculated size
        IHuffmanNode[] nodeArray = new IHuffmanNode[size];
        //Initialize nodeArray with new nodes
        for (int i = 0; i < size; i++) {
            //Use i as setter for binary representation of each possbile word
            nodeArray[i] = new Node(new Word(i));
        }
        //Write back generated array
        _context.setNodeArray(nodeArray);
    }
}
