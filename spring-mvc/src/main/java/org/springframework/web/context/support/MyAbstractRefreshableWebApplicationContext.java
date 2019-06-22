package org.springframework.web.context.support;

import org.springframework.context.support.MyAbstractRefreshableConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.MyThemeSource;
import org.springframework.util.Assert;
import org.springframework.web.context.MyConfigurableWebApplicationContext;

import javax.servlet.ServletContext;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-25
 **/
public abstract class MyAbstractRefreshableWebApplicationContext extends MyAbstractRefreshableConfigApplicationContext
        implements MyConfigurableWebApplicationContext, MyThemeSource {

    private ServletContext servletContext;
    @Override
    protected Resource getResourceByPath(String path) {
        Assert.state(this.servletContext != null, "No ServletContext available");

        Resource resource = new MyServletContextResource(this.servletContext, path);
        System.out.println(resource);
        return resource;
    }
    @Override
    public void setServletContext(@Nullable ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
