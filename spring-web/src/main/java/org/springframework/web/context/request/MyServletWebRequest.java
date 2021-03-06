package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-27
 **/
public class MyServletWebRequest extends MyServletRequestAttributes implements MyNativeWebRequest{


    private boolean notModified = false;

    public MyServletWebRequest(HttpServletRequest request) {
        super(request);
    }

    /**
     * Create a new ServletWebRequest instance for the given request/response pair.
     * @param request current HTTP request
     * @param response current HTTP response (for automatic last-modified handling)
     */
    public MyServletWebRequest(HttpServletRequest request, @Nullable HttpServletResponse response) {
        super(request, response);
    }

    @Override
    public Object getNativeRequest() {
        return null;
    }

    @Override
    public <T> T getNativeRequest(Class<T> requiredType) {
        return null;
    }

    @Override
    public <T> T getNativeResponse(Class<T> requiredType) {
        return null;
    }

    @Override
    public String getParameter(String paramName) {
        return null;
    }

    @Override
    public String[] getParameterValues(String paramName) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return null;
    }

    public boolean isNotModified() {
        return this.notModified;
    }
}
