package helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 02.09.12
 * Time: 22:15
 * To change this template use File | Settings | File Templates.
 */
public class StopWatch {
    private long _startTime = 0;
    private long _endTime = 0;
    private String _caller;
    private String _postfix;
    private static Map<String, Long> _timeTable = new HashMap<String, Long>();

    public static StopWatch start() {

        StopWatch watch = new StopWatch();
        watch._startTime = System.nanoTime();
        watch._caller = getCaller();
        return watch;
    }

    public static StopWatch start(String postfix) {
        StopWatch watch = new StopWatch();
        watch._postfix = postfix;
        watch._startTime = System.nanoTime();
        watch._caller = getCaller();
        return watch;
    }

    public void stop() {
        _endTime = System.nanoTime();
        long timeDiff = _endTime - _startTime;
        String key = _caller + (_postfix == null ? "" : ("[" + _postfix + "]"));
        if (_timeTable.containsKey(key))
            timeDiff += _timeTable.get(key);

        _timeTable.put(key, timeDiff);
    }

    private static String getCaller() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StackTraceElement element = trace[3];
        return element.getClassName() + "." + element.getMethodName();
    }

    public String toString() {
        return _caller + ":" + (_endTime - _startTime);
    }

    private StopWatch() {

    }

    public static void printTable() {
        Iterator it = _timeTable.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            System.out.println(key + " : " + _timeTable.get(key) / 1000000000.0);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        StopWatch watch = start();

        Thread.sleep(1000);

        watch.stop();

        System.out.println(watch.toString());


    }
}
