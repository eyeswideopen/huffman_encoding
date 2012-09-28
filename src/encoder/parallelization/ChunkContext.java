package encoder.parallelization;

import encoder.configuration.interfaces.IConfiguration;
import encoder.io.BitInputStream;
import encoder.io.BitOutputStream;
import encoder.parallelization.interfaces.IChunkContext;
import encoder.processing.HeaderInfo;
import encoder.processing.interfaces.IHuffmanNode;
import encoder.processing.interfaces.IHuffmanWord;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen, valon
 * Date: 11.09.12
 * Time: 12:43
 *
 * The ChunkContext is the bottom object every huffman task works on, gets its data from and saves its results to.
 * It is also used for inter-task-communication if required.
 * Each chunk has its own ChunkContext. The context will not be saved or reused and persists only during the
 * processing of the single chunk.
 */

public class ChunkContext extends Context implements IChunkContext {
    private IConfiguration _conf;

    //chunk
    private int[] _chunkArray;
    private IHuffmanNode[] _nodeArray;
    private IHuffmanWord[] _wordArray;
    private IHuffmanNode _rootNode;
    private HeaderInfo _headerInfo;
    private volatile int _availableWords;
    private int _wordSize;
    private volatile int _chunkSize;

    //subchunk
    public final int subChunkSize = 512;
    public volatile int readSubChunks;
    public volatile int lastSubChunk = Integer.MAX_VALUE;
    public final Object readCountSync = new Object();
    public volatile boolean countingFinished;


    public final ThreadPool threadPool;

    //identification information
    private static volatile long _idCounter;
    public String _chunkId;

    //streams from and towards file
    private BitInputStream _inputStream;
    private BitOutputStream _outputStream;


    //piped streams for inter-thread data flow
    private PipedOutputStream _pipedOutputStream;
    private PipedInputStream _pipedInputStream;

    //bitstreams encapsulationg the piped streams for easy handling
    private volatile int _bitsInBuffer = 0;
    private volatile boolean _translationFinished;
    public static volatile boolean _lastChunk;

    /**
     * This is standard constructor to be used with ChunkContext.
     *
     * @param inputStream   input BitStream which provides the input data
     * @param outputStream  output BitStream where the resulting compressed data goes to
     * @param conf          the configuration the chunk will be processed with
     * @param threadPool    the ThreadPool within the chunk and all the Tasks get handled
     */
    public ChunkContext(BitInputStream inputStream, BitOutputStream outputStream, IConfiguration conf, ThreadPool threadPool) {
        _conf = conf;
        _inputStream = inputStream;
        _outputStream = outputStream;
        _availableWords = 0;
        _headerInfo = new HeaderInfo(_conf);
        _wordSize = _conf.getWordSize();
        _chunkSize = _conf.getChunkSize();
        _chunkArray = new int[_chunkSize];
        this.threadPool = threadPool;

        synchronized (ChunkContext.class) {
            _chunkId = Long.toString(_idCounter);
            _idCounter++;
        }
    }

    public void setTranslationFinished(boolean translationFinished) {
        _translationFinished = translationFinished;
    }

    public IConfiguration getConfiguration() {
        return _conf;
    }

    public boolean isTranslationFinished() {
        return _translationFinished;
    }

    public static boolean isLastChunk() {
        return _lastChunk;
    }

    public static void setLastChunk(boolean lastChunk) {
        _lastChunk = lastChunk;
    }

    public void incrementBitsInBuffer(int i) {
        _bitsInBuffer += i;
    }

    public int getBitsInBuffer() {
        return _bitsInBuffer;
    }

    public BitInputStream getInputBitStream() {
        return _inputStream;
    }

    public BitOutputStream getOutputBitStream() {
        return _outputStream;
    }

    public int[] getChunkArray() {
        synchronized (_chunkArray) {
            return _chunkArray;
        }
    }

    public void setChunkArray(int[] newChunkArray) {

        synchronized (_chunkArray) {
            _chunkArray = newChunkArray;
        }
    }

    //TODO: Interger wrapper class required?
    public Integer getChunkArrayValue(int pos) {
        if (pos >= _availableWords) {
            return null;
        }
        synchronized (_chunkArray) {
            return _chunkArray[pos];
        }
    }

    public void setChunkArrayValue(int pos, int value) {
        _chunkArray[pos] = value;
    }

    public int getAvailableWords() {
        return _availableWords;
    }

    public void incrementAvailableWords() {
        _availableWords += 1;
    }

    public IHuffmanNode[] getNodeArray() {
        return _nodeArray;
    }

    public void setNodeArray(IHuffmanNode[] nodeArray) {
        _nodeArray = nodeArray;
    }

    public IHuffmanWord[] getWordArray() {
        return _wordArray;
    }

    public void setWordArray(IHuffmanWord[] huffmanWordArray) {
        _wordArray = huffmanWordArray;
    }

    public void setRootNode(IHuffmanNode root) {
        _rootNode = root;
    }

    public IHuffmanNode getRootNode() {
        return _rootNode;
    }

    public HeaderInfo getHeaderInfo() {
        return _headerInfo;
    }

    public void reset() {
        //dummy for reusable chunkContext
    }

    public int getChunkSize() {
        return _chunkSize;
    }

    public void setChunkSize(int newChunksize) {
        _chunkSize = newChunksize;
    }

    public int getWordSize() {
        return _wordSize;
    }
}
