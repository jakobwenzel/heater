package jakobwenzel.heater.server;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Request;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jwenzel on 23.01.17.
 */
public class ResponseCookieSetter implements ContainerResponseFilter {

    private static Map<Request,String> cookiesToSet = new HashMap<>();

    public static void sendResponseCookie(Request request, String cookie) {
        cookiesToSet.put(request, cookie);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String cookie = cookiesToSet.remove(requestContext.getRequest());
        if (cookie !=null) {
            responseContext.getHeaders().add("Set-Cookie", new NewCookie("auth", cookie, null, null, Cookie.DEFAULT_VERSION, null, 30*24*60*60, false));
        }
    }
}
