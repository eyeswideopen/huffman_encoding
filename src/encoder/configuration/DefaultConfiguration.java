package encoder.configuration;

import encoder.configuration.evaluation.DefaultEvaluator;
import encoder.configuration.evaluation.interfaces.IEvaluator;
import encoder.configuration.interfaces.IConfiguration;
import encoder.parallelization.Schedules.ImprParallelSchedule;
import encoder.parallelization.interfaces.ISchedule;

/**
 * {@inheritDoc}
 *
 * The DefaultConfiguration has hard coded default values.
 */
public class DefaultConfiguration implements IConfiguration {
    /**
     * {@inheritDoc}
     *
     * Default value: 8
     * @return
     */
    public int getWordSize() {
        return 8;
    }

    /**
     * {@inheritDoc}
     *
     * Default value: 4096
     * @return
     */
    public int getChunkSize() {
        return 4096;
    }

    /**
     * {@inheritDoc}
     *
     * Default value: false
     * @return
     */
    public boolean isLoggingEnabled() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * Default value: 2
     * @return
     */
    public int getAvailableHardwareThreads() {
        return 2;
    }

    /**
     * {@inheritDoc}
     *
     * Default value: false
     * @return
     */
    public boolean useAffinityLocks() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * Default value: {@link encoder.configuration.evaluation.DefaultEvaluator} instance
     * @return
     */
    public IEvaluator getEvaluator() {
        return new DefaultEvaluator();
    }

    /**
     * {@inheritDoc}
     *
     * Default value: {@link encoder.parallelization.Schedules.ImprParallelSchedule} instance
     * @return
     */
    public ISchedule getSchedule() {
        return new ImprParallelSchedule();
    }
}
