package encoder.processing;

import encoder.configuration.interfaces.IConfiguration;
import encoder.processing.interfaces.IHuffmanWord;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen, valon
 * Date: 06.09.12
 * Time: 13:37
 *
 * HeaderInfo contains all information required to generate a complete header for an huffman chunk.
 * An instance of it gets continuously filled with available data and finnaly passed to the writeHeader ITask.
 */
public class HeaderInfo {
    private IConfiguration _conf;

    private int _longestCodeInfoInBits;
    private int _largestCodeGroupInBits;
    private int _originalWordSizeInBits;
    private int _numberOfGroups;
    private List<Integer> _groupSizes;
    private int _chunkSize;
    private List<List<IHuffmanWord>> _wordsPerLevel;
    private int _firstLevel;
    private int _lastLevel;

    public HeaderInfo(IConfiguration conf) {
        _conf = conf;
        init();
        _groupSizes = Collections.synchronizedList(_groupSizes);
        _wordsPerLevel = Collections.synchronizedList(_wordsPerLevel);
    }

    private void init() {
        _longestCodeInfoInBits = 0;
        _largestCodeGroupInBits = 0;
        _originalWordSizeInBits = _conf.getWordSize();
        _numberOfGroups = 0;
        _groupSizes = new LinkedList<Integer>();
        _chunkSize = _conf.getChunkSize();
        _wordsPerLevel = new LinkedList<List<IHuffmanWord>>();
        _firstLevel = Integer.MAX_VALUE;
        _lastLevel = 0;
    }

    public int getFirstLevel() {
        return _firstLevel;
    }

    public void setFirstLevel(int firstLevel) {
        _firstLevel = firstLevel;
    }

    public int getLastLevel() {
        return _lastLevel;
    }

    public void setLastLevel(int lastLevel) {
        _lastLevel = lastLevel;
    }

    public List<IHuffmanWord> getWordsPerLevel(int level) {
        return _wordsPerLevel.get(level);
    }

    public void addWordsOnLevel(IHuffmanWord word, int level) {
        while (level >= _wordsPerLevel.size()) {
            _wordsPerLevel.add(new LinkedList<IHuffmanWord>());

        }
        _wordsPerLevel.get(level).add(word);
    }

    public int getChunkSize() {
        return _chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        _chunkSize = chunkSize;
    }

    public int getLongestCodeInfoInBits() {
        return _longestCodeInfoInBits;
    }

    public void setLongestCodeInfoInBits(int longestCodeInfoInBits) {
        _longestCodeInfoInBits = longestCodeInfoInBits;
    }

    public int getLargestCodeGroupInBits() {
        return _largestCodeGroupInBits;
    }

    public void setLargestCodeGroupInBits(int largestCodeGroupInBits) {
        _largestCodeGroupInBits = largestCodeGroupInBits;
    }

    public int getOriginalWordSizeInBits() {
        return _originalWordSizeInBits;
    }

    public void setOriginalWordSizeInBits(int originalWordSizeInBits) {
        _originalWordSizeInBits = originalWordSizeInBits;
    }

    public int getNumberOfGroups() {
        return _numberOfGroups;
    }

    public void setNumberOfGroups(int numberOfGroups) {
        _numberOfGroups = numberOfGroups;
    }

    public void setGroupSizeAtIndex(int index, int value) {
        while (index >= _groupSizes.size())
            _groupSizes.add(0);

        _groupSizes.set(index, value);
    }

    public int getGroupSizeAtIndex(int index) {
        if (index >= _groupSizes.size())
            return 0;
        return _groupSizes.get(index);
    }

    public List<Integer> getGroupSize() {
        return _groupSizes;
    }

    public int getWordSize() {
        return _conf.getWordSize();
    }

    public void postProcess() {
        for (int size : _groupSizes) {
            if (size > _largestCodeGroupInBits)
                _largestCodeGroupInBits = size;
        }

        //Transform numbers into bits
        _longestCodeInfoInBits = getBitsRequiredForNumber(_longestCodeInfoInBits);
        _largestCodeGroupInBits = getBitsRequiredForNumber(_largestCodeGroupInBits);
    }

    private static int getBitsRequiredForNumber(int number) {
        int i = 1;
        for (; Math.pow(2, i) <= number; i++) ;
        return i;
    }
}
