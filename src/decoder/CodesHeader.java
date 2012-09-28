package decoder;


import encoder.io.BitInputStream;

import java.io.IOException;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/1/12
 * © Jacob Schlesinger 2012
 */
public class CodesHeader {
    public final int CodeLength;
    public final int NumberOfCodes;

    private CodesHeader(int codeLength, int numberOfCodes) {
        CodeLength = codeLength;
        NumberOfCodes = numberOfCodes;
    }

    public static CodesHeader read(BitInputStream bs, ChunkHeader chunkHeader) throws IOException {
        int codeLength, numberOfCodes;
        codeLength = bs.readBits(chunkHeader.MaxCodeLength);
        numberOfCodes = bs.readBits(chunkHeader.MaxGroupNumber);

        return new CodesHeader(codeLength, numberOfCodes);
    }
}
