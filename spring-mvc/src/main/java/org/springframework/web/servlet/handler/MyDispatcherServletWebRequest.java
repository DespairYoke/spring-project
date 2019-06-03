package org.springframework.web.servlet.handler;

import org.springframework.web.context.request.MyServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-28
 **/
public class MyDispatcherServletWebRequest extends MyServletWebRequest {

    public MyDispatcherServletWebRequest(HttpServletRequest request) {
        super(request);
    }
    public MyDispatcherServletWebRequest(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }
}
