package experimental;

import encoder.configuration.CodeConfiguration;
import encoder.configuration.evaluation.base.EnSeverity;
import encoder.configuration.evaluation.interfaces.IEvaluationItem;
import encoder.configuration.evaluation.interfaces.IEvaluator;
import encoder.parallelization.Dependency;
import encoder.parallelization.EnRunningStates;
import encoder.parallelization.ThreadPool;
import encoder.parallelization.tasks.TaskBase;
import helper.Logger;
import helper.StopWatch;

import java.io.IOException;
import java.util.LinkedList;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/15/12
 * © Jacob Schlesinger 2012
 */
public class Exp01 {
    private static volatile int cnt = 0;

    public static void main(String[] args) {
        CodeConfiguration conf = new CodeConfiguration(null);
        conf.setAvailableHardwareThreads(2);
        conf.setLoggingEnabled(true);

        // DON'T SET CONFIGURATION PARAMETERS BEHIND THIS LINE !!!
        IEvaluator evaluator = conf.getEvaluator();
        LinkedList<IEvaluationItem> evalItems = new LinkedList<IEvaluationItem>();
        EnSeverity severity = evaluator.evaluate(conf, evalItems);
        for(IEvaluationItem item : evalItems)
            System.out.println(item);
        if(severity == EnSeverity.Error){
            System.out.println("aborting execution because of configuration errors");
            System.exit(-1);
        }

        final ThreadPool pool = new ThreadPool(conf.getAvailableHardwareThreads(), conf);


        final TaskBase t1 = new TaskBase(null) {
            @Override
            protected void runInternal() throws IOException {
                synchronized (TaskBase.class) {
                    cnt++;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            @Override
            public String getTypeName() {
                return "t1";
            }
        };

        final TaskBase t2 = new TaskBase(null) {
            @Override
            protected void runInternal() throws IOException {
                synchronized (TaskBase.class) {
                    cnt++;
                }
            }

            @Override
            public String getTypeName() {
                return "t2";
            }
        };

        final TaskBase t3 = new TaskBase(null) {
            @Override
            protected void runInternal() throws IOException {
                synchronized (TaskBase.class) {
                    cnt++;
                }
            }

            @Override
            public String getTypeName() {
                return "t3";
            }
        };

        TaskBase mgmtTask = new TaskBase(null) {
            private volatile boolean _kill;
            private volatile boolean _firstRun = true;

            @Override
            protected void runInternal() throws IOException {
                if (_firstRun) {
                    addDependency(new Dependency(t3, EnRunningStates.Completed));
                    _firstRun = false;
                }
                if (cnt < 60) {
                    t1.reset(null);
                    t2.reset(null);
                    t3.reset(null);
                    pool.pushTask(t1, 0);
                    pool.pushTask(t2, 1);
                    pool.pushTask(t3, 1);
                } else {
                    _kill = true;
                }
            }

            @Override
            protected void afterRun() {
                super.afterRun();
                if (_kill) {
                    try {
                        pool.stop();
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                } else {
                    reset(null);
                    pool.pushTask(this, 0);
                }
            }

            @Override
            public String getTypeName() {
                return "mgmt";
            }
        };

        //mgmtTask.addDependency(new Dependency(t3, EnRunningStates.Completed));

        t3.addDependency(new Dependency(t1, EnRunningStates.Completed));
        t3.addDependency(new Dependency(t2, EnRunningStates.Completed));


        StopWatch executionTime = StopWatch.start("executionTime");
        pool.pushTask(mgmtTask, 0);
//        pool.work(0);
        executionTime.stop();

        StopWatch.printTable();
        try {
            Logger.getInstance().stop();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
