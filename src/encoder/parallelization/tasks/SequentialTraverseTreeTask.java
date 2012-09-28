package encoder.parallelization.tasks;

import encoder.parallelization.ChunkContext;
import encoder.processing.HeaderInfo;
import encoder.processing.Word;
import encoder.processing.interfaces.IHuffmanNode;
import encoder.processing.interfaces.IHuffmanWord;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 11.09.12
 * Time: 16:05
        */
public class SequentialTraverseTreeTask extends TaskBase {

    private List<TraverseTreeTask> _branchTasks;

    /**
     * Basic Constructor for TraverseTreeInitTask
     *
     * @param context     Object with data to work with
     */
    public SequentialTraverseTreeTask(ChunkContext context) {
        super(context);
    }

    //Start recursive method call
    @Override
    protected void runInternal() throws IOException {
        traverseTree(
                _context.getRootNode(),
                new int[1],
                0, _context.getHeaderInfo());
    }

    /**
     * Generate a Hoffman Code by a given tree. The tree is represented by the root node.
     * The method will iterate over the whole tree and will attach a new code based on the depth of the node.
     * Nodes on the left side will be expanded by 0.
     * Nodes on the right side will be expanded by 1.
     *
     * @param node  IHuffmanNode parent node
     * @param word  int[] actual hoffman code, if node is a leaf code will be attached
     * @param depth int layer of the node
     */
    public void traverseTree(IHuffmanNode node, int[] word, int depth, HeaderInfo headerInfo) {
        //empty node
        if (node == null)
            return;

        //Node is leaf, attach code
        if (node.isLeaf()) {
            ((Word) node.getWord()).setOutputParam(
                    word,
                    depth);

            updateHeaderInfoForLeaf(
                    node.getWord(),
                    depth,
                    headerInfo);

            //Node is no leaf, recursive method call of @see generateHuffmanCode, to generate hoffman code for the branches
        } else {
            //go to next level of the tree
            traverseTree(
                    node.getLeft(),
                    shiftWord(
                            word.clone(),
                            depth,
                            0),
                    depth + 1, headerInfo);
            traverseTree(node.getRight(),
                    shiftWord(
                            word.clone(),
                            depth,
                            1),
                    depth + 1
                    , headerInfo);
        }


    }

    /**
     * Operation to generate a new Huffman Code
     *
     * @param word  old Huffman Code
     * @param depth
     * @param value digit to add
     * @return new Huffman Code
     */
    private static int[] shiftWord(int[] word, int depth, int value) {
        //Check array size, if it is to small create a bigger array
        if ((depth - 1) / 32 - (depth / 32) < 0) {
            int[] old = word;
            word = new int[word.length + 1];
            for (int i = 0; i < old.length; i++) {
                word[i + 1] = old[i];
            }
        }
        //shift operations to generate new code
        for (int i = 0; i < (depth / 32); i++) {
            word[i] <<= 1;
            word[i] |= word[i + 1] >>> 31;

        }
        word[word.length - 1] <<= 1;
        word[word.length - 1] |= value;
        //return new generated code
        return word;
    }

    /**
     * Update HeaderInformation
     *
     * @param word
     * @param depth
     * @param headerInfo
     */
    private void updateHeaderInfoForLeaf(IHuffmanWord word, int depth, HeaderInfo headerInfo) {
        //get length of output word
        int length = word.getRelevantOutputBits();
        //Check for longest Code
        if (length > headerInfo.getLongestCodeInfoInBits())
            headerInfo.setLongestCodeInfoInBits(length);

        //Increment number of groups
        headerInfo.setNumberOfGroups(headerInfo.getNumberOfGroups() + 1);

        //Count group elements
        while (headerInfo.getGroupSize()
                .size() < length) {
            headerInfo.getGroupSize()
                    .add(0);
        }
        headerInfo.setGroupSizeAtIndex(
                length,
                headerInfo.getGroupSizeAtIndex(length) + 1);
        //set level of highest node
        if (length < headerInfo.getFirstLevel()) {
            headerInfo.setFirstLevel(length);
        }
        //set level of deepest node
        if (length > headerInfo.getLastLevel()) {
            headerInfo.setLastLevel(length);
        }

        //count nodes on one level
        headerInfo.addWordsOnLevel(
                word,
                depth);
    }
}
