package org.springframework.web.context.request;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public class MyServletRequestAttributes extends MyAbstractRequestAttributes{

    private final HttpServletRequest request;

    @Nullable
    private HttpServletResponse response;


    public MyServletRequestAttributes(HttpServletRequest request) {
        Assert.notNull(request, "Request must not be null");
        this.request = request;
    }

    public MyServletRequestAttributes(HttpServletRequest request, @Nullable HttpServletResponse response) {
        this(request);
        this.response = response;
    }


    @Override
    protected void updateAccessedSessionAttributes() {

    }

    public final HttpServletResponse getResponse() {
        return this.response;
    }

    @Override
    public Object getAttribute(String name, int scope) {
        return null;
    }

    public final HttpServletRequest getRequest() {
        return this.request;
    }

}
