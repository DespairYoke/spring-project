package org.springframework.web.context.support;

import org.springframework.context.MyApplicationEvent;
import org.springframework.lang.Nullable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-25
 **/
public class MyRequestHandledEvent extends MyApplicationEvent {


    @Nullable
    private String sessionId;

    @Nullable
    private String userName;


    private final long processingTimeMillis;


    @Nullable
    private Throwable failureCause;


    public MyRequestHandledEvent(Object source, @Nullable String sessionId, @Nullable String userName,
                               long processingTimeMillis) {

        super(source);
        this.sessionId = sessionId;
        this.userName = userName;
        this.processingTimeMillis = processingTimeMillis;
    }

    public MyRequestHandledEvent(Object source, @Nullable String sessionId, @Nullable String userName,
                               long processingTimeMillis, @Nullable Throwable failureCause) {

        this(source, sessionId, userName, processingTimeMillis);
        this.failureCause = failureCause;
    }

}
