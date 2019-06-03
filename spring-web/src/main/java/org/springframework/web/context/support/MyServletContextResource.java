package org.springframework.web.context.support;

import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.core.io.ContextResource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.ServletContext;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public class MyServletContextResource extends AbstractFileResolvingResource implements ContextResource {

    private final ServletContext servletContext;

    private final String path;

    public MyServletContextResource(ServletContext servletContext, String path) {
        // check ServletContext
        Assert.notNull(servletContext, "Cannot resolve ServletContextResource without ServletContext");
        this.servletContext = servletContext;

        // check path
        Assert.notNull(path, "Path is required");
        String pathToUse = StringUtils.cleanPath(path);
        if (!pathToUse.startsWith("/")) {
            pathToUse = "/" + pathToUse;
        }
        this.path = pathToUse;

    }


    public final ServletContext getServletContext() {
        return this.servletContext;
    }

    /**
     * Return the path for this resource.
     */
    public final String getPath() {
        return this.path;
    }

    @Override
    public String getPathWithinContext() {
        return null;
    }

    @Override
    public String getDescription() {
        return "ServletContext resource [" + this.path + "]";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = this.servletContext.getResourceAsStream(this.path);
        if (is == null) {
            throw new FileNotFoundException("Could not open " + getDescription());
        }
        return is;
    }

    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

}
