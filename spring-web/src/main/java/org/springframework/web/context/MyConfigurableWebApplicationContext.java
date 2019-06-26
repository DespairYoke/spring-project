package org.springframework.web.context;

import org.springframework.lang.Nullable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public interface MyConfigurableWebApplicationContext extends MyWebApplicationContext, MyConfigurableApplicationContext {

    String APPLICATION_CONTEXT_ID_PREFIX = MyWebApplicationContext.class.getName() + ":";

    void setConfigLocation(String configLocation);

    void setServletContext(@Nullable ServletContext servletContext);

    void setServletConfig(@Nullable ServletConfig servletConfig);

    void setNamespace(@Nullable String namespace);
}