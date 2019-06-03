package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.MyWebDataBinderFactory;
import org.springframework.web.context.request.MyNativeWebRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public interface MyHandlerMethodArgumentResolver {

    boolean supportsParameter(MethodParameter parameter);

    Object resolveArgument(MethodParameter parameter, @Nullable MyModelAndViewContainer mavContainer,
                           MyNativeWebRequest webRequest, @Nullable MyWebDataBinderFactory binderFactory) throws Exception;
}
