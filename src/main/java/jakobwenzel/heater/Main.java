package jakobwenzel.heater;

import jakobwenzel.heater.model.Heater;
import jakobwenzel.heater.model.Settings;
import jakobwenzel.heater.server.AuthenticationFilter;
import jakobwenzel.heater.server.ObjectMapperProvider;
import jakobwenzel.heater.server.ResponseCookieSetter;
import jakobwenzel.heater.server.ScheduleThread;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class
 */
public class Main{
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     * @param settings The settings
     */
    public static HttpServer startServer(Settings settings) {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example.rest package
        final ResourceConfig rc = new ResourceConfig().packages("jakobwenzel.heater");
        rc.register(JacksonFeature.class);
        rc.register(ObjectMapperProvider.class);
        rc.register(AuthenticationFilter.class);
        rc.register(ResponseCookieSetter.class);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        String baseUri = "http://" + settings.hostname + ":" + settings.port;
        String apiUri = baseUri + "/api/";

        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(apiUri), rc);
        httpServer.getServerConfiguration().addHttpHandler(
                new CLStaticHttpHandler(Main.class.getClassLoader(), "/static/"));

        System.out.println(String.format("Jersey app started at "
                + "%s\nHit enter to stop it...", baseUri));

        return httpServer;
    }

    /**
     * Main method.
     * @param args Arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        ScheduleThread.getInstance().start();

        Settings settings = Settings.readSettings(Main.class.getResourceAsStream("/settings.json"));
        Heater.getInstance().setRegion(settings.region);
        Heater.getInstance().setEndpoint(settings.endpoint);
        AuthenticationFilter.setUsers(settings.users);


        final HttpServer server = startServer(settings);

        System.in.read();
        server.stop();
        ScheduleThread.getInstance().finish();
        ScheduleThread.getInstance().join();
    }
}