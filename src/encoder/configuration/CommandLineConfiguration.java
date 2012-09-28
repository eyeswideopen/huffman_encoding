package encoder.configuration;

import encoder.configuration.evaluation.CommandLineEvaluator;
import encoder.configuration.evaluation.interfaces.IEvaluator;
import encoder.configuration.interfaces.IConfiguration;
import encoder.parallelization.ScheduleFactory;
import encoder.parallelization.interfaces.ISchedule;
import org.apache.commons.cli.*;

/**
 * {@inheritDoc}
 *
 * Furthermore the CommandLineConfiguration class implements settings for modifying the applications execution behavior
 * and usability.
 */
public class CommandLineConfiguration implements IConfiguration {
    private IConfiguration _baseConf;

    private CommandLine _cmd;

    private Integer _wordSize;
    private Integer _chunkSize;
    private Boolean _isLoggingEnabled;
    private Integer _availableHardwareThreads;
    private Boolean _useAffinityLocks;
    private String _inputFileName;
    private String _outputFileName;
    private Boolean _decode;
    private Boolean _stats;
    private Boolean _force;
    private Boolean _verbose;
    private ISchedule _schedule;
    private Boolean _listSchedules;

    private Option _wordSizeOption;
    private Option _chunkSizeOption;
    private Option _isLoggingEnabledOption;
    private Option _availableHardwareThreadsOption;
    private Option _useAffinityLocksOption;
    private Option _helpOption;
    private Option _decodeOption;
    private Option _statsOption;
    private Option _forceOption;
    private Option _verboseOption;
    private Option _scheduleOption;
    private Option _listSchedulesOption;
    private Options _options;

    private ScheduleFactory _scheduleFactory;

    /**
     * Creates a new configuration object taking its values either from the command line (args) or
     * from the base configuration. If the base configuration parameter is null a new {@link DefaultConfiguration}
     * instance is created.
     * @param baseConf
     * @param args the arguments from the applications main method
     */
    public CommandLineConfiguration(IConfiguration baseConf, String[] args) {
        if (baseConf == null)
            _baseConf = new DefaultConfiguration();
        else
            _baseConf = baseConf;

        _scheduleFactory = ScheduleFactory.getInstance();

        _options = new Options();
        _options.addOption(_chunkSizeOption = new Option("c", "chunk.size", true, "chunk size (in words)"));
        _options.addOption(_wordSizeOption = new Option("w", "word-size", true, "word size (in bit)"));
        _options.addOption(_isLoggingEnabledOption = new Option("l", "log", false, "enables logging"));
        _options.addOption(_availableHardwareThreadsOption = new Option("t", "threads", true, "sets the number of used threads"));
        _options.addOption(_useAffinityLocksOption = new Option("a", "affinity-locking", false, "use affinity locks"));
        _options.addOption(_helpOption = new Option("h", "help", false, "show help"));
        _options.addOption(_decodeOption = new Option("d", "decode", false, "decode"));
        _options.addOption(_statsOption = new Option("s", "statistics", false, "show runtime statistics"));
        _options.addOption(_forceOption = new Option("f", "force", false, "force execution, ignoring warnings"));
        _options.addOption(_verboseOption = new Option("v", "verbose", false, "show verbose error messages"));
        _options.addOption(_scheduleOption = new Option("e", "schedule", true, "sets the execution schedule"));
        _options.addOption(_listSchedulesOption = new Option("i", "list-schedules", false, "lists available schedules"));

        CommandLineParser parser = new PosixParser();
        try {
            _cmd = parser.parse(_options, args);

            if (_cmd.hasOption(_helpOption.getOpt())) {
                printHelp();
                System.exit(0);
            }
            if(_cmd.hasOption(_listSchedulesOption.getOpt())){
                _scheduleFactory.printScheduleList();
                System.exit(0);
            }

            if (_cmd.getArgs().length < 2)
                throw new Exception("you must specify an input and an output file");
            _inputFileName = _cmd.getArgs()[0];
            _outputFileName = _cmd.getArgs()[1];
        } catch (Exception e) {
            System.out.println(e.getMessage());
            printHelp();
            System.exit(-1);
        }
    }

