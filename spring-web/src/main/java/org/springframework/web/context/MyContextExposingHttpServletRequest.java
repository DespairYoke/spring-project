package org.springframework.web.context;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Set;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
public class MyContextExposingHttpServletRequest  extends HttpServletRequestWrapper {


    private final MyWebApplicationContext webApplicationContext;

    @Nullable
    private final Set<String> exposedContextBeanNames;


    public MyContextExposingHttpServletRequest(HttpServletRequest originalRequest, MyWebApplicationContext context,
                                             @Nullable Set<String> exposedContextBeanNames) {

        super(originalRequest);
        Assert.notNull(context, "WebApplicationContext must not be null");
        this.webApplicationContext = context;
        this.exposedContextBeanNames = exposedContextBeanNames;
    }
}
