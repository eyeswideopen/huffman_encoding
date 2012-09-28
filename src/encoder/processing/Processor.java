package encoder.processing;

import encoder.configuration.FileConfiguration;
import encoder.configuration.evaluation.base.EnSeverity;
import encoder.configuration.evaluation.interfaces.IEvaluationItem;
import encoder.configuration.evaluation.interfaces.IEvaluator;
import encoder.configuration.interfaces.IConfiguration;
import encoder.io.FileReceiver;
import encoder.io.FileSender;
import encoder.parallelization.Schedules.*;
import encoder.parallelization.ThreadPool;
import encoder.parallelization.tasks.ChunkTaskManagement;
import encoder.parallelization.interfaces.ISchedule;
import encoder.processing.interfaces.IHuffmanProcessor;
import helper.Logger;
import helper.StopWatch;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan, valon
 * Date: 28.08.12
 * Time: 15:20
 * The main class where ThreadPool gets initialized, the management task set up and started.
 */

public class Processor implements IHuffmanProcessor {

    private IConfiguration _configuration;
    private FileReceiver _receiver;
    private FileSender _sender;

    /**
     * Standard constructor
     * @param configuration     the configuration to be used for current job
     */
    public Processor(IConfiguration configuration) {
        _configuration = configuration;
    }

    public FileReceiver getReceiver() {
        return _receiver;
    }

    /**
     * Sets the FileReceiver for the current job
     *
     * @param receiver      FileReceiver to be used
     */
    public void setReceiver(FileReceiver receiver) {
        _receiver = receiver;
    }

    public FileSender getSender() {
        return _sender;
    }

    /**
     * Sets the FileSender for the current job
     *
     * @param sender      FileSender to be used
     */
    public void setSender(FileSender sender) {
        _sender = sender;
    }
    public void process() {

        //ThreadPool to be used to process the given schedule
        ThreadPool _pool = new ThreadPool(_configuration.getAvailableHardwareThreads(), _configuration);

        ISchedule schedule = _configuration.getSchedule();

        //Push the management task to queue of first worker thread.
        //Management task handles all the working task queueing.
        try {
            _pool.pushTask(new ChunkTaskManagement(_receiver.getInputStream(), _sender.getOutputStream(), _pool, _configuration, schedule), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //put the current thread to work on queue 0 in the threadPool
        try {
            _pool.work(0);
            //when all tasks are finished the worker threads get stopped and current tread
            //(was working on queue 0 in threadPool)returns here.
        } catch (Throwable t) {
            t.printStackTrace();
            // tear down thread pool if something goes wrong
            try {
                _pool.stop();
            } catch (InterruptedException e) {
                // print stack trace if something goes wrong on going wrong
                e.printStackTrace();
            }
        }
    }

    /**
     * Test main method. Is only used for in-IDE testing and usage.
     * @param args
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String[] args) throws InterruptedException, IOException {

        //start timing
        StopWatch overallTime = StopWatch.start();

        //test config to be used
        String pwd = System.getProperty("user.dir");

        IConfiguration conf = new FileConfiguration(null, pwd + "/testdata/config.properties");
        IEvaluator evaluator = conf.getEvaluator();
        LinkedList<IEvaluationItem> evalItems = new LinkedList<IEvaluationItem>();
        EnSeverity severity = evaluator.evaluate(conf, evalItems);
        for(IEvaluationItem item : evalItems)
            System.out.println(item);
        if(severity == EnSeverity.Error){
            System.out.println("aborting execution because of configuration errors");
            System.exit(-1);
        }


        //processor instance
        Processor processor = new Processor(conf);

        //input and output destinations
        processor.setReceiver(new FileReceiver(pwd + "/testdata/testdata/koransuren.txt"));
        processor.setSender(new FileSender(pwd + "/testdata/testdata/koransuren_enc"));

        //start main processing
        processor.process();

        //timing of overall processing
        overallTime.stop();
        StopWatch.printTable();

        //stop logging to write all left buffered log messages to file.
        try {
            if (conf.isLoggingEnabled())
                Logger.getInstance().stop();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
