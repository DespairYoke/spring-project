package org.springframework.web.context.request.async;

import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public final class MyWebAsyncManager {

    private MyAsyncWebRequest asyncWebRequest;

    private final Map<Object, MyCallableProcessingInterceptor> callableInterceptors = new LinkedHashMap<>();

    public void registerCallableInterceptor(Object key, MyCallableProcessingInterceptor interceptor) {
        Assert.notNull(key, "Key is required");
        Assert.notNull(interceptor, "CallableProcessingInterceptor  is required");
        this.callableInterceptors.put(key, interceptor);
    }

    public boolean isConcurrentHandlingStarted() {
        return (this.asyncWebRequest != null && this.asyncWebRequest.isAsyncStarted());
    }

}
