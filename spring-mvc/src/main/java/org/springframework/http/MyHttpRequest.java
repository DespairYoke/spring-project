package org.springframework.http;

import org.springframework.lang.Nullable;

import java.net.URI;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
public interface MyHttpRequest extends MyHttpMessage{


    @Nullable
    default MyHttpMethod getMethod() {
        return MyHttpMethod.resolve(getMethodValue());
    }


    String getMethodValue();


    URI getURI();
}
