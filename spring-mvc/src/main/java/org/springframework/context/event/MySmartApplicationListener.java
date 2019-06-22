package org.springframework.context.event;

import org.springframework.context.MyApplicationEvent;
import org.springframework.context.MyApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public interface MySmartApplicationListener extends MyApplicationListener<MyApplicationEvent>, Ordered {

    boolean supportsEventType(Class<? extends MyApplicationEvent> eventType);

    /**
     * Determine whether this listener actually supports the given source type.
     */
    boolean supportsSourceType(@Nullable Class<?> sourceType);
}
