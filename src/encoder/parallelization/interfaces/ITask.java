package encoder.parallelization.interfaces;

import encoder.parallelization.ChunkContext;
import encoder.parallelization.EnRunningStates;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen, valon
 * Date: 06.09.12
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */

public interface ITask extends Runnable {

    public void run();

    public String getTypeName();

    public String getID();

    public EnRunningStates getState();

    public void setState(EnRunningStates state);

    public void reset(ChunkContext newChunkContext);

}
