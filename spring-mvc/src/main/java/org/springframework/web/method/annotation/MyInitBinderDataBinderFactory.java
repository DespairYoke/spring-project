package org.springframework.web.method.annotation;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.MyDefaultDataBinderFactory;
import org.springframework.web.bind.support.MyWebBindingInitializer;
import org.springframework.web.method.support.MyInvocableHandlerMethod;

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
}
