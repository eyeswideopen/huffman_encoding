package encoder.parallelization;

import encoder.parallelization.interfaces.IDependency;
import encoder.parallelization.interfaces.ITask;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen, valon
 * Date: 06.09.12
 * Time: 12:20
 *
 * Represents a dependency between 2 ITasks. A Dependency consists of the ITask it depends on
 * and the EnRunningState the task has to have.
 * If ITask a depends on ITask b to be at least running a Dependency Instance is given to a,
 * containing b and EnRunningStates.RUNNING.
 */

public class Dependency implements IDependency {

    private ITask _task;
    private EnRunningStates _state;

    /**
     * The standard Constructor.
     * @param task      the ITask the owner of this instance depends on
     * @param state     the EnRunningStates state the given ITask has to have (at least)
     */
    public Dependency(ITask task, EnRunningStates state) {
        _task = task;
        _state = state;
    }

    public EnRunningStates getState() {
        return _state;
    }

    /**
     * checks if the Dependency is fulfilled
     * @return
     */
    public boolean check() {
        return _task.getState().getNumber() >= _state.getNumber();
    }

    public ITask getTask() {
        return _task;
    }
}
