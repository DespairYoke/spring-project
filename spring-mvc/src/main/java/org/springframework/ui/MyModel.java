package org.springframework.ui;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Map;

public interface MyModel {

    /**
     * Add the supplied attribute under the supplied name.
     * @param attributeName the name of the model attribute (never {@code null})
     * @param attributeValue the model attribute value (can be {@code null})
     */
    MyModel addAttribute(String attributeName, @Nullable Object attributeValue);

    /**
     * Add the supplied attribute to this {@code Map} using a
     * {@link org.springframework.core.Conventions#getVariableName generated name}.
     * <p><emphasis>Note: Empty {@link java.util.Collection Collections} are not added to
     * the model when using this method because we cannot correctly determine
     * the true convention name. View code should check for {@code null} rather
     * than for empty collections as is already done by JSTL tags.</emphasis>
     * @param attributeValue the model attribute value (never {@code null})
     */
    MyModel addAttribute(Object attributeValue);

    /**
     * Copy all attributes in the supplied {@code Collection} into this
     * {@code Map}, using attribute name generation for each element.
     * @see #addAttribute(Object)
     */
    MyModel addAllAttributes(Collection<?> attributeValues);

    /**
     * Copy all attributes in the supplied {@code Map} into this {@code Map}.
     * @see #addAttribute(String, Object)
     */
    MyModel addAllAttributes(Map<String, ?> attributes);

    /**
     * Copy all attributes in the supplied {@code Map} into this {@code Map},
     * with existing objects of the same name taking precedence (i.e. not getting
     * replaced).
     */
    MyModel mergeAttributes(Map<String, ?> attributes);

    /**
     * Does this model contain an attribute of the given name?
     * @param attributeName the name of the model attribute (never {@code null})
     * @return whether this model contains a corresponding attribute
     */
    boolean containsAttribute(String attributeName);

    /**
     * Return the current set of model attributes as a Map.
     */
    Map<String, Object> asMap();
}
