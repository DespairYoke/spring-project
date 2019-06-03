package org.springframework.web.context.support;

import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.MyWebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public class MyWebApplicationContextUtils {



    @Nullable
    public static MyWebApplicationContext getWebApplicationContext(ServletContext sc) {
        return getWebApplicationContext(sc, MyWebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    }

    @Nullable
    public static MyWebApplicationContext getWebApplicationContext(ServletContext sc, String attrName) {
        Assert.notNull(sc, "ServletContext must not be null");
        Object attr = sc.getAttribute(attrName);
        if (attr == null) {
            return null;
        }
        if (attr instanceof RuntimeException) {
            throw (RuntimeException) attr;
        }
        if (attr instanceof Error) {
            throw (Error) attr;
        }
        if (attr instanceof Exception) {
            throw new IllegalStateException((Exception) attr);
        }
        if (!(attr instanceof MyWebApplicationContext)) {
            throw new IllegalStateException("Context attribute is not of type WebApplicationContext: " + attr);
        }
        return (MyWebApplicationContext) attr;
    }


    public static void initServletPropertySources(MutablePropertySources sources,
                                                  @Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {

        Assert.notNull(sources, "'propertySources' must not be null");
        String name = MyStandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME;
        if (servletContext != null && sources.contains(name) && sources.get(name) instanceof PropertySource.StubPropertySource) {
            sources.replace(name, new MyServletContextPropertySource(name, servletContext));
        }
        name = MyStandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME;
        if (servletConfig != null && sources.contains(name) && sources.get(name) instanceof PropertySource.StubPropertySource) {
            sources.replace(name, new MyServletConfigPropertySource(name, servletConfig));
        }
    }

}
