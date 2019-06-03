package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.MyWebDataBinder;
import org.springframework.web.context.request.MyNativeWebRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
public class MyDefaultDataBinderFactory implements MyWebDataBinderFactory{

    private final MyWebBindingInitializer initializer;

    public MyDefaultDataBinderFactory(@Nullable MyWebBindingInitializer initializer) {
        this.initializer = initializer;
    }

    @Override
    public MyWebDataBinder createBinder(MyNativeWebRequest webRequest, Object target, String objectName) throws Exception {
        return null;
    }
}
