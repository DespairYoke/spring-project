package org.springframework.web.bind.support;

import org.springframework.web.bind.MyWebDataBinder;
import org.springframework.web.context.request.MyWebRequest;

public interface MyWebBindingInitializer {

    void initBinder(MyWebDataBinder binder);

    @Deprecated
    default void initBinder(MyWebDataBinder binder, MyWebRequest request) {
        initBinder(binder);
    }
}
