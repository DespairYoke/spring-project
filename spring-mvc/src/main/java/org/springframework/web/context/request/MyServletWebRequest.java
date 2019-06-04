package org.springframework.web.context.request;

import org.springframework.lang.Nullable;
import org.springframework.web.util.MyWebUtils;

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
    public <T> T getNativeRequest(Class<T> requiredType) {
        return MyWebUtils.getNativeRequest(getRequest(), requiredType);
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
        return getRequest().getParameterValues(paramName);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return null;
    }

    public boolean isNotModified() {
        return this.notModified;
    }

    @Override
    @Nullable
    public String getHeader(String headerName) {
        return getRequest().getHeader(headerName);
    }
}
