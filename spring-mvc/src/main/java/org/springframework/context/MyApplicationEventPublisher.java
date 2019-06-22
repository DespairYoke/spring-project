package org.springframework.context;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
@FunctionalInterface
public interface MyApplicationEventPublisher {

    default void publishEvent(MyApplicationEvent event) {
        publishEvent((Object) event);
    }

    void publishEvent(Object event);
}
