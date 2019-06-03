package org.springframework.web.bind.annotation;

import java.lang.annotation.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyResponseBody {

}
