package encoder.configuration;

import encoder.configuration.evaluation.DefaultEvaluator;
import encoder.configuration.evaluation.interfaces.IEvaluator;
import encoder.configuration.interfaces.IConfiguration;
import encoder.parallelization.ScheduleFactory;
import encoder.parallelization.interfaces.ISchedule;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * {@inheritDoc}
 *
 * The FileConfiguration can retrieve settings from the specified properties file.
 */
public class FileConfiguration implements IConfiguration {


    private Integer _wordSize;
    private Integer _chunkSize;
    private Boolean _isLoggingEnabled;
    private Integer _availableHardwareThreads;
    private Boolean _useAffinityLocks;
    private ISchedule _schedule;
    private ScheduleFactory _scheduleFactory;

    private Properties _properties = null;

    private IConfiguration _baseConf;

    private String _configFile;

    /**
     * Creates a new configuration object retrieving it's information from the specified
     * configuration file or from the base configuration. If the base configuration parameter
     * is set to null a new {@link DefaultConfiguration} instance is created.
     * @param baseConf the base configuration to be used for values not provided in the configuration file
     * @param configFile qualified path of configuration file
     */
    public FileConfiguration(IConfiguration baseConf, String configFile) {
        if (baseConf == null)
            _baseConf = new DefaultConfiguration();
        else
            _baseConf = baseConf;

        _configFile = configFile;

        _scheduleFactory = ScheduleFactory.getInstance();
    }

    private Properties getProperties() {
        if (_properties == null) {
            _properties = new Properties();
            try {
                BufferedInputStream stream = new BufferedInputStream(new FileInputStream(_configFile));
                _properties.load(stream);
                stream.close();
            } catch (IOException e) {
                System.out.println("no configuration file found at '" + _configFile + "'");
                _properties = new Properties();
            }
        }
        return _properties;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public int getWordSize() {
        if (_wordSize == null) {
            if (getProperties().containsKey("wordSize"))
                _wordSize = Integer.parseInt(getProperties().getProperty("wordSize"));
            else
                _wordSize = _baseConf.getWordSize();
        }
        return _wordSize;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public int getChunkSize() {
        if (_chunkSize == null) {
            if (getProperties().containsKey("chunkSize"))
                _chunkSize = Integer.parseInt(getProperties().getProperty("chunkSize"));
            else
                _chunkSize = _baseConf.getChunkSize();
        }
        return _chunkSize;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public boolean isLoggingEnabled() {
        if (_isLoggingEnabled == null) {
            if (getProperties().containsKey("enableLogging"))
                _isLoggingEnabled = Boolean.parseBoolean(getProperties().getProperty("enableLogging"));
            else
                _isLoggingEnabled = _baseConf.isLoggingEnabled();
        }
        return _isLoggingEnabled;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public int getAvailableHardwareThreads() {
        if (_availableHardwareThreads == null) {
            if (getProperties().containsKey("hardwareThreads"))
                _availableHardwareThreads = Integer.parseInt(getProperties().getProperty("hardwareThreads"));
            else
                _availableHardwareThreads = _baseConf.getAvailableHardwareThreads();
        }
        return _availableHardwareThreads;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public boolean useAffinityLocks() {
        if (_useAffinityLocks == null) {
            if (getProperties().contains("useAffinityLocks"))
                _useAffinityLocks = Boolean.parseBoolean(getProperties().getProperty("enableAffinityLocks"));
            else
                _useAffinityLocks = _baseConf.useAffinityLocks();
        }
        return _useAffinityLocks;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public IEvaluator getEvaluator() {
        return new DefaultEvaluator("file configuration: "+_configFile);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public ISchedule getSchedule() {
        if(_schedule == null){
            if(_properties.containsKey("schedule"))
                _schedule = _scheduleFactory.getSchedule(_properties.getProperty("schedule"));
            else
                _schedule = _baseConf.getSchedule();
        }
        return _schedule;
    }
}



