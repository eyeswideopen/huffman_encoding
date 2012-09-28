package encoder.parallelization.tasks;

import encoder.parallelization.ChunkContext;
import encoder.parallelization.interfaces.ITask;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen
 * Date: 06.09.12
 * Time: 16:15
 * Task for testing the implementation.
 * No need for the real algorithm and implementation.
 */
public class WaitingTask extends TaskBase implements ITask {

    public WaitingTask(ChunkContext context) {
        super(context);
    }

    //TestTask just to wait
    @Override
    protected void runInternal() {
        try {
            Thread.sleep(700);
            System.out.println("task #" + getID() + " of Type: " + getTypeName() + " finished on " + Thread.currentThread().getName());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
