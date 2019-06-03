package org.springframework.web.method.annotation;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.MySessionAttributes;
import org.springframework.web.bind.support.MySessionAttributeStore;
import org.springframework.web.context.request.MyWebRequest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MySessionAttributesHandler {

    private final Set<String> attributeNames = new HashSet<>();

    private final Set<Class<?>> attributeTypes = new HashSet<>();

    private final Set<String> knownAttributeNames = Collections.newSetFromMap(new ConcurrentHashMap<>(4));

    private final MySessionAttributeStore sessionAttributeStore;

    public MySessionAttributesHandler(Class<?> handlerType, MySessionAttributeStore sessionAttributeStore) {
        Assert.notNull(sessionAttributeStore, "SessionAttributeStore may not be null");
        this.sessionAttributeStore = sessionAttributeStore;

        MySessionAttributes ann = AnnotatedElementUtils.findMergedAnnotation(handlerType, MySessionAttributes.class);
        if (ann != null) {
            Collections.addAll(this.attributeNames, ann.names());
            Collections.addAll(this.attributeTypes, ann.types());
        }
        this.knownAttributeNames.addAll(this.attributeNames);
    }
    public void cleanupAttributes(MyWebRequest request) {
        for (String attributeName : this.knownAttributeNames) {
            this.sessionAttributeStore.cleanupAttribute(request, attributeName);
        }
    }

    public boolean isHandlerSessionAttribute(String attributeName, Class<?> attributeType) {
        Assert.notNull(attributeName, "Attribute name must not be null");
        if (this.attributeNames.contains(attributeName) || this.attributeTypes.contains(attributeType)) {
            this.knownAttributeNames.add(attributeName);
            return true;
        }
        else {
            return false;
        }
    }

    public void storeAttributes(MyWebRequest request, Map<String, ?> attributes) {
        attributes.forEach((name, value) -> {
            if (value != null && isHandlerSessionAttribute(name, value.getClass())) {
                this.sessionAttributeStore.storeAttribute(request, name, value);
            }
        });
    }

}
