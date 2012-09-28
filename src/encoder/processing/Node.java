package encoder.processing;

import encoder.processing.interfaces.IHuffmanNode;
import encoder.processing.interfaces.IHuffmanWord;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen, valon
 * Date: 06.09.12
 * Time: 13:37
 *
 * The node class is a datacontainer class for the representation of the Huffman-Tree.
 * One node can own a right or left child, but it also can be a Leaf-Node with no childs. In that case it will contain a @IHuffmanWord to represent a word.
 * Based on that spanned tree, the Huffman Code will be generated.
 */
public class Node implements IHuffmanNode {
    private IHuffmanWord _word;
    private IHuffmanNode _left;
    private IHuffmanNode _right;

    /**
     * Basic constructor.
     */
    public Node() {
    }

    /**
     * Creates a Node that contains a @IHuffmanWord.
     *
     * @param word      given huffman word
     */
    public Node(IHuffmanWord word) {
        _word = word;
    }

    /**
     * Returns the containing @IHuffmanWord. It will return null if no word is represented by this node.
     *
     * @return @IHuffmanWord
     */
    public IHuffmanWord getWord() {
        return _word;
    }

    /**
     * Returns true or false, whether the node is leafnode and contains a word, or not.
     *
     * @return boolean
     */
    public boolean hasWord() {
        return _word != null;
    }

    /**
     * @return true if node is a leaf
     */
    public boolean isLeaf() {
        return _left == null && _right == null;
    }

    /**
     * Returns the node on the left side of this node. If no left node is existent it will be null returned.
     *
     * @return @IHuffmanNode
     */
    public IHuffmanNode getLeft() {
        return _left;
    }

    /**
     * Returns the node on the right side of this node. If no right node is existent it will be null returned.
     *
     * @return @IHuffmanNode
     */
    public IHuffmanNode getRight() {
        return _right;

    }

    /**
     * Sets a right child node for this node.
     *
     * @param @IHuffmanNode node to set on the right side
     */
    public void setRight(IHuffmanNode right) {
        _right = right;

    }

    /**
     * Sets a left child node for this node.
     *
     * @param @IHuffmanNode node to set on the left side
     */
    public void setLeft(IHuffmanNode left) {
        _left = left;
    }

    public void setWord(IHuffmanWord word) {
        _word = word;
    }

    /**
     * Returns a frequency value.
     * Is this node a leaf node and contains a word, it will return the frequency of this word.
     * On the other case, if that node is no leaf it will sum the frequency of the child.
     *
     * @return int Frequency Value
     */
    public int getFrequency() {
        return hasWord() ? _word.getFrequency() : (_left.getFrequency() + _right.getFrequency());
    }

    /**
     * Method to compare a node with an other node. The comparison is based on the frequency.
     *
     * @param @IHuffmanNode to compare with this node
     * @return difference between the frequency of the given two nodes.
     */
    public int compareTo(IHuffmanNode other) {
        return getFrequency() - other.getFrequency();
    }
}
