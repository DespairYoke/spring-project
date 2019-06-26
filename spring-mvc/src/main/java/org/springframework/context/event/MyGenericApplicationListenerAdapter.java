package org.springframework.context.event;

import org.springframework.context.MyApplicationListener;
import org.springframework.util.Assert;

public class MyGenericApplicationListenerAdapter {

    public MyGenericApplicationListenerAdapter(MyApplicationListener<?> delegate) {
    }
}
