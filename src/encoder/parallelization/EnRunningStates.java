package encoder.parallelization;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen, valon
 * Date: 06.09.12
 * Time: 12:17
 * Represents a state an ITask is in.
 */
public enum EnRunningStates {
    NotScheduled(0), Scheduled(1), WaitingForDependencies(2), Running(3), Completed(4);

    private int _number;

    private EnRunningStates(int number) {
        _number = number;
    }

    public int getNumber() {
        return _number;
    }
}
