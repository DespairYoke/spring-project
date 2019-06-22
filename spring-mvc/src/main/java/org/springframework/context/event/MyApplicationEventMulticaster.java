package org.springframework.context.event;

import org.springframework.context.MyApplicationEvent;
import org.springframework.context.MyApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
public interface MyApplicationEventMulticaster {

    void addApplicationListener(MyApplicationListener<?> listener);

    void addApplicationListenerBean(String listenerBeanName);

    void multicastEvent(MyApplicationEvent event);


    void multicastEvent(MyApplicationEvent event, @Nullable ResolvableType eventType);

}
