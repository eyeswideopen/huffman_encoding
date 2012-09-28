package encoder.io;

/**
 * The BitStreamsHelper class provides bit masks for the {@link BitInputStream BitInputStream} and
 * {@link BitOutputStream BitOutputStream} classes. The rhsMasks member (right hand side masks) contains
 * masks filled up with ones from the right hand side (with the rightmost bit being the bit of the lowest order)
 * and the index euqaling the number of ones. The lhsMasks member (left hand side masks) contains masks filled
 * from the left hand side analogue to the rhsMember.
 */
public final class BitStreamsHelper {
    public static final int[] rhsMasks;
    public static final int[] lhsMasks;

    static {
        rhsMasks = new int[33];
        lhsMasks = new int[33];

        int mRhs = 0;
        int mLhs = 0;
        int lBit = 1 << 31;
        for (int i = 0; i < 33; i++) {
            rhsMasks[i] = mRhs;
            lhsMasks[i] = mLhs;
            mRhs <<= 1;
            mRhs |= 1;
            mLhs >>>= 1;
            mLhs |= lBit;
        }
    }
}