    /**
     * Print the help generated via apache cli options to the standard output stream.
     */
    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("$huffman [options] <input_file> <output_file>", _options);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public int getWordSize() {
        if (_wordSize == null) {
            if (_cmd.hasOption(_wordSizeOption.getOpt())) {
                try {
                    _wordSize = Integer.parseInt(_cmd.getOptionValue(_wordSizeOption.getOpt()));
                } catch (Throwable t) {
                    System.out.println(t.getMessage());
                    printHelp();
                    System.exit(-1);
                }
            } else
                _wordSize = _baseConf.getWordSize();
        }
        return _wordSize;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public boolean wordSizeSet(){
        return _cmd.hasOption(_wordSizeOption.getOpt());
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public int getChunkSize() {
        if (_chunkSize == null) {
            if (_cmd.hasOption(_chunkSizeOption.getOpt())) {
                try {
                    _chunkSize = Integer.parseInt(_cmd.getOptionValue(_chunkSizeOption.getOpt()));
                } catch (Throwable t) {
                    System.out.print(t.getMessage());
                    printHelp();
                    System.exit(-1);
                }
            } else
                _chunkSize = _baseConf.getChunkSize();
        }
        return _chunkSize;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public boolean chunkSizeSet(){
        return _cmd.hasOption(_chunkSizeOption.getOpt());
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public boolean isLoggingEnabled() {
        if (_isLoggingEnabled == null) {
            if (_cmd.hasOption(_isLoggingEnabledOption.getOpt())) {
                _isLoggingEnabled = true;
            } else
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
            if (_cmd.hasOption(_availableHardwareThreadsOption.getOpt())) {
                try {
                    _availableHardwareThreads = Integer.parseInt(_cmd.getOptionValue(_availableHardwareThreadsOption.getOpt()));
                } catch (Throwable t) {
                    System.out.print(t.getMessage());
                    printHelp();
                    System.exit(-1);
                }
            } else
                _availableHardwareThreads = _baseConf.getAvailableHardwareThreads();
        }
        return _availableHardwareThreads;
    }

    /**
     * Specifies whether the number of available hardware threads has been set via command line option or not.
     * @return
     */
    public boolean availableHardwareThreadsSet(){
        return _cmd.hasOption(_availableHardwareThreadsOption.getOpt());
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public boolean useAffinityLocks() {
        if (_useAffinityLocks == null) {
            if (_cmd.hasOption(_useAffinityLocksOption.getOpt())) {
                _useAffinityLocks = true;
            } else
                _useAffinityLocks = _baseConf.useAffinityLocks();
        }
        return _useAffinityLocks;
    }

    /**
     * Specifies whether to decode or encode the specified input file.
     * @return
     */
    public boolean decode() {
        if (_decode == null) {
            if (_cmd.hasOption(_decodeOption.getOpt()))
                _decode = true;
            else
                _decode = false;
        }
        return _decode;
    }

    /**
     * Specifies whether to show execution statistics after execution or not.
     * @return
     */
    public boolean showStatistics() {
        if (_stats == null) {
            if (_cmd.hasOption(_statsOption.getOpt()))
                _stats = true;
            else
                _stats = false;
        }
        return _stats;
    }

    /**
     * Specifies whether to ignore warnings generated by the {@link CommandLineEvaluator evaluator}.
     * @return
     */
    public boolean force(){
        if(_force==null)
            _force = _cmd.hasOption(_forceOption.getOpt());
        return _force;
    }

    /**
     * Specifies whether to print stack traces of exceptions aborting execution or not.
     * @return
     */
    public boolean verbose(){
        if(_verbose == null)
            _verbose = _cmd.hasOption(_verboseOption.getOpt());
        return _verbose;
    }

    /**
     * Retrieves the input file name specified as the first parameter on the command line.
     * @return
     */
    public String getInputFileName() {
        return _inputFileName;
    }

    /**
     * Retrieves the output file name specified as the second parameter on the command line.
     * @return
     */
    public String getOutputFileNmae() {
        return _outputFileName;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public IEvaluator getEvaluator() {
        return new CommandLineEvaluator();
    }

    /**
     * {@inheritDoc}
     * @return
     */
    public ISchedule getSchedule() {
        if(_schedule==null){
            if(_cmd.hasOption(_scheduleOption.getOpt()))
                _schedule = _scheduleFactory.getSchedule(_cmd.getOptionValue(_scheduleOption.getOpt()));
            else
                _schedule = _baseConf.getSchedule();
        }
        return _schedule;
    }
}
