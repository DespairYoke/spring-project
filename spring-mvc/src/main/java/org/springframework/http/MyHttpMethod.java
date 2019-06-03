package org.springframework.http;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public enum  MyHttpMethod {

    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    private static final Map<String, MyHttpMethod> mappings = new HashMap<>(8);

    @Nullable
    public static MyHttpMethod resolve(@Nullable String method) {
        return (method != null ? mappings.get(method) : null);
    }

    public boolean matches(String method) {
        return (this == resolve(method));
    }

}
