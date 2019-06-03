package org.springframework.web.util;

import org.springframework.lang.Nullable;

import javax.servlet.ServletException;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-25
 **/
public class MyNestedServletException  extends ServletException {

    public MyNestedServletException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
