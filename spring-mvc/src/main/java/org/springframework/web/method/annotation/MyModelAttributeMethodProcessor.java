package org.springframework.web.method.annotation;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Assert;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MyWebDataBinder;
import org.springframework.web.bind.support.MyWebDataBinderFactory;
import org.springframework.web.bind.support.MyWebRequestDataBinder;
import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.method.support.MyHandlerMethodArgumentResolver;
import org.springframework.web.method.support.MyHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.MyModelAndViewContainer;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.util.Optional;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyModelAttributeMethodProcessor implements MyHandlerMethodArgumentResolver, MyHandlerMethodReturnValueHandler {

    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final boolean annotationNotRequired;

    public MyModelAttributeMethodProcessor(boolean annotationNotRequired) {
        this.annotationNotRequired = annotationNotRequired;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, MyModelAndViewContainer mavContainer, MyNativeWebRequest webRequest, MyWebDataBinderFactory binderFactory) throws Exception {
        return null;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return false;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, MyModelAndViewContainer mavContainer, MyNativeWebRequest webRequest) throws Exception {

    }

    protected Object createAttribute(String attributeName, MethodParameter parameter,
                                     MyWebDataBinderFactory binderFactory, MyNativeWebRequest webRequest) throws Exception {

        MethodParameter nestedParameter = parameter.nestedIfOptional();
        Class<?> clazz = nestedParameter.getNestedParameterType();

        Constructor<?> ctor = BeanUtils.findPrimaryConstructor(clazz);
        if (ctor == null) {
            Constructor<?>[] ctors = clazz.getConstructors();
            if (ctors.length == 1) {
                ctor = ctors[0];
            }
            else {
                try {
                    ctor = clazz.getDeclaredConstructor();
                }
                catch (NoSuchMethodException ex) {
                    throw new IllegalStateException("No primary or default constructor found for " + clazz, ex);
                }
            }
        }

        Object attribute = constructAttribute(ctor, attributeName, binderFactory, webRequest);
        if (parameter != nestedParameter) {
            attribute = Optional.of(attribute);
        }
        return attribute;
    }


    protected Object constructAttribute(Constructor<?> ctor, String attributeName,
                                        MyWebDataBinderFactory binderFactory, MyNativeWebRequest webRequest) throws Exception {

        if (ctor.getParameterCount() == 0) {
            // A single default constructor -> clearly a standard JavaBeans arrangement.
            return BeanUtils.instantiateClass(ctor);
        }

        // A single data class constructor -> resolve constructor arguments from request parameters.
        ConstructorProperties cp = ctor.getAnnotation(ConstructorProperties.class);
        String[] paramNames = (cp != null ? cp.value() : parameterNameDiscoverer.getParameterNames(ctor));
        Assert.state(paramNames != null, () -> "Cannot resolve parameter names for constructor " + ctor);
        Class<?>[] paramTypes = ctor.getParameterTypes();
        Assert.state(paramNames.length == paramTypes.length,
                () -> "Invalid number of parameter names: " + paramNames.length + " for constructor " + ctor);

        Object[] args = new Object[paramTypes.length];
        MyWebDataBinder binder = binderFactory.createBinder(webRequest, null, attributeName);
        String fieldDefaultPrefix = binder.getFieldDefaultPrefix();
        String fieldMarkerPrefix = binder.getFieldMarkerPrefix();
        boolean bindingFailure = false;

        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            Class<?> paramType = paramTypes[i];
            Object value = webRequest.getParameterValues(paramName);
            if (value == null) {
                if (fieldDefaultPrefix != null) {
                    value = webRequest.getParameter(fieldDefaultPrefix + paramName);
                }
                if (value == null && fieldMarkerPrefix != null) {
                    if (webRequest.getParameter(fieldMarkerPrefix + paramName) != null) {
                        value = binder.getEmptyValue(paramType);
                    }
                }
            }
            try {
                MethodParameter methodParam = new MethodParameter(ctor, i);
                if (value == null && methodParam.isOptional()) {
                    args[i] = (methodParam.getParameterType() == Optional.class ? Optional.empty() : null);
                }
                else {
                    args[i] = binder.convertIfNecessary(value, paramType, methodParam);
                }
            }
            catch (TypeMismatchException ex) {
                ex.initPropertyName(paramName);
                binder.getBindingErrorProcessor().processPropertyAccessException(ex, binder.getBindingResult());
                bindingFailure = true;
                args[i] = value;
            }
        }

        if (bindingFailure) {
            if (binder.getBindingResult() instanceof AbstractBindingResult) {
                AbstractBindingResult result = (AbstractBindingResult) binder.getBindingResult();
                for (int i = 0; i < paramNames.length; i++) {
                    result.recordFieldValue(paramNames[i], paramTypes[i], args[i]);
                }
            }
            throw new BindException(binder.getBindingResult());
        }

        return BeanUtils.instantiateClass(ctor, args);
    }


//    protected void bindRequestParameters(MyWebDataBinder binder, MyNativeWebRequest request) {
//        ((MyWebRequestDataBinder) binder).bind(request);
//    }
}
