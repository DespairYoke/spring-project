package org.springframework.web.context.request.async;

import org.springframework.web.context.request.MyNativeWebRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-25
 **/
public interface MyAsyncWebRequest extends MyNativeWebRequest {

    boolean isAsyncStarted();
}
