package encoder.io.interfaces;


import encoder.io.BitOutputStream;

import java.io.IOException;

/**
 * A sender implementation capsules {@link BitOutputStream bit output stream} written to by the
 * {@link encoder.processing.Processor processor}. The idea is to decouple the output method
 * from the execution logic, so it is possible to change the output method i.e. from writing to local files
 * to sending data via a TCP stream or writing to a database without modifying existing code but instead
 * just implementing a new sender.
 */
public interface ISender {
    public BitOutputStream getOutputStream() throws IOException;
}
