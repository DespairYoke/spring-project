package org.springframework.context.expression;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.Nullable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-05
 **/
public class MyStandardBeanExpressionResolver implements BeanExpressionResolver {

    private ExpressionParser expressionParser;

    public MyStandardBeanExpressionResolver() {
        this.expressionParser = new SpelExpressionParser();
    }

    /**
     * Create a new {@code StandardBeanExpressionResolver} with the given bean class loader,
     * using it as the basis for expression compilation.
     * @param beanClassLoader the factory's bean class loader
     */
    public MyStandardBeanExpressionResolver(@Nullable ClassLoader beanClassLoader) {
        this.expressionParser = new SpelExpressionParser(new SpelParserConfiguration(null, beanClassLoader));
    }

    @Override
    public Object evaluate(String s, BeanExpressionContext beanExpressionContext) throws BeansException {
        return null;
    }
}
