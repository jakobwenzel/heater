package jakobwenzel.heater.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A user.
 */
public class User {
    public final String username;
    public final String password;

    public User(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }
}
