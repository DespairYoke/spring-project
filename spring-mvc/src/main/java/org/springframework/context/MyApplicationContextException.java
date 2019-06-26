package org.springframework.context;

import org.springframework.beans.FatalBeanException;

public class MyApplicationContextException extends FatalBeanException {


    public MyApplicationContextException(String msg) {
        super(msg);
    }

    /**
     * Create a new {@code ApplicationContextException}
     * with the specified detail message and the given root cause.
     * @param msg the detail message
     * @param cause the root cause
     */
    public MyApplicationContextException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
