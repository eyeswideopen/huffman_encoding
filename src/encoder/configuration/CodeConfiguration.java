package encoder.configuration;

import encoder.configuration.evaluation.DefaultEvaluator;
import encoder.configuration.evaluation.interfaces.IEvaluator;
import encoder.configuration.interfaces.IConfiguration;
import encoder.parallelization.interfaces.ISchedule;

/**
 * {@inheritDoc}
 *
 * Furthermore the CodeConfiguration implements setter methods to create a configuration via code.
 */
public class CodeConfiguration implements IConfiguration {
    private IConfiguration _baseConf;

    private Integer _wordSize;
    private Integer _chunkSize;
    private Boolean _isLoggingEnabled;
    private Integer _availableHardwareThreads;
    private Boolean _useAffinityLocks;
    private ISchedule _schedule;

    /**
     * Creates a new configuration object taking its values either from the base configuration object or from the values
     * set via this instances setter methods. If the base configuration parameter
     * is set to null a new {@link DefaultConfiguration} instance is created.
     * @param baseConf
     */
    public CodeConfiguration(IConfiguration baseConf) {
        if (baseConf == null)
            _baseConf = new DefaultConfiguration();
        else
            _baseConf = baseConf;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public int getWordSize() {
        if (_wordSize == null)
            _wordSize = _baseConf.getWordSize();
        return _wordSize;
    }

    /**
     *
     * @param wordSize
     */
    public void setWordSize(int wordSize) {
        _wordSize = wordSize;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public int getChunkSize() {
        if (_chunkSize == null)
            _chunkSize = _baseConf.getChunkSize();
        return _chunkSize;
    }

    /**
     * Sets the chunkSize in {@link encoder.configuration.DefaultConfiguration#getWordSize() words}.
     * @param chunkSize
     */
    public void setChunkSize(int chunkSize) {
        _chunkSize = chunkSize;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public boolean isLoggingEnabled() {
        if (_isLoggingEnabled)
            _isLoggingEnabled = _baseConf.isLoggingEnabled();
        return _isLoggingEnabled;
    }


    /**
     * Specifies whether to log execution information or not.
     * @param enableLogging
     */
    public void setLoggingEnabled(boolean enableLogging) {
        _isLoggingEnabled = enableLogging;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public int getAvailableHardwareThreads() {
        if (_availableHardwareThreads == null)
            _availableHardwareThreads = _baseConf.getAvailableHardwareThreads();
        return _availableHardwareThreads;
    }

    /**
     * Sets the number of threads available for scheduling.
     * @param availableHardwareThreads
     */
    public void setAvailableHardwareThreads(int availableHardwareThreads) {
        _availableHardwareThreads = availableHardwareThreads;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public boolean useAffinityLocks() {
        if (_useAffinityLocks == null)
            _useAffinityLocks = _baseConf.useAffinityLocks();
        return _useAffinityLocks;
    }

    /**
     * Specifies whether to use affinity locks or not.
     * @param useAffinityLocks
     */
    public void useAffinityLocks(boolean useAffinityLocks) {
        _useAffinityLocks = useAffinityLocks;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public IEvaluator getEvaluator() {
        return new DefaultEvaluator("code configuration");
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public ISchedule getSchedule() {
        if(_schedule == null)
            _schedule = _baseConf.getSchedule();
        return _schedule;
    }

    /**
     * Sets the schedule to be used for execution.
     * @param schedule
     */
    public void setSchedule(ISchedule schedule){
        _schedule = schedule;
    }
}
