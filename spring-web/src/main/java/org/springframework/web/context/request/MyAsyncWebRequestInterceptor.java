package org.springframework.web.context.request;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
public interface MyAsyncWebRequestInterceptor extends MyWebRequestInterceptor{

    void afterConcurrentHandlingStarted(MyWebRequest request);
}
