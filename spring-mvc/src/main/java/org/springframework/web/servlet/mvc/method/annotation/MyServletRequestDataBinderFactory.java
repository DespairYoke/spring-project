package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.MyServletRequestDataBinder;
import org.springframework.web.bind.support.MyWebBindingInitializer;
import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.method.annotation.MyInitBinderDataBinderFactory;
import org.springframework.web.method.support.MyInvocableHandlerMethod;

import java.util.List;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-07
 **/
public class MyServletRequestDataBinderFactory extends MyInitBinderDataBinderFactory {
    /**
     * Create a new InitBinderDataBinderFactory instance.
     *
     * @param binderMethods {@code @InitBinder} methods
     * @param initializer   for global data binder initialization
     */
    public MyServletRequestDataBinderFactory(List<MyInvocableHandlerMethod> binderMethods, MyWebBindingInitializer initializer) {
        super(binderMethods, initializer);
    }
    protected MyServletRequestDataBinder createBinderInstance(
            @Nullable Object target, String objectName, MyNativeWebRequest request) throws Exception  {

        return new MyExtendedServletRequestDataBinder(target, objectName);
    }
}
