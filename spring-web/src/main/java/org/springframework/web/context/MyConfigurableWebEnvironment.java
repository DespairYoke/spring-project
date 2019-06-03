package org.springframework.web.context;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public interface MyConfigurableWebEnvironment extends ConfigurableEnvironment {
    void initPropertySources(@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig);
}
