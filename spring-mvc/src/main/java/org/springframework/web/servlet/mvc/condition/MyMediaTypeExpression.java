package org.springframework.web.servlet.mvc.condition;


import org.springframework.http.MyMediaType;

public interface MyMediaTypeExpression {

    MyMediaType getMediaType();

    boolean isNegated();
}
