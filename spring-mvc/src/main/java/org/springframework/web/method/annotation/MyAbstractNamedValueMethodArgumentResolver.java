package org.springframework.web.method.annotation;

import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.MyRequestScope;
import org.springframework.web.method.support.MyHandlerMethodArgumentResolver;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public abstract class MyAbstractNamedValueMethodArgumentResolver implements MyHandlerMethodArgumentResolver {

    private final ConfigurableBeanFactory configurableBeanFactory;

    private final BeanExpressionContext expressionContext;

    public MyAbstractNamedValueMethodArgumentResolver() {
        this.configurableBeanFactory = null;
        this.expressionContext = null;
    }

    public MyAbstractNamedValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
        this.configurableBeanFactory = beanFactory;
        this.expressionContext =
                (beanFactory != null ? new BeanExpressionContext(beanFactory, new MyRequestScope()) : null);
    }
}
