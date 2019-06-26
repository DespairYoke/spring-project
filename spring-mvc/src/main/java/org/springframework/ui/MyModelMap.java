package org.springframework.ui;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

public class MyModelMap extends LinkedHashMap<String, Object> {

    public boolean containsAttribute(String attributeName) {
        return containsKey(attributeName);
    }


    public MyModelMap addAttribute(String attributeName, @Nullable Object attributeValue) {
        Assert.notNull(attributeName, "Model attribute name must not be null");
        put(attributeName, attributeValue);
        return this;
    }

    public MyModelMap addAllAttributes(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            putAll(attributes);
        }
        return this;
    }
}
