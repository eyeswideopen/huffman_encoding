package helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: valon
 * Date: 06.09.12
 * Time: 16:22
 * Logging Information for building Schedule
 */
public class Logger {

    private static Logger _instance = null;
    private Queue<String> _logQueue;
    private Thread _thread;
    private BufferedWriter _out;
    private boolean _working;

    /**
    * A private Constructor prevents any other class from instantiating
    */
    private Logger() {
    }

    /**
     * getInstance() for Singelton-Class "Logger"
     *
     * @return returns a Logger-instance if it is present, otherwise a new Logger-instance will be created before
     */
    public static Logger getInstance() throws IOException {
        if (_instance == null) {
            _instance = new Logger();
        } else {
            return _instance;
        }

        // create LinkedBlockingQueue, which orders elements FIFO (first-in-first-out)
        _instance._logQueue = new LinkedBlockingQueue<String>();
        //create new FileWriter to write logging-information
        FileWriter _logFile = new FileWriter("logging.txt");
        _instance._out = new BufferedWriter(_logFile);

        //create new runnable Thread to run the method work()
        _instance._thread = new Thread(new Runnable() {
            public void run() {
                _instance.work();
            }
        });
        _instance._working = true;
        _instance._thread.start();

        return _instance;
    }

    /**
     * @param message, which will be added in queue "_logQueue"
     */
    public void log(String message) {
        _logQueue.add(message);
    }

    /**
     * writes log messages of the queue in File if queue ist not empty
     */
    private void work() {
        try {
            //do while "_working=true" or "_logQueue" isn´t empty (operation "or" ist used to write out the remaining parts before stop)
            while (_working || !_logQueue.isEmpty()) {
                if (_logQueue.isEmpty()) {
                    Thread.sleep(100);
                } else {
                    //write messages of "_logQueue" in BufferedWriter, which holds a reference on FileWriter "_logFile" to write in File
                    _out.write(_logQueue.poll());
                    //write "Enter" to write the next line under the line before
                    _out.write("\n");
                }
            }
        } catch (Exception e) {
            _working = false;
            e.printStackTrace();
        }
    }

    /**
     * stop thread and close BufferedWriter "_out", which holds a reference on FileWriter "_logFile" to write in File
     */
    public void stop() {
        _working = false;
        try {
            //Waits 1000 milliseconds for this thread to die
            _thread.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            //close stream
            _out.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
