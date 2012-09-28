package helper.logStatistic;

import encoder.parallelization.EnRunningStates;
import org.apache.commons.cli.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 21.09.12
 * Time: 14:51
 */
public class Evaluator {

    public static void main(String[] args) throws IOException {
        Options options = new Options();
        Option skipOption = new Option("s","skip",true,"number of chunks to skip");
        options.addOption(skipOption);
        Option sequentialOption = new Option("q", "sequential", false, "log is sequential task log");
        options.addOption(sequentialOption);

        CommandLineParser parser = new PosixParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            System.exit(-1);
        }

        if(cmd.getArgs().length!=1){
            System.out.println("please provide exactly one log file as a parameter");
            System.exit(-1);
        }
        if(!new File(cmd.getArgs()[0]).exists()){
            System.out.println("file '"+args[0]+"' does not exist");
            System.exit(-1);
        }

        boolean sequential = cmd.hasOption(sequentialOption.getOpt());
        int skipchunks = 4;
        try{
            if(cmd.hasOption(skipOption.getOpt()))
                skipchunks = Integer.parseInt(cmd.getOptionValue(skipOption.getOpt()));
            if(skipchunks<0)
                throw new Exception("skip chunks must be >=0");
        }catch(Throwable t){
            t.printStackTrace();
        }


        Map<Integer, ChunkTime> chunkTimeMap = new HashMap<Integer, ChunkTime>();

        List<Integer> threads = new LinkedList<Integer>();

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(new File(cmd.getArgs()[0])));

        //Read data from logfile
        String line;
        while ((line = reader.readLine()) != null) {
            long time = getTimeStamp(line);
            int id = getID(line);
            EnRunningStates state = getState(line);
                 Integer threadID = getThread(line);

            if(!threads.contains(threadID))
                threads.add(threadID);
            //Skip chunks of vm heating phase
             if (id > skipchunks || sequential) {
                //refresh chunk information
                if (chunkTimeMap.containsKey(id)) {
                    chunkTimeMap.get(id).putTimeStamp(time, state, threadID);
                 //Create new chunk
                } else {
                    chunkTimeMap.put(id, new ChunkTime(time, state, threadID));
                }
            }
        }

        //evaluate data
        Double processTime = getProcessingTime(chunkTimeMap);
        Double overallTime = getOverallTime(chunkTimeMap);
        int countOfChunks = chunkTimeMap.keySet().size();
        int chunksPerSec = (int) ((countOfChunks * 1000) / (overallTime));

        DescriptiveStatistics statistics = new DescriptiveStatistics();

        for (Integer key : chunkTimeMap.keySet()){
            statistics.addValue(Double.parseDouble(chunkTimeMap.get(key).getDurationInSeconds()));
        }

        //print data
        System.out.println("Number of Chunks processed: \t" + countOfChunks);
        System.out.println("Number of Chunks per second: \t" + chunksPerSec);
        System.out.println("Slowest Chunk (ms): \t" + statistics.getMax());
        System.out.println("Fastest Chunk (ms): \t" + statistics.getMin());
        System.out.println("Average Chunk (ms): \t" + statistics.getMean());
        System.out.println("Geometric Mean (ms): \t" + statistics.getGeometricMean());
       System.out.println("Standard deviation (ms): \t" + statistics.getStandardDeviation());
        System.out.println("Variance (ms): \t" + statistics.getVariance());
        System.out.println("Overall running Time (ms): \t" + overallTime);
        System.out.println("Real Working Time (ms): \t" + processTime);
        for(Integer threadID : threads){
            System.out.println("Real Working Time of Thread " + threadID +" (ms): \t" + getProcessingTimeOfThread(chunkTimeMap, threadID));
        }
        System.out.println("Process Idle (" + threads.size() + " thread(s)) %: \t" + (((overallTime * threads.size()) - processTime) * 100 / (overallTime * threads.size())));
        for(Integer threadID : threads){
            System.out.println("Process Idle of Thread " + threadID +" %: \t" + (100 - (getProcessingTimeOfThread(chunkTimeMap, threadID) * 100 / overallTime)));
        }

    }

    private static Double getOverallTime(Map<Integer, ChunkTime> chunkTimeMap){
        long overallStartTime = Long.MAX_VALUE;
        long overallEndTime = Long.MIN_VALUE;

        for(Integer key : chunkTimeMap.keySet()){
            long first = chunkTimeMap.get(key).getFirstTime();
            long last = chunkTimeMap.get(key).getLastTime();
            if(first < overallStartTime)
                overallStartTime = first;
            if(last > overallEndTime)
               overallEndTime = last;
        }
             return (overallEndTime - overallStartTime) / Math.pow(10, 6);
    }
    /**
     * Total of processing time.
     * Each task from running to completed is measured.
     * @param chunkTimeMap data with extracted information
     * @return time in seconds
     */
    private static Double getProcessingTime(Map<Integer, ChunkTime> chunkTimeMap) {
        long excecTime = 0;
        for (Integer key : chunkTimeMap.keySet()) {
            excecTime += chunkTimeMap.get(key).executionTimeOfWholeChunk();
        }
        return excecTime / Math.pow(10, 6);
    }


    /**
     * Total of processing time.
     * Each task from running to completed is measured.
     * @param chunkTimeMap data with extracted information
     * @return time in seconds
     */
    private static Double getProcessingTimeOfThread(Map<Integer, ChunkTime> chunkTimeMap, Integer threadID) {
        long excecTime = 0;
        for (Integer key : chunkTimeMap.keySet()) {
            excecTime += chunkTimeMap.get(key).executionTimeOfChunkPerThread(threadID);
        }
        return excecTime / Math.pow(10, 6);
    }

    /**
     * Extract time information of logline
     * @param line dataline
     * @return time in nanoseconds
     */
    public static long getTimeStamp(String line) {
        String time = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
        long lTime = Long.valueOf(time);
        return lTime;
    }

    /**
     * Extract id information of logline
     * @param line dataline
     * @return id of chunk
     */
    public static int getID(String line) {
        String id = line.substring(line.indexOf("@") + 1, line.indexOf("-"));
        int iId = Integer.valueOf(id);
        return iId;
    }


    /**
     * Extract state information of logline
     * @param line dataline
     * @return state of chunk
     */
    public static EnRunningStates getState(String line) {
        String state = line.substring(line.indexOf(":") + 1);
        return EnRunningStates.valueOf(state);
    }


    /**
     * Extract thread information of logline
     * @param line dataline
     * @return thread of chunk
     */
    public static Integer getThread(String line) {
        return Integer.valueOf(line.substring(line.indexOf("{") + 1, line.indexOf("}")));
    }
}
