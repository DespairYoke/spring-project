package org.springframework.context.event;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
public abstract class MyAbstractApplicationEventMulticaster implements MyApplicationEventMulticaster, BeanClassLoaderAware, BeanFactoryAware {
}
