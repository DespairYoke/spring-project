package org.springframework.web.context.support;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.web.context.MyWebApplicationContext;

import javax.servlet.ServletContext;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-27
 **/
public abstract class MyWebApplicationObjectSupport extends ApplicationObjectSupport  {

    private ServletContext servletContext;


    protected void initServletContext(ServletContext servletContext) {
    }

    protected final MyWebApplicationContext getWebApplicationContext() throws IllegalStateException {
        ApplicationContext ctx = getApplicationContext();
        if (ctx instanceof MyWebApplicationContext) {
            return (MyWebApplicationContext) getApplicationContext();
        }
        else if (isContextRequired()) {
            throw new IllegalStateException("WebApplicationObjectSupport instance [" + this +
                    "] does not run in a WebApplicationContext but in: " + ctx);
        }
        else {
            return null;
        }
    }
    protected final ServletContext getServletContext() throws IllegalStateException {
        if (this.servletContext != null) {
            return this.servletContext;
        }
        ServletContext servletContext = null;
        MyWebApplicationContext wac = getWebApplicationContext();
        if (wac != null) {
            servletContext = wac.getServletContext();
        }
        if (servletContext == null && isContextRequired()) {
            throw new IllegalStateException("WebApplicationObjectSupport instance [" + this +
                    "] does not run within a ServletContext. Make sure the object is fully configured!");
        }
        return servletContext;
    }

}
