package org.springframework.web.servlet.handler;

import org.springframework.util.Assert;

import org.springframework.web.context.request.MyAsyncWebRequestInterceptor;
import org.springframework.web.context.request.MyWebRequestInterceptor;
import org.springframework.web.servlet.MyAsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-28
 **/
public class MyWebRequestHandlerInterceptorAdapter implements MyAsyncHandlerInterceptor {

    private final MyWebRequestInterceptor requestInterceptor;

    public MyWebRequestHandlerInterceptorAdapter(MyWebRequestInterceptor requestInterceptor) {
        Assert.notNull(requestInterceptor, "WebRequestInterceptor must not be null");
        this.requestInterceptor = requestInterceptor;
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (this.requestInterceptor instanceof MyAsyncWebRequestInterceptor) {
            MyAsyncWebRequestInterceptor asyncInterceptor = (MyAsyncWebRequestInterceptor) this.requestInterceptor;
            MyDispatcherServletWebRequest webRequest = new MyDispatcherServletWebRequest(request, response);
            asyncInterceptor.afterConcurrentHandlingStarted(webRequest);
        }
    }
}
