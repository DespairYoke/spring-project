package org.springframework.context.event;

import org.springframework.beans.factory.BeanFactory;

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

}
