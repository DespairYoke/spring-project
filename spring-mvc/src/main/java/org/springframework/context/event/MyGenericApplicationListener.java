package org.springframework.context.event;

import org.springframework.context.MyApplicationEvent;
import org.springframework.context.MyApplicationListener;
import org.springframework.core.Ordered;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public interface MyGenericApplicationListener extends MyApplicationListener<MyApplicationEvent>, Ordered {
}
