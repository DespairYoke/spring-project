package org.springframework.web.context.support;

import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.context.MyConfigurableWebEnvironment;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public class MyStandardServletEnvironment  extends StandardEnvironment implements MyConfigurableWebEnvironment {

    /** Servlet context init parameters property source name: {@value} */
    public static final String SERVLET_CONTEXT_PROPERTY_SOURCE_NAME = "servletContextInitParams";

    /** Servlet config init parameters property source name: {@value} */
    public static final String SERVLET_CONFIG_PROPERTY_SOURCE_NAME = "servletConfigInitParams";

    @Override
    public void initPropertySources(ServletContext servletContext, ServletConfig servletConfig) {

        MyWebApplicationContextUtils.initServletPropertySources(getPropertySources(), servletContext, servletConfig);
    }
}
