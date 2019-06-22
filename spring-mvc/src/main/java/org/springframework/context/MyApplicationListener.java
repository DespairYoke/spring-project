package org.springframework.context;

import java.util.EventListener;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
@FunctionalInterface
public interface MyApplicationListener<E extends MyApplicationEvent> extends EventListener {

    /**
     * Handle an application event.
     * @param event the event to respond to
     */
    void onApplicationEvent(E event);

}