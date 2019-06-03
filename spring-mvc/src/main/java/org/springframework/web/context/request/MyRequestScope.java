package org.springframework.web.context.request;

import org.springframework.beans.factory.ObjectFactory;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyRequestScope  extends MyAbstractRequestAttributesScope{
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        return null;
    }

    @Override
    public Object remove(String name) {
        return null;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {

    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }
}
