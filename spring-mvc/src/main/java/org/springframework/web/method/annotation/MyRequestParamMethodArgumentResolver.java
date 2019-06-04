package org.springframework.web.method.annotation;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.MyRequestParam;
import org.springframework.web.bind.annotation.MyValueConstants;
import org.springframework.web.bind.support.MyWebDataBinderFactory;
import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.context.request.MyRequestAttributes;
import org.springframework.web.method.support.MyModelAndViewContainer;
import org.springframework.web.method.support.MyUriComponentsContributor;
import org.springframework.web.servlet.MyView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyRequestParamMethodArgumentResolver extends MyAbstractNamedValueMethodArgumentResolver
        implements MyUriComponentsContributor {

    private final boolean useDefaultResolution;

    public MyRequestParamMethodArgumentResolver(boolean useDefaultResolution) {
        this.useDefaultResolution = useDefaultResolution;
    }

    public MyRequestParamMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory,
                                              boolean useDefaultResolution) {

        super(beanFactory);
        this.useDefaultResolution = useDefaultResolution;
    }
    @Override
    protected Object resolveName(String name, MethodParameter parameter, MyNativeWebRequest request) throws Exception {

        Object arg = null;


        if (arg == null) {
            String[] paramValues = request.getParameterValues(name);
            if (paramValues != null) {
                arg = (paramValues.length == 1 ? paramValues[0] : paramValues);
            }
        }
        return arg;
    }
    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        if (this.useDefaultResolution) {
           return BeanUtils.isSimpleProperty(parameter.getNestedParameterType());
        }else {
            return false;
        }
    }
    @Override
    @SuppressWarnings("unchecked")
    protected void handleResolvedValue(@Nullable Object arg, String name, MethodParameter parameter,
                                       @Nullable MyModelAndViewContainer mavContainer, MyNativeWebRequest request) {

        String key = MyView.PATH_VARIABLES;
        int scope = MyRequestAttributes.SCOPE_REQUEST;
        Map<String, Object> pathVars = (Map<String, Object>) request.getAttribute(key, scope);
        if (pathVars == null) {
            pathVars = new HashMap<>();
//            request.setAttribute(key, pathVars, scope);
        }
        pathVars.put(name, arg);
    }

    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        MyRequestParam ann = parameter.getParameterAnnotation(MyRequestParam.class);
        return (ann != null ? new RequestParamNamedValueInfo(ann) : new RequestParamNamedValueInfo());
    }
    private static class RequestParamNamedValueInfo extends NamedValueInfo {


        public RequestParamNamedValueInfo() {
            super("", false, MyValueConstants.DEFAULT_NONE);
        }

        public RequestParamNamedValueInfo(MyRequestParam annotation) {
            super(annotation.name(), annotation.required(), annotation.defaultValue());
        }
    }
}
