package encoder.parallelization;

import encoder.configuration.interfaces.IConfiguration;
import encoder.parallelization.interfaces.ITask;
import vanilla.java.affinity.AffinityLock;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: eyeswideopen, valon
 * Date: 06.09.12
 * Time: 13:37
 * ThreadPool provides the concurrent processing of ITask objects on a given amount of parallel threads.
 * Each thread has its own queue of ITasks he works on.
 * When the constructor is called all the threads start observing their own queues except the main thread.
 *
 * After initialisation you can call the work(0) to start the main thread to process its queue.
 * If you do so the filling of the queues with ITasks should be handled within management ITasks they
 * should already be filled completely.
 *
 * If a thread has no more ITasks left he is in busy wait until new ones appear in the queue or he gets stopped.
 *
 * If set true in the config file it uses the Affinity framework to lock the threads to certain hardware threads.
 */

public class ThreadPool {

    private Thread[] _threads;
    private boolean _working;
    private Queue[] _taskQueues;
    private boolean _useThreadLock;

    /**
     * standard constructor which initializes given amount of working threads and starts them.
     *
     * @param size  number of threads that should be used as worker threads (including current thread)
     * @param conf  the configuration for the ThreadPool session
     */
    public ThreadPool(int size, IConfiguration conf) {
        _threads = new Thread[size];
        _taskQueues = new LinkedBlockingQueue[size];
        _useThreadLock = conf.useAffinityLocks();

        for (int i = 0; i < size; i++) {
            _taskQueues[i] = new LinkedBlockingQueue<ITask>();
        }

        _threads[0] = Thread.currentThread();
        for (int i = 1; i < size; i++) {
            final int j = i;
            _threads[i] = new Thread(new Runnable() {
                public void run() {
                    work(j);
                }
            });
            _threads[i].setName("Thread" + i);
            _threads[i].start();
        }
    }

    //TODO: get rid of busywait with wait() and notifyAll() in pushTask

    /**
     * The main working method.
     * It gets called by all the worker threads automatically in the constructor.
     * Call it manually with 0 as parameter to put the current main thread to work as well.
     *
     * @param queueNumber   the thread queue number which will be handled
     */
    public void work(int queueNumber) {
        _working = true;
        AffinityLock lock = null;
        if (_useThreadLock) {
            lock = AffinityLock.acquireLock(true);
        }
        try {
            while (_working) {
                if (_taskQueues[queueNumber].size() == 0) {
//                    why does this not work?!
//                    try {
//                        wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                    }
                } else {
                    ITask task = (ITask) _taskQueues[queueNumber].poll();
                    if (task != null) {
                        task.run();
                    }
                }
            }
        } finally {
            if (lock != null) {
                lock.release();
            }
        }
    }

    /**
     * stops the whole ThreadPool with all its worker threads
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException {
        _working = false;
        for (Thread thread : _threads) {
            if (thread != Thread.currentThread()) thread.join(1000);
        }
    }

    /**
     * Used to push a certain ITask to a given queue to be handled by the corresponding worker thread.
     *
     * @param task          ITask to be processed
     * @param queueNumber   destination queue number
     */
    public void pushTask(ITask task, int queueNumber) {
        if (_taskQueues[queueNumber].contains(task))
            throw new RuntimeException("task " + task.getID() + " is already in queue");
        task.setState(EnRunningStates.Scheduled);
        _taskQueues[queueNumber].add(task);
    }

    /**
     * This notifys all waiting worker threads to wake up and re-check their dependencies.
     */
    public void notifyWaiting() {
        synchronized (this) {
            try {
                this.notifyAll();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}