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
    @SuppressWarnings("deprecation")
    public final MyWebDataBinder createBinder(
            MyNativeWebRequest webRequest, @Nullable Object target, String objectName) throws Exception {

        MyWebDataBinder dataBinder = createBinderInstance(target, objectName, webRequest);
        if (this.initializer != null) {
            this.initializer.initBinder(dataBinder, webRequest);
        }
        initBinder(dataBinder, webRequest);
        return dataBinder;
    }
    protected MyWebDataBinder createBinderInstance(
            @Nullable Object target, String objectName, MyNativeWebRequest webRequest) throws Exception {

        return new MyWebRequestDataBinder(target, objectName);
    }

    protected void initBinder(MyWebDataBinder dataBinder, MyNativeWebRequest webRequest)
            throws Exception {

    }

}
