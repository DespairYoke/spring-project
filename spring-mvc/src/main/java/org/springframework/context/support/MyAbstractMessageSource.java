package org.springframework.context.support;

import org.springframework.context.MyHierarchicalMessageSource;
import org.springframework.context.MyMessageSource;
import org.springframework.lang.Nullable;

public abstract class MyAbstractMessageSource implements MyHierarchicalMessageSource {

    private MyMessageSource parentMessageSource;

    @Override
    public void setParentMessageSource(@Nullable MyMessageSource parent) {
        this.parentMessageSource = parent;
    }
}
