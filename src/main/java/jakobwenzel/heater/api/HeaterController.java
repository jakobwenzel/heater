package jakobwenzel.heater.api;

import jakobwenzel.heater.model.Heater;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Created by jwenzel on 18.01.17.
 */
@Path("heater")
public class HeaterController {
    @GET
    @PermitAll
    public int getSetting() {
        return Heater.getInstance().getSetting();
    }

    @POST
    public void setSetting(int angle) {
        System.out.println(angle);
        Heater.getInstance().setSetting(angle);
    }
}
