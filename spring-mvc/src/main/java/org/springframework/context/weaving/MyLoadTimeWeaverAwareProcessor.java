package org.springframework.context.weaving;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
public class MyLoadTimeWeaverAwareProcessor implements BeanPostProcessor, BeanFactoryAware {

    private BeanFactory beanFactory;

    public MyLoadTimeWeaverAwareProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }
}
