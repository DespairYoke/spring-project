package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.MyNativeWebRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyHandlerMethodReturnValueHandlerComposite implements MyHandlerMethodReturnValueHandler{

    private final List<MyHandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>();
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return false;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, MyModelAndViewContainer mavContainer, MyNativeWebRequest webRequest) throws Exception {

        MyHandlerMethodReturnValueHandler handler = selectHandler(returnValue, returnType);
        if (handler == null) {
            throw new IllegalArgumentException("Unknown return value type: " + returnType.getParameterType().getName());
        }
        handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }
    private MyHandlerMethodReturnValueHandler selectHandler(@Nullable Object value, MethodParameter returnType) {

        for (MyHandlerMethodReturnValueHandler handler : this.returnValueHandlers) {
            if (handler.supportsReturnType(returnType)) {
                return handler;
            }
        }
        return null;
    }
    public MyHandlerMethodReturnValueHandlerComposite addHandlers(
            @Nullable List<? extends MyHandlerMethodReturnValueHandler> handlers) {

        if (handlers != null) {
            this.returnValueHandlers.addAll(handlers);
        }
        return this;
    }
}
