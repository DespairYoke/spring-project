package org.springframework.web.servlet.handler;

import org.springframework.web.method.MyHandlerMethod;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
@FunctionalInterface
public interface MyHandlerMethodMappingNamingStrategy<T> {

    String getName(MyHandlerMethod handlerMethod, T mapping);
}
