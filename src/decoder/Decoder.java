package decoder;

import encoder.io.BitInputStream;
import encoder.io.BitOutputStream;
import encoder.processing.Node;
import encoder.processing.interfaces.IHuffmanNode;

import java.io.*;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/1/12
 * © Jacob Schlesinger 2012
 */
public class Decoder {
    private String _inputFileName;
    private String _outputFileName;
    private FileInputStream _inputStream;
    private FileOutputStream _outputStream;
    private BitOutputStream _bsOut;
    private BitInputStream _bsIn;

    public Decoder(String inputFileName, String outputFileName) {
        _inputFileName = inputFileName;
        _outputFileName = outputFileName;
    }

    public void decode() throws IOException {
        _inputStream = new FileInputStream(_inputFileName);
        _outputStream = new FileOutputStream(_outputFileName);

        _bsOut = new BitOutputStream(new BufferedOutputStream(_outputStream));
        _bsIn = new BitInputStream(new BufferedInputStream(_inputStream));

        try {
            IHuffmanNode rootNode;

            while (!_bsIn.atEndOfFile()) {
                ChunkHeader chunkHeader = ChunkHeader.read(_bsIn);
                //_bsIn.setWordSize(chunkHeader.WordSize);
                //_bsOut.setWordSize(chunkHeader.WordSize);
                rootNode = new Node();


                int groupCounter = chunkHeader.NumberOfGroups;
                for (int i = 0; i < groupCounter; ) {
                    CodesHeader codesHeader = CodesHeader.read(_bsIn, chunkHeader);
                    i += codesHeader.NumberOfCodes;
                    Helper.insertIntoTree(
                            rootNode,
                            Helper.readCodes(
                                    _bsIn,
                                    codesHeader.CodeLength,
                                    chunkHeader.WordSize,
                                    codesHeader.NumberOfCodes));

                }// for

                for (int i = 0; i < chunkHeader.ChunkSize; i++) {
                    IHuffmanNode curNode = rootNode;
                    while (!curNode.isLeaf()) {
                        curNode = _bsIn.readBit() == 0 ? curNode.getLeft() : curNode.getRight();
                    }
                    _bsOut.writeBits(curNode.getWord().getInputWord(), chunkHeader.WordSize);
                }// for
                if(chunkHeader.IsLastChunk)
                    break;
            }// while


        } finally {
            _bsOut.flush();
            _bsOut.close();
            _bsIn.close();
        }

    }

    public static void main(String[] args) {
        //Decoder decoder = new Decoder("/home/jlaw/fh/se2012/src/encoder.io/testdata/koransuren_encoded","/home/jlaw/fh/se2012/src/encoder.io/testdata/koransuren_decoded");
        //Decoder decoder = new Decoder("/home/jlaw/fh/se2012/src/encoder.io/testdata/HDome_encoded","/home/jlaw/fh/se2012/src/encoder.io/testdata/HDome_decoded.bmp");
        Decoder decoder = new Decoder("testdata/testdata/koransuren.enc", "testdata/testdata/koransuren_decoded.txt");

        try {
            decoder.decode();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
