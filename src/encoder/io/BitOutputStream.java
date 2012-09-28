package encoder.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Capsules an {@link OutputStream OutputStream} and enables
 * writing of single bits or bit groups of up to 32 bits to it.
 */
public class BitOutputStream extends OutputStream {
    private int _bits;
    private int _cached;
    private OutputStream _outputStream;

    public BitOutputStream(OutputStream outputStream) {
        _outputStream = outputStream;
    }

    /**
     * {@inheritDoc}
     * @param b
     * @throws IOException
     */
    @Override
    public void write(int b) throws IOException {
        writeBits(
                b,
                8);
    }

    /**
     * Writes n bits to the underlying {@link OutputStream OutputStream}.
     * @param bits
     * @param n
     * @throws IOException
     */
    public void writeBits(int bits, int n) throws IOException {
        bits <<= 32 - n;
        for (int c = 0; c < n; c++) {
            writeBit(bits >>> 31);
            bits <<= 1;
        }
    }

    /**
     * Writes 1 bits to the underlying {@link OutputStream OutputStream}.
     * @param i
     * @throws IOException
     */
    public void writeBit(int i) throws IOException {
        _bits <<= 1;
        _bits |= i;
        _cached++;
        if (_cached == 8) {
            _outputStream.write(_bits);
            _bits = 0;
            _cached = 0;
        }
    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public void flush() throws IOException {
        if (_cached != 0) {
            _bits &= BitStreamsHelper.rhsMasks[_cached];
            _bits <<= 8 - _cached;
            _outputStream.write(_bits);
            _cached = 0;
        }
        _outputStream.flush();
    }

    /**
     * {@inheritDoc}
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        super.close();
        _outputStream.close();
    }
}
