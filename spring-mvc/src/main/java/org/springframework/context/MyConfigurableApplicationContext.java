package org.springframework.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;

import java.io.Closeable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public interface MyConfigurableApplicationContext extends MyApplicationContext, Lifecycle, Closeable {

    String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";

    String ENVIRONMENT_BEAN_NAME = "environment";

    /**
     * Name of the System properties bean in the factory.
     * @see java.lang.System#getProperties()
     */
    String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";

    String CONVERSION_SERVICE_BEAN_NAME = "conversionService";

    /**
     * Name of the System environment bean in the factory.
     * @see java.lang.System#getenv()
     */
    String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";

    void refresh() throws BeansException, IllegalStateException;

    void setEnvironment(ConfigurableEnvironment environment);

    void setParent(@Nullable MyApplicationContext parent);

    void setId(String id);

    void addApplicationListener(MyApplicationListener<?> listener);

    ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;
}
