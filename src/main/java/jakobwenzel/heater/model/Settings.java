package jakobwenzel.heater.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Settings
 */
public class Settings {
    public final String region;
    public final String endpoint;
    public final String hostname;
    public final int port;
    public final Set<User> users;

    public Settings(
            @JsonProperty("region") String region,
            @JsonProperty("endpoint") String endpoint,
            @JsonProperty("users") Set<User> users,
            @JsonProperty("hostname") String hostname,
            @JsonProperty("port") int port
    ) {
        this.region = region;
        this.endpoint = endpoint;
        this.hostname = hostname;
        this.port = port;
        this.users = users;
    }

    public static Settings readSettings(InputStream settings) throws IOException {
        return new ObjectMapper().readValue(settings, Settings.class);
    }
}
