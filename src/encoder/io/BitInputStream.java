package encoder.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * BitInputStream capsules an {@link InputStream InputStream} and enables reading of single bits
 * and bit groups of up to 32 bits into integers.
 */
public class BitInputStream extends InputStream {
    private int _cached;
    private int _bits;
    private InputStream _inputStream;
    private boolean _eof;

    public BitInputStream(InputStream inputStream) {
        _inputStream = inputStream;
    }

    /**
     * {@inheritDoc}
     * @return
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        if (_cached >= 8) {
            _cached -= 8;
            return (_bits >>> (_cached)) & BitStreamsHelper.rhsMasks[8];
        }
        _bits <<= 8;
        _bits |= _inputStream.read();
        return read();
    }

    /**
     * Reads n bits from the underlying {@link InputStream InputStream}.
     * @param n
     * @return
     * @throws IOException
     */
    public int readBits(int n) throws IOException {
        while (_cached < n) {
            int r = _inputStream.read();
            if (r < 0) {
                _eof = true;
                break;
            }
            _bits <<= 8;
            _bits |= (r & 0xFF);
            _cached += 8;
        }
        _cached -= n;
        return (_bits >> _cached) & BitStreamsHelper.rhsMasks[n];
    }

    /**
     * Reads 1 bits from the underlying {@link InputStream InputStream}.
     * @return
     * @throws IOException
     */
    public int readBit() throws IOException {
        return readBits(1);
    }

    /**
     * {@inheritDoc}
     * @return
     * @throws IOException
     */
    @Override
    public int available() throws IOException {
        return _cached + (_inputStream.available() * 8);
    }

    /**
     * Returns true if the read pointer is at the end of the file.
     * @return
     */
    public boolean atEndOfFile() {
        return _eof && (_cached <= 0);
    }
}
