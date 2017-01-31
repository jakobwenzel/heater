package jakobwenzel.heater.model;

import jakobwenzel.heater.server.ScheduleThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jwenzel on 18.01.17.
 */
public class Schedule {
    private static Schedule instance;

    public synchronized static Schedule getInstance() {
        if (instance==null)
            instance = new Schedule();
        return instance;
    }

    private List<ScheduleItem> schedule = Collections.synchronizedList(new ArrayList<ScheduleItem>());

    public synchronized List<ScheduleItem> getSchedule() {
        return Collections.unmodifiableList(schedule);
    }

    public synchronized void addItem(ScheduleItem item) {
        for (int i=0;i<schedule.size();i++) {
            ScheduleItem inList = schedule.get(i);
            if (item.getInstant().isBefore(inList.getInstant())) {
                schedule.add(i,item);
                ScheduleThread.getInstance().interrupt();
                return;
            }
        }
        schedule.add(item);
        ScheduleThread.getInstance().interrupt();
    }

    public synchronized void scheduledTime() {
        if (schedule.isEmpty())
            return;
        ScheduleItem item = schedule.remove(0);
        System.out.println("Scheduled item reached: "+item);
        Heater.getInstance().setSetting(item.getSetting());
    }

    public synchronized ScheduleItem getNextItem() {
        if (schedule.isEmpty())
            return null;
        return schedule.get(0);
    }

    public synchronized void deleteItem(ScheduleItem item) {
        schedule.remove(item);
        ScheduleThread.getInstance().interrupt();
    }
}
