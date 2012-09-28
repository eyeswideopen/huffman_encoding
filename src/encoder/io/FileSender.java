package encoder.io;

import encoder.io.interfaces.ISender;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * {@inheritDoc}
 *
 * User: valon
 * Date: 29.08.12
 *
 * The FileSender writes data to the specified output file.
 */
public class FileSender implements ISender {

    BufferedOutputStream _stream = null;
    BitOutputStream _bitStream;
    private String _fileName;

    /**
     * Basic Constructor for FileSender
     *
     * @param filename    Filename to work with
     */
    public FileSender(String filename) {
        _fileName = filename;
    }

    /**
     *
     * @return BitOutpuStream
     * @throws IOException
     */
    public BitOutputStream getOutputStream() throws IOException {
        //if (_stream == null){
        if (_bitStream == null) {
            //read File in FileOutputStream and capsulate in BufferedOutputStream to increase performance
            _stream = new BufferedOutputStream(new FileOutputStream(_fileName));
            _bitStream = new BitOutputStream(_stream);
        }
        return _bitStream;
    }

}
