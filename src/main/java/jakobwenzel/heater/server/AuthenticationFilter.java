package jakobwenzel.heater.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakobwenzel.heater.model.User;
import org.glassfish.jersey.internal.util.Base64;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jwenzel on 19.01.17.
 */
@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter
{

    private static Map<String,User> users;

    @Context
    private ResourceInfo resourceInfo;

    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";


    private static Map<String,String> authorized = new HashMap<>();

    private static ObjectMapper mapper = new ObjectMapper();
    private static File authFile = new File("authorized.json");
    static {
        try {
            if (authFile.exists())
                authorized = mapper.readValue(authFile, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static synchronized void addAuthorized(String username, String auth) {
        authorized.put(auth, username);

        try {

            mapper.writeValue(authFile, authorized);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext)
    {
        Method method = resourceInfo.getResourceMethod();
        //Access allowed for all
        if( ! method.isAnnotationPresent(PermitAll.class))
        {
            //Access denied for all
            if(method.isAnnotationPresent(DenyAll.class))
            {
                requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                        .entity("Access blocked for all users !!").build());
                return;
            }

            Cookie cookie = requestContext.getCookies().get("auth");
            if (cookie != null) {
                String usernameFromCookie = authorized.get(cookie.getValue());
                if (usernameFromCookie!=null && users.get(usernameFromCookie) != null) {
                    System.out.println("Authorized "+usernameFromCookie+" from cookie");
                    return;
                }
            }

            //Get request headers
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();

            //Fetch authorization header
            final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);


            //If no authorization information present; block access
            if(authorization == null || authorization.isEmpty())
            {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .header("WWW-Authenticate","Basic realm=\"myRealm\"")
                        .entity("Auth required").build());
                return;
            }

            //Get encoded username and password
            final String encodedUserPassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

            //Decode username and password
            String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));;

            //Split username and password tokens
            final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
            final String username = tokenizer.nextToken();
            final String password = tokenizer.nextToken();

            //Is user valid?
            if( ! isUserAllowed(username, password))
            {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .header("WWW-Authenticate","Basic realm=\"myRealm\"")
                        .entity("Auth required").build());
            } else {
                String auth = UUID.randomUUID().toString();
                addAuthorized(username, auth);
                ResponseCookieSetter.sendResponseCookie(requestContext.getRequest(),auth);
            }
        }
    }
    private boolean isUserAllowed(final String username, final String password)
    {
        User user = users.get(username);
        if (user!=null)
            return password.equals(user.password);
        else return false;
    }

    public static final void setUsers(Set<User> users) {
        AuthenticationFilter.users = users.stream().collect(Collectors.toMap(u -> u.username, Function.identity()));
    }
}