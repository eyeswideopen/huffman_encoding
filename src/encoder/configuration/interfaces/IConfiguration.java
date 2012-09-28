package encoder.configuration.interfaces;

import encoder.configuration.evaluation.interfaces.IEvaluator;
import encoder.parallelization.interfaces.ISchedule;

/**
 * The IConfiguration interface defines all elemental settings needed for execution in the
 * {@link encoder.processing.Processor Processor} class.
 */
public interface IConfiguration {

    /**
     * Retrieves the input word size in bits.
     * @return
     */
    int getWordSize();

    /**
     * Retrieves the chunk size in {@link  IConfiguration#getWordSize() words}.
     * @return
     */
    int getChunkSize();

    /**
     * Specifies whether to log execution information or not.
     * @return
     */
    boolean isLoggingEnabled();

    /**
     * Retrieves the configured number of threads to use.
     * @return
     */
    int getAvailableHardwareThreads();

    /**
     * Specifies whether to use affinity locks or not.
     * @return
     */
    boolean useAffinityLocks();

    /**
     * Retrieves an {@link encoder.configuration.evaluation.interfaces.IEvaluator} implementation instance
     * which can be used for evaluating the validity of this configuration instance.
     * @return
     */
    IEvaluator getEvaluator();

    /**
     * Retrieves an {@link encoder.parallelization.interfaces.ISchedule} implementation instance to be used
     * for execution.
     * @return
     */
    ISchedule getSchedule();
}