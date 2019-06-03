package org.springframework.web.context;

import org.springframework.beans.factory.Aware;

import javax.servlet.ServletContext;

public interface MyServletContextAware  extends Aware {

    void setServletContext(ServletContext servletContext);
}
