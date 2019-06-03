package org.springframework.web.context;

import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

import javax.servlet.ServletContext;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public interface MyWebApplicationContext extends ApplicationContext {

    String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = MyWebApplicationContext.class.getName() + ".ROOT";

    @Nullable
    ServletContext getServletContext();
}
