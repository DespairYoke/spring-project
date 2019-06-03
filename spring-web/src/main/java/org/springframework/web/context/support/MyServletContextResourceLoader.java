package org.springframework.web.context.support;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import javax.servlet.ServletContext;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public class MyServletContextResourceLoader extends DefaultResourceLoader {

    private final ServletContext servletContext;

    public MyServletContextResourceLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
    @Override
    protected Resource getResourceByPath(String path) {
        return new MyServletContextResource(this.servletContext, path);
    }
}
