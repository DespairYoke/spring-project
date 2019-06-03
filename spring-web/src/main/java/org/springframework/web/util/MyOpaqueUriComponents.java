package org.springframework.web.util;

import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
public class MyOpaqueUriComponents  extends MyUriComponents{

    private final String ssp;
    MyOpaqueUriComponents(@Nullable String scheme, @Nullable String schemeSpecificPart, @Nullable String fragment) {
        super(scheme, fragment);
        this.ssp = schemeSpecificPart;
    }

    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getHost() {
        return null;
    }
}
