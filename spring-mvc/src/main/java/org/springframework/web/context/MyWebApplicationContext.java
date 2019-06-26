package org.springframework.web.context;

import org.springframework.context.MyApplicationContext;
import org.springframework.lang.Nullable;

import javax.servlet.ServletContext;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public interface MyWebApplicationContext extends MyApplicationContext {

    String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = MyWebApplicationContext.class.getName() + ".ROOT";

    @Nullable
    ServletContext getServletContext();
}
