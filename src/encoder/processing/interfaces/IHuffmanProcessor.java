package encoder.processing.interfaces;

import encoder.io.FileReceiver;
import encoder.io.FileSender;
import encoder.processing.HeaderInfo;

import java.io.IOException;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 8/28/12
 * © Jacob Schlesinger 2012
 */
public interface IHuffmanProcessor {

    public void setReceiver(FileReceiver receiver);

    public FileReceiver getReceiver();

    public void setSender(FileSender sender);

    public FileSender getSender();

    public void process() throws IOException;
}
