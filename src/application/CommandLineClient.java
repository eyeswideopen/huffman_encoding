package application;

import decoder.Decoder;
import encoder.configuration.CommandLineConfiguration;
import encoder.configuration.FileConfiguration;
import encoder.configuration.evaluation.base.EnSeverity;
import encoder.configuration.evaluation.interfaces.IEvaluationItem;
import encoder.configuration.evaluation.interfaces.IEvaluator;
import encoder.io.FileReceiver;
import encoder.io.FileSender;
import encoder.processing.Processor;
import helper.Logger;
import helper.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/20/12
 * © Jacob Schlesinger 2012
 */
public class CommandLineClient {

    public static void main(String[] args) {
        CommandLineConfiguration conf =
                new CommandLineConfiguration(
                        new FileConfiguration(
                                null,
                                System.getProperty("user.home") + "/.huffman.properties"),
                        args);

        Processor processor = new Processor(conf);

        IEvaluator evaluator = conf.getEvaluator();
        LinkedList<IEvaluationItem> evalItems = new LinkedList<IEvaluationItem>();
        EnSeverity severity = evaluator.evaluate(conf, evalItems);
        for(IEvaluationItem item : evalItems)
            System.out.println(item);
        switch (severity){
            case Warning:
                if(conf.force())
                    break;
                System.out.println("aborting execution because of warning(s) - set force option to ignore warnings");
                System.exit(-1);
            case Error:
                System.out.println("aborting execution because of error(s)");
                conf.printHelp();
                System.exit(-1);
        }

        if (conf.decode()) {
            try {
                File f = new File(conf.getInputFileName());
                if (!f.exists())
                    throw new Exception("file '" + conf.getInputFileName() + "' does not exist");
                Decoder decoder = new Decoder(conf.getInputFileName(), conf.getOutputFileNmae());
                StopWatch overallRuntime = StopWatch.start("overall runtime");
                decoder.decode();
                overallRuntime.stop();
                if (conf.showStatistics()) {
                    StopWatch.printTable();
                }
            } catch (Throwable t) {
                System.out.println("execution failed with '"+t.getClass().getSimpleName()+"': "+ t.getMessage());
                if(conf.verbose())
                    t.printStackTrace();
                System.exit(-1);
            }
        } else {
            try {
                File f = new File(conf.getInputFileName());
                if (!f.exists())
                    throw new Exception("file '" + conf.getInputFileName() + "' does not exist");
                processor.setReceiver(new FileReceiver(conf.getInputFileName()));
                processor.setSender(new FileSender(conf.getOutputFileNmae()));
                StopWatch processTime = StopWatch.start();
                processor.process();
                processTime.stop();
                if (conf.showStatistics()) {
                    StopWatch.printTable();
                }
                if (conf.isLoggingEnabled()){
                    try {
                        Logger.getInstance().stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable t) {
                System.out.println("execution failed with '"+t.getClass().getSimpleName()+"': "+ t.getMessage());
                if(conf.verbose())
                    t.printStackTrace();
                System.exit(-1);
            }
        }

        System.exit(0);
    }
}
