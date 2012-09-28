package encoder.processing.interfaces;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 8/28/12
 * © Jacob Schlesinger 2012
 */
public interface IHuffmanNode extends Comparable<IHuffmanNode> {

    public IHuffmanWord getWord();

    public boolean hasWord();

    public IHuffmanNode getLeft();

    public IHuffmanNode getRight();

    public int getFrequency();

    public boolean isLeaf();
}
