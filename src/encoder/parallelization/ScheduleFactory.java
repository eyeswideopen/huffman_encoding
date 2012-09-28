package encoder.parallelization;

import encoder.parallelization.Schedules.*;
import encoder.parallelization.interfaces.ISchedule;

import java.util.*;

/**
 * author: Jacob Schlesinger <schlesinger.jacob@gmail.com>
 * creation date: 9/23/12
 * © Jacob Schlesinger 2012
 */
public class ScheduleFactory {
    private Map<String, ISchedule> _schedules;

    private static ScheduleFactory _instance;

    static {
        _instance = new ScheduleFactory();
    }

    private ScheduleFactory() {
        _schedules = new HashMap<String, ISchedule>();

        // TODO: replace with dynamic filling via reflection
        ISchedule schedule = new BasicParallelSchedule();
        _schedules.put(schedule.getID(), schedule);
        schedule = new ImprParallelSchedule();
        _schedules.put(schedule.getID(), schedule);
        schedule = new MirroredParallelSchedule();
        _schedules.put(schedule.getID(), schedule);
        schedule = new SequentialSingelTaskSchedule();
        _schedules.put(schedule.getID(), schedule);
        schedule = new SequentialSchedule();
        _schedules.put(schedule.getID(), schedule);
    }

    public static ScheduleFactory getInstance(){
        return _instance;
    }

    public Collection<String> getIdList(){
        return Collections.unmodifiableCollection(_schedules.keySet());
    }

    public Collection<ISchedule> getScheduleList(){
        return Collections.unmodifiableCollection(_schedules.values());
    }

    public ISchedule getSchedule(String id){
        if(!_schedules.containsKey(id))
            throw new RuntimeException("no schedule for id '"+id+"'");
        return _schedules.get(id);
    }

    public void printScheduleList(){
        System.out.println("ID\t\tDESCRIPTION\n--\t\t-----------");
        for(ISchedule schedule : _schedules.values())
            System.out.println(schedule.getID()+"\t\t"+schedule.getDescription());
    }

}
