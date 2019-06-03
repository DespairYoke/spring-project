package org.springframework.web.method.annotation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.MyWebDataBinderFactory;
import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.method.support.MyModelAndViewContainer;
import org.springframework.web.method.support.MyUriComponentsContributor;

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
    public boolean supportsParameter(MethodParameter parameter) {
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, MyModelAndViewContainer mavContainer, MyNativeWebRequest webRequest, MyWebDataBinderFactory binderFactory) throws Exception {
        return null;
    }
}
