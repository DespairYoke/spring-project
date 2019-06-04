package org.springframework.web.bind.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {


    @AliasFor("name")
    String value() default "";


    @AliasFor("value")
    String name() default "";


    boolean required() default true;


    String defaultValue() default MyValueConstants.DEFAULT_NONE;
}
