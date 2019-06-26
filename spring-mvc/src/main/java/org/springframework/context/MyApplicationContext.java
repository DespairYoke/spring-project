package org.springframework.context;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public interface MyApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
        MyMessageSource, MyApplicationEventPublisher, ResourcePatternResolver {

    String getId();

    String getDisplayName();

    AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;
}
