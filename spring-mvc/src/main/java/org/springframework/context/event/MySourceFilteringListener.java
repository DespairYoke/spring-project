package org.springframework.context.event;

import org.springframework.context.MyApplicationEvent;
import org.springframework.context.MyApplicationListener;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public class MySourceFilteringListener implements MyGenericApplicationListener, MySmartApplicationListener{

    public MySourceFilteringListener(Object source, MyApplicationListener<?> delegate) {
//        this.source = source;
//        this.delegate = (delegate instanceof MyGenericApplicationListener ?
//                (MyGenericApplicationListener) delegate : new MyGenericApplicationListenerAdapter(delegate));
    }

    @Override
    public boolean supportsEventType(Class<? extends MyApplicationEvent> eventType) {
        return false;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return false;
    }

    @Override
    public void onApplicationEvent(MyApplicationEvent event) {

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
