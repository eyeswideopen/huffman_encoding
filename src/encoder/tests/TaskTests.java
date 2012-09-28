package encoder.tests;

import encoder.configuration.interfaces.IConfiguration;
import encoder.parallelization.ChunkContext;
import encoder.parallelization.ThreadPool;
import encoder.parallelization.tasks.*;
import encoder.processing.Node;
import encoder.processing.Word;
import encoder.processing.interfaces.IHuffmanNode;
import encoder.processing.interfaces.IHuffmanWord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 25.09.12
 * Time: 15:40
 * Test for testing all relevant tasks.
 */
public class TaskTests {
    private static ChunkContext workContext;
    private static ChunkContext compareContext;
    private static IConfiguration testConfig = new TestConfiguration();
    private static ThreadPool pool = new ThreadPool(1, testConfig);

    /**
     * Preparation for following tests
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        compareContext = new ChunkContext(null, null, testConfig, null);
        IHuffmanNode[] testNodeArray = {new Node(new Word(0)), new Node(new Word(1)), new Node(new Word(2)), new Node(new Word(3))};
        IHuffmanWord[] testWordArray = {new Word(0), new Word(1), new Word(2), new Word(3)};
        compareContext.setNodeArray(testNodeArray);
        compareContext.setWordArray(testWordArray);

        workContext = new ChunkContext(null, null, testConfig, pool);
        workContext.setChunkArray(new int[]{0, 2, 2, 1, 3, 2, 0});

    }

    @Test
    public void testInitNodeArray() {

        Thread t1 = new Thread(new InitNodeArrayTask(workContext));
        t1.run();

        for (int i = 0; i < workContext.getNodeArray().length; i++) {
            Assert.assertEquals(compareContext.getNodeArray()[i].getWord().getInputWord(), workContext.getNodeArray()[i].getWord().getInputWord());
        }

    }

    @Test
    public void testInitWordArray() {
        Thread t1 = new Thread(new InitHuffmanArrayTask(workContext));
        t1.run();

        for (int i = 0; i < workContext.getWordArray().length; i++) {
            Assert.assertEquals(compareContext.getWordArray()[i].getInputWord(), workContext.getWordArray()[i].getInputWord());
        }

    }

    @Test
    public void testCountFrequency() {
        Thread t1 = new Thread(new SequentialCountFrequencyTask(workContext));
        t1.run();

        Assert.assertEquals(2, workContext.getWordArray()[0].getFrequency());
        Assert.assertEquals(1, workContext.getWordArray()[1].getFrequency());
        Assert.assertEquals(3, workContext.getWordArray()[2].getFrequency());
        Assert.assertEquals(1, workContext.getWordArray()[3].getFrequency());

    }

    @Test
    public void testBuildTree() {
        Thread t1 = new Thread(new BuildTreeTask(workContext));
        t1.run();

        IHuffmanNode rootNode = workContext.getRootNode();

        //            *
        //          /   \
        //         2    *
        //            /   \
        //           0    *
        //              /   \
        //             1    3

        Assert.assertEquals(2, rootNode.getLeft().getWord().getInputWord());
        Assert.assertEquals(0, rootNode.getRight().getLeft().getWord().getInputWord());
        Assert.assertEquals(1, rootNode.getRight().getRight().getLeft().getWord().getInputWord());
        Assert.assertEquals(3, rootNode.getRight().getRight().getRight().getWord().getInputWord());

    }

    @Test
    public void testTraverseTree() {
        Thread t1 = new Thread(new SequentialTraverseTreeTask(workContext));
        t1.run();


        IHuffmanNode rootNode = workContext.getRootNode();


        Assert.assertEquals(0, rootNode.getLeft().getWord().getOutputWord()[0]);
        Assert.assertEquals(2, rootNode.getRight().getLeft().getWord().getOutputWord()[0]);
        Assert.assertEquals(6, rootNode.getRight().getRight().getLeft().getWord().getOutputWord()[0]);
        Assert.assertEquals(7, rootNode.getRight().getRight().getRight().getWord().getOutputWord()[0]);

    }

    @Test
    public void testHeaderInfo() {
        Thread t1 = new Thread(new ProcessHeaderInfoTask(workContext));
        t1.run();

        Assert.assertEquals(3, workContext.getHeaderInfo().getLastLevel());
        Assert.assertEquals(1, workContext.getHeaderInfo().getFirstLevel());
        Assert.assertEquals(2, workContext.getHeaderInfo().getLargestCodeGroupInBits());
        Assert.assertEquals(2, workContext.getHeaderInfo().getLongestCodeInfoInBits());
        Assert.assertEquals(4, workContext.getHeaderInfo().getNumberOfGroups());
        Assert.assertEquals(1, workContext.getHeaderInfo().getWordsPerLevel(1).size());
        Assert.assertEquals(1, workContext.getHeaderInfo().getWordsPerLevel(2).size());
        Assert.assertEquals(2, workContext.getHeaderInfo().getWordsPerLevel(3).size());

    }
}
