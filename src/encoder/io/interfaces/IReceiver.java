package encoder.io.interfaces;


import encoder.io.BitInputStream;

import java.io.IOException;

/**
 * A receiver implementation capsules whatever input stream is to be fed to the
 * {@link encoder.processing.Processor processor}. The idea is to decouple the input method
 * from the execution logic, so it is possible to change the input method i.e. from reading local files
 * to receiving data via a TCP stream or from a database without modifying existing code but instead
 * just implementing a new receiver.
 */
public interface IReceiver {
    public BitInputStream getInputStream() throws IOException;
}
