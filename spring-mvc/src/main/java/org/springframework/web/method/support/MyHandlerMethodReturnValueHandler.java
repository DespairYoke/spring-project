package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.MyNativeWebRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public interface MyHandlerMethodReturnValueHandler {

    boolean supportsReturnType(MethodParameter returnType);

    void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
                           MyModelAndViewContainer mavContainer, MyNativeWebRequest webRequest) throws Exception;
}
