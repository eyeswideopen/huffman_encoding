package encoder.tests;

import encoder.configuration.evaluation.interfaces.IEvaluator;
import encoder.configuration.interfaces.IConfiguration;
import encoder.parallelization.interfaces.ISchedule;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 25.09.12
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
public class TestConfiguration implements IConfiguration {
    public int getWordSize() {
        return 2;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getChunkSize() {
        return 5000;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isLoggingEnabled() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getAvailableHardwareThreads() {
        return 1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean useAffinityLocks() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IEvaluator getEvaluator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ISchedule getSchedule() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
