package encoder.tests;

import encoder.processing.Word;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 30.08.12
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class WordTest {
    private Word _word;

    @Before
    public void setUp() {
        _word = new Word(1, 5);
        _word.setOutputWord(new int[]{Integer.MAX_VALUE, 1});
        _word.setRelevantOutputBits(64);
    }

    @Test
    public void testGetInputWord() throws Exception {
        Assert.assertEquals(_word.getInputWord(), 1);
    }

    @Test
    public void testIncrementFrequency() throws Exception {
        Assert.assertEquals(_word.getFrequency(), 5);
        _word.incrementFrequency();
        Assert.assertEquals(_word.getFrequency(), 6);
    }


    @Test
    public void testCompareTo() throws Exception {
        Word compWord = new Word(2, 6);
        Assert.assertEquals(-1, _word.compareTo(compWord));
        Assert.assertEquals(1, compWord.compareTo(_word));
        compWord.setFrequency(10);
        Assert.assertEquals(-5, _word.compareTo(compWord));
        Assert.assertEquals(5, compWord.compareTo(_word));
    }
}
