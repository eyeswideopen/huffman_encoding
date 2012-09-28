package encoder.parallelization.interfaces;

import encoder.parallelization.EnRunningStates;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen
 * Date: 06.09.12
 * Time: 12:18
 * To change this template use File | Settings | File Templates.
 */
public interface IDependency {

    public EnRunningStates getState();

    public boolean check();

    public ITask getTask();

}
