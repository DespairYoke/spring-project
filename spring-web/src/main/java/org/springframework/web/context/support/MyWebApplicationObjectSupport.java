package org.springframework.web.context.support;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.web.context.MyServletContextAware;
import org.springframework.web.context.MyWebApplicationContext;

import javax.servlet.ServletContext;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-27
 **/
public abstract class MyWebApplicationObjectSupport extends ApplicationObjectSupport implements MyServletContextAware {

    private ServletContext servletContext;

    @Override
    public final void setServletContext(ServletContext servletContext) {
        if (servletContext != this.servletContext) {
            this.servletContext = servletContext;
            initServletContext(servletContext);
        }
    }

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
