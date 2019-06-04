package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public interface MyNativeWebRequest extends MyWebRequest{


    @Nullable
    <T> T getNativeRequest(@Nullable Class<T> requiredType);


    @Nullable
    <T> T getNativeResponse(@Nullable Class<T> requiredType);
}
