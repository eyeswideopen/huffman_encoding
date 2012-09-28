package decoder;

import encoder.io.BitInputStream;
import encoder.processing.Node;
import encoder.processing.Word;
import encoder.processing.interfaces.IHuffmanNode;
import encoder.processing.interfaces.IHuffmanWord;

import java.io.IOException;
import java.util.LinkedList;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/1/12
 * © Jacob Schlesinger 2012
 */
public class Helper {

    public static LinkedList<IHuffmanWord> readCodes(BitInputStream bs, int codeSize, int wordSize, int numberOfCodes) throws IOException {
        LinkedList<IHuffmanWord> list = new LinkedList<IHuffmanWord>();

        IHuffmanWord word;
        int[] huffmanCode;
        int originalCode;

        for (int i = 0; i < numberOfCodes; i++) {
            huffmanCode = readWord(bs, codeSize);
            originalCode = bs.readBits(wordSize);
            word = new Word(originalCode);
            ((Word) word).setOutputParam(huffmanCode, codeSize);
            list.add(word);
        }

        return list;
    }


    public static int[] readWord(BitInputStream bs, int wordSize) throws IOException {
        int[] word = new int[wordSize / 32 + 1];
        word[0] = bs.readBits(wordSize % 32);
        for (int i = 1, l = wordSize / 32 + 1; i < l; i++)
            word[i] = bs.readBits(32);

        return word;
    }


    public static IHuffmanNode insertIntoTree(IHuffmanNode rootNode, LinkedList<IHuffmanWord> list) {
        rootNode = rootNode == null ? new Node() : rootNode;
        for (IHuffmanWord word : list) {
            IHuffmanNode curNode = rootNode;
            int[] code = word.getOutputWord();
            int relBits = word.getRelevantOutputBits();

            int startBit = relBits % 32 - 1;
            for (int subCode : code) {
                for (int i = startBit; i >= 0; i--) {
                    int bit = (subCode >>> i) & 1;
                    if (bit == 0 && curNode.getLeft() == null)
                        ((Node) curNode).setLeft(new Node());
                    if (bit == 1 && curNode.getRight() == null)
                        ((Node) curNode).setRight(new Node());
                    curNode = bit == 0 ? curNode.getLeft() : curNode.getRight();
                }
                startBit = 31;
            }
            ((Node) curNode).setWord(word);
        }
        return rootNode;
    }

}
