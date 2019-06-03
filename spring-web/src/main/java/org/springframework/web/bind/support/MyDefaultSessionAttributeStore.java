package org.springframework.web.bind.support;

import org.springframework.web.context.request.MyWebRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyDefaultSessionAttributeStore implements MySessionAttributeStore{
    @Override
    public void storeAttribute(MyWebRequest request, String attributeName, Object attributeValue) {

    }

    @Override
    public Object retrieveAttribute(MyWebRequest request, String attributeName) {
        return null;
    }

    @Override
    public void cleanupAttribute(MyWebRequest request, String attributeName) {

    }
}
