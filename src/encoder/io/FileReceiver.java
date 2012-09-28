package encoder.io;

import encoder.io.interfaces.IReceiver;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * {@inheritDoc}
 *
 * User: valon
 * Date: 29.08.12
 *
 * The FileReceiver reads data from the specified input file.
 */
public class FileReceiver implements IReceiver {

    private BitInputStream _bitStream = null;
    private String _fileName;

    /**
     * Basic Constructor for FileReceiver
     *
     * @param filename    Filename to work with
     */
    public FileReceiver(String filename) {
        _fileName = filename;
    }

    /**
     *
     * @return BitInputStream
     * @throws IOException
     */
    public BitInputStream getInputStream() throws IOException {
        if (_bitStream == null) {
            //read File in FileInputStream and capsulate in BufferedInputStream to increase performance
            BufferedInputStream _stream = new BufferedInputStream(new FileInputStream(_fileName));
            _bitStream = new BitInputStream(_stream);
        }
        return _bitStream;
    }
}
