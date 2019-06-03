package org.springframework.web.bind.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MyRequestMapping
public @interface MyGetMapping {


    @AliasFor(annotation = MyRequestMapping.class)
    String[] value() default {} ;
}
