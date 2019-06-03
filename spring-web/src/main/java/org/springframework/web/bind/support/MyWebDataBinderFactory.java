package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.MyWebDataBinder;
import org.springframework.web.context.request.MyNativeWebRequest;


/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public interface MyWebDataBinderFactory {
    MyWebDataBinder createBinder(MyNativeWebRequest webRequest, @Nullable Object target, String objectName)
            throws Exception;
}
