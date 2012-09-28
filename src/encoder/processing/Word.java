package encoder.processing;

import encoder.processing.interfaces.IHuffmanWord;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 28.08.12
 * Time: 15:20
 *
 * The Word-Class represents a datacontainer for a given inputWord and the calculated and allocated outputWord,
 * that is based on the Huffman-Code.
 */

public class Word implements IHuffmanWord {
    private int _inputWord;
    private int[] _outputWord;
    private int _relevantOutputBits = 0;
    private int _frequency = 0;

    /**
     * Returns the inputWord
     *
     * @return @int
     */
    public int getInputWord() {
        return _inputWord;
    }

    /**
     * Returns the huffman coded outputWord
     *
     * @return @int[]
     */
    public int[] getOutputWord() {
        return _outputWord;
    }

    /**
     * This value represents how much bits are relevant for the huffman-code.
     *
     * @return @int
     */
    public int getRelevantOutputBits() {
        return _relevantOutputBits;
    }

    /**
     * Returns the frequency of this inputWord. How often it is represented in the inputData.
     *
     * @return @int
     */
    public int getFrequency() {
        return _frequency;
    }

    public void incrementFrequency() {
        _frequency++;
    }

    public void setFrequency(int frequency) {
        _frequency = frequency;
    }

    /**
     * Compare a @IHuffmanWord with an other.
     * The comparison is based on the frequency.
     * @param other @IHuffmanWord
     * @return the difference of the frequency of this and the other word.
     */


    /**
     * Set the huffman-code that represents the coded outputWord
     *
     * @param @int[] outputWord
     */
    public void setOutputWord(int[] outputWord) {
        _outputWord = outputWord;
    }

    /**
     * Set how long the calculated huffman-code is.
     *
     * @param @int relevantOutputBits
     */
    public void setRelevantOutputBits(int relevantOutputBits) {
        _relevantOutputBits = relevantOutputBits;
    }

    /**
     * Method to set outputWord and relevantOutputBits.
     *
     * @param outputWord
     * @param relevantOutputBits
     * @see @setRelevantOutputBits and @see @setOutputWord.
     */
    public void setOutputParam(int[] outputWord, int relevantOutputBits) {
        _outputWord = outputWord;
        _relevantOutputBits = relevantOutputBits;
    }

    /**
     * Basic-Constructor to instantiate a word.
     *
     * @param inputWord
     */
    public Word(int inputWord) {
        _inputWord = inputWord;
    }

    /**
     * Constructor to instantiate a word with frequency.
     *
     * @param inputWord
     */
    public Word(int inputWord, int frequency) {
        _frequency = frequency;
        _inputWord = inputWord;
    }
    /*
    public int compareTo(IHuffmanWord other) {
        return _frequency - other.getFrequency();
    }
    */

    public int compareTo(IHuffmanWord o) {
        if (o instanceof IHuffmanWord) {
            IHuffmanWord other = (IHuffmanWord) o;
            return _frequency - other.getFrequency();
        }
        return 0;
    }
}
