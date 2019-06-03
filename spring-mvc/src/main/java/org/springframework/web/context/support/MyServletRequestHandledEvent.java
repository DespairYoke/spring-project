package org.springframework.web.context.support;

import org.springframework.lang.Nullable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-25
 **/
public class MyServletRequestHandledEvent extends MyRequestHandledEvent{

    private final String requestUrl;

    /** IP address that the request came from */
    private final String clientAddress;

    /** Usually GET or POST */
    private final String method;

    /** Name of the servlet that handled the request */
    private final String servletName;

    /** HTTP status code of the response */
    private final int statusCode;




    public MyServletRequestHandledEvent(Object source, String requestUrl,
                                      String clientAddress, String method, String servletName, @Nullable String sessionId,
                                      @Nullable String userName, long processingTimeMillis, @Nullable Throwable failureCause, int statusCode) {

        super(source, sessionId, userName, processingTimeMillis, failureCause);
        this.requestUrl = requestUrl;
        this.clientAddress = clientAddress;
        this.method = method;
        this.servletName = servletName;
        this.statusCode = statusCode;
    }

}
