package org.springframework.context.event;

import org.springframework.context.MyApplicationContext;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public class MyContextRefreshedEvent extends MyApplicationContextEvent{
    public MyContextRefreshedEvent(MyApplicationContext source) {
        super(source);
    }
}
