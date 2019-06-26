package org.springframework.context;

import org.springframework.lang.Nullable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
public interface MyHierarchicalMessageSource  extends MyMessageSource {

    void setParentMessageSource(@Nullable MyMessageSource parent);

    /**
     * Return the parent of this MessageSource, or {@code null} if none.
     */
    @Nullable
    MyMessageSource getParentMessageSource();

}
