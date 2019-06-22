package org.springframework.context.support;

import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
class MyApplicationListenerDetector implements DestructionAwareBeanPostProcessor, MergedBeanDefinitionPostProcessor {

    private final transient MyAbstractApplicationContext applicationContext;

    public MyApplicationListenerDetector(MyAbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
