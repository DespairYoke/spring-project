package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public interface MyWebRequest  extends MyRequestAttributes {

    String getParameter(String paramName);

    String[] getParameterValues(String paramName);

    Map<String, String[]> getParameterMap();

    @Nullable
    String getHeader(String headerName);
}
