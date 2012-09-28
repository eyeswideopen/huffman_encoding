package helper.logStatistic;

import encoder.parallelization.EnRunningStates;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Stefan
 * Date: 21.09.12
 * Time: 15:06
 * To change this template use File | Settings | File Templates.
 */
public class ChunkTime {
    private long _firstTime = Long.MAX_VALUE;
    private long _lastTime = Long.MIN_VALUE;
    private Map<Integer, Long> _runTime = new Hashtable<Integer, Long>();
    private Map<Integer, Long> _endTime = new Hashtable<Integer, Long>();

    public ChunkTime(long time, EnRunningStates state,int threadID) {
        putTimeStamp(time, state, threadID);
    }

    public long getFirstTime(){
        return _firstTime;
    }
    public long getLastTime(){
           return _lastTime;
    }

    public void setFirstTime(long time){
        if(_firstTime > time){
            _firstTime = time;
        }
    }
    public void setLastTime(long time){
        if(_lastTime < time){
            _lastTime = time;
        }
    }

    public long getDuration(){
        return _lastTime - _firstTime;
    }

    public String getDurationInSeconds(){
        return String.valueOf(getDuration() / Math.pow(10, 6));
    }

    public long executionTimeOfWholeChunk(){
        long runTime = 0, endTime = 0;
            for(Integer key : _runTime.keySet()){
                runTime += _runTime.get(key);
                endTime += _endTime.get(key);
            }

        return endTime - runTime;
    }
    public long executionTimeOfChunkPerThread(int threadID){
        if(_endTime.get(threadID) == null || _runTime.get(threadID) == null)
            return 0;

        return _endTime.get(threadID) - _runTime.get(threadID);
    }

    public void putTimeStamp(long time, EnRunningStates state, int threadID) {
        setFirstTime(time);
        setLastTime(time);


        if(state.equals(EnRunningStates.Running))
           if(_runTime.containsKey(threadID))
                _runTime.put(threadID, _runTime.get(threadID) + time);
           else
               _runTime.put(threadID, time);
        else if(state.equals(EnRunningStates.Completed))
            if(_endTime.containsKey(threadID))
                _endTime.put(threadID, _endTime.get(threadID) + time);
            else
                _endTime.put(threadID, time);

    }
}
