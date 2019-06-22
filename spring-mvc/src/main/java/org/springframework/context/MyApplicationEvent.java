package org.springframework.context;

import java.util.EventObject;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public abstract class MyApplicationEvent extends EventObject {

    private static final long serialVersionUID = 7099057708183571937L;

    /** System time when the event happened */
    private final long timestamp;


    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public MyApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }


    /**
     * Return the system time in milliseconds when the event happened.
     */
    public final long getTimestamp() {
        return this.timestamp;
    }
}
