package org.springframework.web.context.request;

import org.springframework.lang.Nullable;
import org.springframework.ui.MyModelMap;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
public interface MyWebRequestInterceptor {

    void postHandle(MyWebRequest request, @Nullable MyModelMap model) throws Exception;


    void afterCompletion(MyWebRequest request, @Nullable Exception ex) throws Exception;
}
