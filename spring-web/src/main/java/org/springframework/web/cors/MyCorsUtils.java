package org.springframework.web.cors;

import org.springframework.http.MyHttpHeaders;
import org.springframework.http.MyHttpMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-27
 **/
public class MyCorsUtils {

    public static boolean isCorsRequest(HttpServletRequest request) {
        return (request.getHeader(MyHttpHeaders.ORIGIN) != null);
    }

    /**
     * Returns {@code true} if the request is a valid CORS pre-flight one.
     */
    public static boolean isPreFlightRequest(HttpServletRequest request) {
        return (isCorsRequest(request) && MyHttpMethod.OPTIONS.matches(request.getMethod()) &&
                request.getHeader(MyHttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null);
    }
}
