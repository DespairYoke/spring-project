package org.springframework.web.bind.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MyHttpStatus;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyResponseStatus {

    @AliasFor("code")
    MyHttpStatus value() default MyHttpStatus.INTERNAL_SERVER_ERROR;


    @AliasFor("value")
    MyHttpStatus code() default MyHttpStatus.INTERNAL_SERVER_ERROR;

    /**
     * The <em>reason</em> to be used for the response.
     * @see javax.servlet.http.HttpServletResponse#sendError(int, String)
     */
    String reason() default "";
}
