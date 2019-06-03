package org.springframework.web.method.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.MyWebDataBinderFactory;
import org.springframework.web.context.request.MyNativeWebRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyHandlerMethodArgumentResolverComposite implements MyHandlerMethodArgumentResolver{

    protected final Log logger = LogFactory.getLog(getClass());

    private final List<MyHandlerMethodArgumentResolver> argumentResolvers = new LinkedList<>();

    private final Map<MethodParameter, MyHandlerMethodArgumentResolver> argumentResolverCache =
            new ConcurrentHashMap<>(256);



    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return false;
    }

    public Object resolveArgument(MethodParameter parameter, @Nullable MyModelAndViewContainer mavContainer,
                                  MyNativeWebRequest webRequest, @Nullable MyWebDataBinderFactory binderFactory) throws Exception {

        MyHandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
        if (resolver == null) {
            throw new IllegalArgumentException("Unknown parameter type [" + parameter.getParameterType().getName() + "]");
        }
        return resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }

    private MyHandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
        MyHandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
        if (result == null) {
            for (MyHandlerMethodArgumentResolver methodArgumentResolver : this.argumentResolvers) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Testing if argument resolver [" + methodArgumentResolver + "] supports [" +
                            parameter.getGenericParameterType() + "]");
                }
                if (methodArgumentResolver.supportsParameter(parameter)) {
                    result = methodArgumentResolver;
                    this.argumentResolverCache.put(parameter, result);
                    break;
                }
            }
        }
        return result;
    }
    public MyHandlerMethodArgumentResolverComposite addResolvers(
            @Nullable List<? extends MyHandlerMethodArgumentResolver> resolvers) {

        if (resolvers != null) {
            for (MyHandlerMethodArgumentResolver resolver : resolvers) {
                this.argumentResolvers.add(resolver);
            }
        }
        return this;
    }
}
