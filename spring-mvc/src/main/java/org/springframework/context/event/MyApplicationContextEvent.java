package org.springframework.context.event;

import org.springframework.context.MyApplicationContext;
import org.springframework.context.MyApplicationEvent;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public abstract class MyApplicationContextEvent extends MyApplicationEvent {

    public MyApplicationContextEvent(MyApplicationContext source) {
        super(source);
    }
    public final MyApplicationContext getApplicationContext() {
        return (MyApplicationContext) getSource();
    }
}
