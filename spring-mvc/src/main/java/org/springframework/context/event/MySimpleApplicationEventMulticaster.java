package org.springframework.context.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.MyApplicationEvent;
import org.springframework.context.MyApplicationListener;
import org.springframework.core.ResolvableType;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
public class MySimpleApplicationEventMulticaster extends MyAbstractApplicationEventMulticaster{
    public MySimpleApplicationEventMulticaster(BeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void addApplicationListener(MyApplicationListener<?> listener) {

    }

    @Override
    public void addApplicationListenerBean(String listenerBeanName) {

    }

    @Override
    public void multicastEvent(MyApplicationEvent event) {

    }

    @Override
    public void multicastEvent(MyApplicationEvent event, ResolvableType eventType) {

    }
}
