package org.springframework.web.bind.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyInitBinder {

    String[] value() default {};
}
