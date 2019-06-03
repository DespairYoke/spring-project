package org.springframework.web.bind.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyModelAttribute {


    @AliasFor("name")
    String value() default "";


    @AliasFor("value")
    String name() default "";

    boolean binding() default true;

}
