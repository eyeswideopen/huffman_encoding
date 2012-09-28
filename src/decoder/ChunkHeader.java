package decoder;


import encoder.io.BitInputStream;
import encoder.io.BitStreamsHelper;

import java.io.IOException;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/1/12
 * © Jacob Schlesinger 2012
 */
public class ChunkHeader {
    public final int ChunkSize;
    public final int MaxCodeLength;
    public final int MaxGroupNumber;
    public final int NumberOfGroups;
    public final int WordSize;
    public final boolean IsLastChunk;

    private ChunkHeader(int chunkSize, int maxCodeLength, int maxGroupNumber, int wordSize, int numberOfGroups, boolean isLastChunk) {
        ChunkSize = chunkSize;
        MaxCodeLength = maxCodeLength;
        MaxGroupNumber = maxGroupNumber;
        NumberOfGroups = numberOfGroups;
        WordSize = wordSize;
        IsLastChunk = isLastChunk;
    }

    public static ChunkHeader read(BitInputStream bs) throws IOException {
        int chunkSize, maxCodeLength, maxGroupNumber, numberOfGroups, wordSize;
        maxCodeLength = bs.readBits(32);
        maxGroupNumber = bs.readBits(32);
        wordSize = bs.readBits(32);
        numberOfGroups = bs.readBits(32);
        chunkSize = bs.readBits(32);
        boolean lastChunk = (chunkSize & (1<<31))!=0;
        chunkSize = chunkSize & BitStreamsHelper.rhsMasks[31];

        return new ChunkHeader(chunkSize, maxCodeLength, maxGroupNumber, wordSize, numberOfGroups, lastChunk);
    }
}
