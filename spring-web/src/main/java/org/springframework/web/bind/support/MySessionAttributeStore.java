package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.MyWebRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public interface MySessionAttributeStore {


    void storeAttribute(MyWebRequest request, String attributeName, Object attributeValue);


    @Nullable
    Object retrieveAttribute(MyWebRequest request, String attributeName);


    void cleanupAttribute(MyWebRequest request, String attributeName);
}
