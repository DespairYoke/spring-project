package org.springframework.context.support;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.context.MyConfigurableApplicationContext;
import org.springframework.util.StringValueResolver;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-05
 **/
class MyApplicationContextAwareProcessor implements BeanPostProcessor {

    private final MyConfigurableApplicationContext applicationContext;

    private final StringValueResolver embeddedValueResolver;

    public MyApplicationContextAwareProcessor(MyConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.embeddedValueResolver = new EmbeddedValueResolver(applicationContext.getBeanFactory());
    }
}
