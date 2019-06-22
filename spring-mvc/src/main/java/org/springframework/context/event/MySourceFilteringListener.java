package org.springframework.context.event;

import org.springframework.context.MyApplicationListener;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public class MySourceFilteringListener implements MyGenericApplicationListener, SmartApplicationListener{

    public MySourceFilteringListener(Object source, MyApplicationListener<?> delegate) {
        this.source = source;
        this.delegate = (delegate instanceof GenericApplicationListener ?
                (GenericApplicationListener) delegate : new GenericApplicationListenerAdapter(delegate));
    }

}
