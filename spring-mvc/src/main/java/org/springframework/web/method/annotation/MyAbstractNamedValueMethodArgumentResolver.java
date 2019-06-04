package org.springframework.web.method.annotation;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.MyValueConstants;
import org.springframework.web.bind.support.MyWebDataBinderFactory;
import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.context.request.MyRequestScope;
import org.springframework.web.method.support.MyHandlerMethodArgumentResolver;
import org.springframework.web.method.support.MyModelAndViewContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public abstract class MyAbstractNamedValueMethodArgumentResolver implements MyHandlerMethodArgumentResolver {

    private final ConfigurableBeanFactory configurableBeanFactory;

    private final BeanExpressionContext expressionContext;

    private final Map<MethodParameter, NamedValueInfo> namedValueInfoCache = new ConcurrentHashMap<>(256);
    public MyAbstractNamedValueMethodArgumentResolver() {
        this.configurableBeanFactory = null;
        this.expressionContext = null;
    }

    public MyAbstractNamedValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
        this.configurableBeanFactory = beanFactory;
        this.expressionContext =
                (beanFactory != null ? new BeanExpressionContext(beanFactory, new MyRequestScope()) : null);
    }

    protected abstract Object resolveName(String name, MethodParameter parameter, MyNativeWebRequest request)
            throws Exception;

    public final Object resolveArgument(MethodParameter parameter, @Nullable MyModelAndViewContainer mavContainer,
                                        MyNativeWebRequest webRequest, @Nullable MyWebDataBinderFactory binderFactory) throws Exception {

        NamedValueInfo namedValueInfo = getNamedValueInfo(parameter);
        MethodParameter nestedParameter = parameter.nestedIfOptional();

        Object resolvedName = resolveStringValue(namedValueInfo.name);
        if (resolvedName == null) {
            throw new IllegalArgumentException(
                    "Specified name must not resolve to null: [" + namedValueInfo.name + "]");
        }

        Object arg = resolveName(resolvedName.toString(), nestedParameter, webRequest);


        handleResolvedValue(arg, namedValueInfo.name, parameter, mavContainer, webRequest);

        return arg;
    }

    protected void handleResolvedValue(@Nullable Object arg, String name, MethodParameter parameter,
                                       @Nullable MyModelAndViewContainer mavContainer, MyNativeWebRequest webRequest) {
    }


    @Nullable
    private Object resolveStringValue(String value) {
        if (this.configurableBeanFactory == null) {
            return value;
        }
        String placeholdersResolved = this.configurableBeanFactory.resolveEmbeddedValue(value);
        BeanExpressionResolver exprResolver = this.configurableBeanFactory.getBeanExpressionResolver();
        if (exprResolver == null || this.expressionContext == null) {
            return value;
        }
        return exprResolver.evaluate(placeholdersResolved, this.expressionContext);
    }

    private NamedValueInfo getNamedValueInfo(MethodParameter parameter) {
        NamedValueInfo namedValueInfo = this.namedValueInfoCache.get(parameter);
        if (namedValueInfo == null) {
            namedValueInfo = createNamedValueInfo(parameter);
            namedValueInfo = updateNamedValueInfo(parameter, namedValueInfo);
            this.namedValueInfoCache.put(parameter, namedValueInfo);
        }
        return namedValueInfo;
    }

    protected abstract NamedValueInfo createNamedValueInfo(MethodParameter parameter);


    private NamedValueInfo updateNamedValueInfo(MethodParameter parameter, NamedValueInfo info) {
        String name = info.name;
        if (info.name.isEmpty()) {
            name = parameter.getParameterName();
            if (name == null) {
                throw new IllegalArgumentException(
                        "Name for argument type [" + parameter.getNestedParameterType().getName() +
                                "] not available, and parameter name information not found in class file either.");
            }
        }
        String defaultValue = (MyValueConstants.DEFAULT_NONE.equals(info.defaultValue) ? null : info.defaultValue);
        return new NamedValueInfo(name, info.required, defaultValue);
    }
    protected static class NamedValueInfo {

        private final String name;

        private final boolean required;

        @Nullable
        private final String defaultValue;

        public NamedValueInfo(String name, boolean required, @Nullable String defaultValue) {
            this.name = name;
            this.required = required;
            this.defaultValue = defaultValue;
        }
    }
}
