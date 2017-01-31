package jakobwenzel.heater.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Created by jwenzel on 18.01.17.
 */
public class ScheduleItem implements Comparable<ScheduleItem> {
    private final Instant instant;
    private final int setting;


    @JsonCreator
    public ScheduleItem(@JsonProperty("instant") Instant instant, @JsonProperty("setting") int setting) {
        this.instant = instant;
        this.setting = setting;
    }

    public Instant getInstant() {
        return instant;
    }

    public int getSetting() {
        return setting;
    }

    @Override
    public int compareTo(ScheduleItem scheduleItem) {
        return instant.compareTo(scheduleItem.instant);
    }

    @Override
    public String toString() {
        return "ScheduleItem{" +
                "instant=" + instant +
                ", setting=" + setting +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleItem that = (ScheduleItem) o;

        if (setting != that.setting) return false;
        return instant.equals(that.instant);

    }

    @Override
    public int hashCode() {
        int result = instant.hashCode();
        result = 31 * result + setting;
        return result;
    }
}
