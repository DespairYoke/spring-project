package org.springframework.web.context.request.async;

import org.springframework.web.context.request.MyNativeWebRequest;

import java.util.concurrent.Callable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public interface MyCallableProcessingInterceptor {

    default <T> void preProcess(MyNativeWebRequest request, Callable<T> task) throws Exception {
    }

    default <T> void postProcess(MyNativeWebRequest request, Callable<T> task,
                                 Object concurrentResult) throws Exception {
    }

}
