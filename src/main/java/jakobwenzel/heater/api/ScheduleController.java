package jakobwenzel.heater.api;

import jakobwenzel.heater.model.Schedule;
import jakobwenzel.heater.model.ScheduleItem;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by jwenzel on 18.01.17.
 */
@Path("schedule")
public class ScheduleController {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public List<ScheduleItem> getSchedule() {
        return Schedule.getInstance().getSchedule();
    }

    @POST
    public void addItem(ScheduleItem item) {
        Schedule.getInstance().addItem(item);
    }


    @POST
    @Path("delete")
    public void deleteItem(ScheduleItem item) {
        Schedule.getInstance().deleteItem(item);
    }
}
