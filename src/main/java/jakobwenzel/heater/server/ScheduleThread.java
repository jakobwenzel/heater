package jakobwenzel.heater.server;

import jakobwenzel.heater.model.Schedule;
import jakobwenzel.heater.model.ScheduleItem;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by jwenzel on 18.01.17.
 */
public class ScheduleThread extends Thread {
    private static ScheduleThread instance;

    public synchronized static ScheduleThread getInstance() {
        if (instance==null)
            instance = new ScheduleThread();
        return instance;
    }
    boolean finish = false;
    public void finish() {
        finish = true;
        interrupt();

    }
    @Override
    public void run() {
        Schedule schedule = Schedule.getInstance();
        while (!finish) {
            ScheduleItem nextItem = schedule.getNextItem();
            if (nextItem==null) {
                try {
                    if (!Thread.interrupted())
                        Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            } else {
                Instant now = Instant.now();
                if (now.isAfter(nextItem.getInstant())) {
                    schedule.scheduledTime();
                } else {
                    long millis = Duration.between(now, nextItem.getInstant()).toMillis();
                    try {
                        if (!Thread.interrupted())
                            Thread.sleep(millis+100);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }
}
