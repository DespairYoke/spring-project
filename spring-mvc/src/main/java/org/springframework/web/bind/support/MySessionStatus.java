package org.springframework.web.bind.support;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
public interface MySessionStatus {

    void setComplete();

    /**
     * Return whether the current handler's session processing has been marked
     * as complete.
     */
    boolean isComplete();
}
