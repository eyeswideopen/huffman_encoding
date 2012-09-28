package encoder.parallelization.tasks;

import encoder.parallelization.ChunkContext;
import encoder.processing.HeaderInfo;
import encoder.processing.Node;
import encoder.processing.Word;
import encoder.processing.interfaces.IHuffmanNode;
import encoder.processing.interfaces.IHuffmanWord;
import helper.StopWatch;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 22.09.12
 * Time: 13:21
 * To change this template use File | Settings | File Templates.
 */
public class SequentialHuffmanAlgorithmTask extends TaskBase {
    public SequentialHuffmanAlgorithmTask(ChunkContext context) {
        super(context);
    }

    @Override
    protected void runInternal() throws IOException {
        int[] chunk = new int[_context.getChunkSize()];
        while (!_context.getInputBitStream().atEndOfFile()) {
            int chunkSize = 0;
            for (; chunkSize < chunk.length && (!_context.getInputBitStream().atEndOfFile()); chunkSize++) {
                chunk[chunkSize] = _context.getInputBitStream().readBits(_context.getWordSize());
            }

            // ugly fix for quirky atEndOfFile method
            if(_context.getInputBitStream().atEndOfFile())
                chunkSize--;

            if (chunkSize < _context.getChunkSize())
                chunk = Arrays.copyOfRange(
                        chunk,
                        0,
                        chunkSize);
            processChunk(chunk, _context.getInputBitStream().atEndOfFile());

        }
       _context.getOutputBitStream().close();
       _context.getInputBitStream().close();
    }

    public void processChunk(int[] chunk, boolean isLastChunk) throws IOException {
        IHuffmanNode[] nodeArray = initArray();

        IHuffmanWord[] wordArray = new IHuffmanWord[nodeArray.length];
        for (int i = 0, l = nodeArray.length; i < l; i++)
            wordArray[i] = nodeArray[i].getWord();

        HeaderInfo headerInfo = new HeaderInfo(_context.getConfiguration());

        countFrequencies(chunk, wordArray);

        IHuffmanNode rootNode = buildTree(nodeArray);

        generateHuffmanCode(
                rootNode,
                new int[1],
                0, headerInfo);

        headerInfo.setChunkSize(chunk.length);
        headerInfo.postProcess();
        writeHeader(headerInfo, isLastChunk);

        for (int wordIndex : chunk) {
            IHuffmanWord word = wordArray[wordIndex];
//            _bitStream.putBits(wordArray[word].getOutputWord(), wordArray[word].getRelevantOutputBits());
            _context.getOutputBitStream().writeBits(
                    word.getOutputWord()[0],
                    word.getRelevantOutputBits() % 32);
            for (int c = 1; c < word.getOutputWord().length; c++) {
                _context.getOutputBitStream().writeBits(
                        word.getOutputWord()[c],
                        32);
            }
        }
        //_sender.getOutputStream()
        //       .flush();
    }
    
    public IHuffmanNode[] initArray() {
        int size = (int) Math.pow(
                2.0,
                _context.getWordSize());
        IHuffmanNode[] array = new IHuffmanNode[size];

        for (int i = 0; i < size; i++) {
            array[i] = new Node(new Word(i));
        }
        return array;
    }

    public void countFrequencies(int[] chunk, IHuffmanWord[] wordArray) {
        for (int i = 0; i < chunk.length; i++) {
            wordArray[chunk[i]].incrementFrequency();
        }
    }
    /**
     * Generate a tree based on a given array. The tree will be generated based on the huffman algorithm.
     *
     * @param array IHuffmanNode[] array to generate a tree
     * @return IHuffmanNode root node that represents the tree
     */
    public IHuffmanNode buildTree(IHuffmanNode[] array) {
        //Generate list
        List<IHuffmanNode> list = new LinkedList<IHuffmanNode>();
        for (IHuffmanNode node : array) {
            if (node.getFrequency() != 0) {
                list.add(node);
            }
        }
        //Add all array elements to a priority queue
        //the priority Queue works with the comparedTo Method of the @IHuffmanNode class
        PriorityQueue<IHuffmanNode> pQ = new PriorityQueue<IHuffmanNode>(list);

        //while more then 1 element is in the queue
        while (pQ.size() > 1) {
            //Create new parentNode
            Node parent = new Node(null);
            //add child to parent
            parent.setLeft(pQ.poll());
            parent.setRight(pQ.poll());
            //return parent to Queue
            //two child nodes are now combined in one parent
            pQ.add(parent);
        }

        //Return last node in queue, the root node.
        return pQ.poll();
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
    public void generateHuffmanCode(IHuffmanNode node, int[] word, int depth, HeaderInfo headerInfo) {

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
            generateHuffmanCode(
                    node.getLeft(),
                    shiftWord(
                            word.clone(),
                            depth,
                            0),
                    depth + 1, headerInfo);
            generateHuffmanCode(
                    node.getRight(),
                    shiftWord(
                            word.clone(),
                            depth,
                            1),
                    depth + 1, headerInfo);
        }


    }

    private void updateHeaderInfoForLeaf(IHuffmanWord word, int depth, HeaderInfo headerInfo) {
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

    public void writeHeader(HeaderInfo info, boolean isLastChunk) throws IOException {
        //generation of header 1
        _context.getOutputBitStream().writeBits(
                info.getLongestCodeInfoInBits(),
                32);    //maximum huffmancode length
        _context.getOutputBitStream().writeBits(
                info.getLargestCodeGroupInBits(),
                32);   //maximum groupnumber
        _context.getOutputBitStream().writeBits(
                info.getOriginalWordSizeInBits(),
                32);   //wordsize in current chunk
        _context.getOutputBitStream().writeBits(
                info.getNumberOfGroups(),
                32);           //number of groups in current chunk
        _context.getOutputBitStream().writeBits(
                info.getChunkSize() |
                (isLastChunk?(1<<31):0),
                32);                //chunksize

        //generation of header 2 for each group
        for (int i = info.getFirstLevel(); i <= info.getLastLevel(); i++) {
            //huffman-wordlength of current group sent in x bits required for huffman maxlength
            _context.getOutputBitStream().writeBits(
                    i,
                    info.getLongestCodeInfoInBits());
            //number of tupel following until next header2
            _context.getOutputBitStream().writeBits(
                    info.getGroupSizeAtIndex(i),
                    info.getLargestCodeGroupInBits());
            //tupel of current group
            for (IHuffmanWord word : info.getWordsPerLevel(i)) {
                //huffmancode of current word
                _context.getOutputBitStream().writeBits(
                        word.getOutputWord()[0],
                        word.getRelevantOutputBits() % 32);
                for (int c = 1; c < word.getOutputWord().length; c++) {
                    _context.getOutputBitStream().writeBits(
                            word.getOutputWord()[c],
                            32);
                }
                //original bits of current word
                _context.getOutputBitStream().writeBits(
                        word.getInputWord(),
                        info.getWordSize());
            }

        }
    }




}
