package encoder.parallelization.interfaces;

import encoder.io.BitInputStream;
import encoder.io.BitOutputStream;
import encoder.processing.HeaderInfo;
import encoder.processing.interfaces.IHuffmanNode;
import encoder.processing.interfaces.IHuffmanWord;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen, valon
 * Date: 11.09.12
 * Time: 12:29
 * To change this template use File | Settings | File Templates.
 */
public interface IChunkContext {

    public BitInputStream getInputBitStream();

    public BitOutputStream getOutputBitStream();


    public int[] getChunkArray();

    public void setChunkArrayValue(int pos, int value);

    public Integer getChunkArrayValue(int pos);

    public int getAvailableWords();

    public void incrementAvailableWords();

    public IHuffmanNode[] getNodeArray();

    public void setNodeArray(IHuffmanNode[] nodeArray);

    public IHuffmanWord[] getWordArray();

    public void setWordArray(IHuffmanWord[] huffmanWordArray);

    public void setRootNode(IHuffmanNode root);

    public IHuffmanNode getRootNode();

    public HeaderInfo getHeaderInfo();

    public void reset();


}
