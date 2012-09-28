package encoder.parallelization.tasks;

import encoder.parallelization.ChunkContext;
import encoder.processing.HeaderInfo;
import encoder.processing.Word;
import encoder.processing.interfaces.IHuffmanNode;
import encoder.processing.interfaces.IHuffmanWord;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 11.09.12
 * Time: 16:06
 * TraverseTreeTasks traverses through whole tree. Based on the given branch-node of the initTreeTask.
 */
public class TraverseTreeTask extends TaskBase {
    private IHuffmanNode _branchNode;
    private int _depth;
    private int[] _word;

    public TraverseTreeTask(ChunkContext context) {
        super(context);
    }

    @Override
    protected void runInternal() throws IOException {
        if (_branchNode == null)
            return;
        traverseTree(_branchNode, _word, _depth);
    }

    /**
     * Set params for traversal. This params are set by the @TraverseTreeInitTask.
     *
     * @param node  branch node to start
     * @param word  current code word
     * @param depth level of tree
     */
    public void setParams(IHuffmanNode node, int[] word, int depth) {
        synchronized (TraverseTreeTask.class) {
            _branchNode = node;
            _word = word;
            _depth = depth;

        }

    }

    public IHuffmanNode getBranchNode() {
        synchronized (TraverseTreeTask.class) {
            return _branchNode;
        }
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
    public void traverseTree(IHuffmanNode node, int[] word, int depth) {
        //empty node
        if (node == null)
            return;

        //Node is leaf, attach code
        if (node.isLeaf()) {
            ((Word) node.getWord()).setOutputParam(
                    word,
                    depth);

            synchronized (TraverseTreeTask.class) {
                updateHeaderInfoForLeaf(
                        node.getWord(),
                        depth);
            }
            //Node is no leaf, recursive method call of @see generateHuffmanCode, to generate hoffman code for the branches
        } else {
            traverseTree(
                    node.getLeft(),
                    shiftWord(
                            word.clone(),
                            depth,
                            0),
                    depth + 1);
            traverseTree(node.getRight(),
                    shiftWord(
                            word.clone(),
                            depth,
                            1),
                    depth + 1);
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

        for (int i = 0; i < (depth / 32); i++) {
            word[i] <<= 1;
            word[i] |= word[i + 1] >>> 31;

        }
        word[word.length - 1] <<= 1;
        word[word.length - 1] |= value;
        return word;
    }

    private void updateHeaderInfoForLeaf(IHuffmanWord word, int depth) {
        int length = word.getRelevantOutputBits();

        HeaderInfo headerInfo = _context.getHeaderInfo();

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

        if (length < headerInfo.getFirstLevel()) {
            headerInfo.setFirstLevel(length);
        }
        if (length > headerInfo.getLastLevel()) {
            headerInfo.setLastLevel(length);
        }

        headerInfo.addWordsOnLevel(
                word,
                depth);
    }

    /**
     * Reset params at task for reuse
     *
     * @param newChunkContext
     */
    public void reset(ChunkContext newChunkContext) {
        super.reset(newChunkContext);
        _branchNode = null;
        _depth = 0;
        _word = null;

    }
}
