package org.springframework.web.util;

import org.springframework.lang.Nullable;

public interface MyUriBuilder {

    MyUriBuilder scheme(@Nullable String scheme);

    MyUriBuilder userInfo(@Nullable String userInfo);

    MyUriBuilder host(@Nullable String host);

    MyUriBuilder path(String path);

    MyUriBuilder query(String query);

    MyUriBuilder port(int port);

    MyUriBuilder port(@Nullable String port);

    MyUriBuilder fragment(@Nullable String fragment);

    MyUriBuilder queryParam(String name, Object... values);

    MyUriBuilder pathSegment(String... pathSegments) throws IllegalArgumentException;

}
