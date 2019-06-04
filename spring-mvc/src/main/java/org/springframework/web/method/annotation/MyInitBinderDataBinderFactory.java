package org.springframework.web.method.annotation;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.MyWebDataBinder;
import org.springframework.web.bind.annotation.MyInitBinder;
import org.springframework.web.bind.support.MyDefaultDataBinderFactory;
import org.springframework.web.bind.support.MyWebBindingInitializer;
import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.method.MyHandlerMethod;
import org.springframework.web.method.support.MyInvocableHandlerMethod;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
public class MyInitBinderDataBinderFactory extends MyDefaultDataBinderFactory {


    private final List<MyInvocableHandlerMethod> binderMethods;



    public MyInitBinderDataBinderFactory(@Nullable List<MyInvocableHandlerMethod> binderMethods,
                                       @Nullable MyWebBindingInitializer initializer) {

        super(initializer);
        this.binderMethods = (binderMethods != null ? binderMethods : Collections.emptyList());
    }

    @Override
    public void initBinder(MyWebDataBinder binder, MyNativeWebRequest request) throws Exception {
        for (MyInvocableHandlerMethod binderMethod : this.binderMethods) {
            if (isBinderMethodApplicable(binderMethod, binder)) {
                Object returnValue = binderMethod.invokeForRequest(request, null, binder);
                if (returnValue != null) {
                    throw new IllegalStateException(
                            "@InitBinder methods should return void: " + binderMethod);
                }
            }
        }
    }

    protected boolean isBinderMethodApplicable(MyHandlerMethod binderMethod, MyWebDataBinder binder) {
        MyInitBinder ann = binderMethod.getMethodAnnotation(MyInitBinder.class);
        Assert.state(ann != null, "No InitBinder annotation");
        Collection<String> names = Arrays.asList(ann.value());
        return (names.isEmpty() || names.contains(binder.getObjectName()));
    }
}
