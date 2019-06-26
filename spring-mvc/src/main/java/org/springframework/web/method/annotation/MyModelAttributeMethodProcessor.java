package org.springframework.web.method.annotation;

import org.springframework.beans.BeanUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Assert;
import org.springframework.validation.MyBindException;
import org.springframework.validation.MyBindingResult;
import org.springframework.validation.MyErrors;
import org.springframework.web.bind.MyWebDataBinder;
import org.springframework.web.bind.annotation.MyModelAttribute;
import org.springframework.web.bind.support.MyWebDataBinderFactory;
import org.springframework.web.bind.support.MyWebRequestDataBinder;
import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.method.support.MyHandlerMethodArgumentResolver;
import org.springframework.web.method.support.MyHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.MyModelAndViewContainer;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.net.BindException;
import java.util.Map;
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
        return (parameter.hasParameterAnnotation(MyModelAttribute.class) ||
                (this.annotationNotRequired && !BeanUtils.isSimpleProperty(parameter.getParameterType())));
    }


    protected void bindRequestParameters(MyWebDataBinder binder, MyNativeWebRequest request) {
        ((MyWebRequestDataBinder) binder).bind(request);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, MyModelAndViewContainer mavContainer, MyNativeWebRequest webRequest, MyWebDataBinderFactory binderFactory) throws Exception {
        Assert.state(mavContainer != null, "ModelAttributeMethodProcessor requires ModelAndViewContainer");
        Assert.state(binderFactory != null, "ModelAttributeMethodProcessor requires WebDataBinderFactory");

        String name = MyModelFactory.getNameForParameter(parameter);
        MyModelAttribute ann = parameter.getParameterAnnotation(MyModelAttribute.class);
        if (ann != null) {
            mavContainer.setBinding(name, ann.binding());
        }

        Object attribute = null;
        MyBindingResult bindingResult = null;

        if (mavContainer.containsAttribute(name)) {
            attribute = mavContainer.getModel().get(name);
        }
        else {
            // Create attribute instance
            try {
                attribute = createAttribute(name, parameter, binderFactory, webRequest);
            }
            catch (MyBindException ex) {
                if (isBindExceptionRequired(parameter)) {
                    // No BindingResult parameter -> fail with BindException
                    throw ex;
                }
                // Otherwise, expose null/empty value and associated BindingResult
                if (parameter.getParameterType() == Optional.class) {
                    attribute = Optional.empty();
                }
                bindingResult = ex.getBindingResult();
            }
        }

        if (bindingResult == null) {
//            // Bean property binding and validation;
//            // skipped in case of binding failure on construction.
//            MyWebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);
//            if (binder.getTarget() != null) {
//                if (!mavContainer.isBindingDisabled(name)) {
//                    bindRequestParameters(binder, webRequest);
//                }
//                if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
//                    throw new BindException(binder.getBindingResult());
//                }
//            }
//            // Value type adaptation, also covering java.util.Optional
//            if (!parameter.getParameterType().isInstance(attribute)) {
//                attribute = binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType(), parameter);
//            }
//            bindingResult = binder.getBindingResult();
        }

        // Add resolved attribute and BindingResult at the end of the model
        Map<String, Object> bindingResultModel = bindingResult.getModel();
//        mavContainer.removeAttributes(bindingResultModel);
        mavContainer.addAllAttributes(bindingResultModel);

        return attribute;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return false;
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, MyModelAndViewContainer mavContainer, MyNativeWebRequest webRequest) throws Exception {

    }

    protected boolean isBindExceptionRequired(MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getExecutable().getParameterTypes();
        boolean hasBindingResult = (paramTypes.length > (i + 1) && MyErrors.class.isAssignableFrom(paramTypes[i + 1]));
        return !hasBindingResult;
    }

    protected boolean isBindExceptionRequired(MyWebDataBinder binder, MethodParameter parameter) {
        return isBindExceptionRequired(parameter);
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

        //走这里
        if (ctor.getParameterCount() == 0) {
            // A single default constructor -> clearly a standard JavaBeans arrangement.
            return BeanUtils.instantiateClass(ctor);
        }

     return null;
    }


//    protected void bindRequestParameters(MyWebDataBinder binder, MyNativeWebRequest request) {
//        ((MyWebRequestDataBinder) binder).bind(request);
//    }
}
