package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;

import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.method.support.MyHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.MyModelAndViewContainer;
import org.springframework.web.servlet.MySmartView;
import org.springframework.web.servlet.MyView;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-11
 **/
public class MyViewMethodReturnValueHandler implements MyHandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return MyView.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
                                  MyModelAndViewContainer mavContainer, MyNativeWebRequest webRequest) throws Exception {

        if (returnValue instanceof MyView){
            MyView view = (MyView) returnValue;
            mavContainer.setView(view);
            if (view instanceof MySmartView && ((MySmartView) view).isRedirectView()) {
                mavContainer.setRedirectModelScenario(true);
            }
        }
        else if (returnValue != null) {
            // should not happen
            throw new UnsupportedOperationException("Unexpected return type: " +
                    returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
        }
    }
}
