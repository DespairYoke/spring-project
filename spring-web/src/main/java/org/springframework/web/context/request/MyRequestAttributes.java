package org.springframework.web.context.request;

public interface MyRequestAttributes {

    int SCOPE_REQUEST = 0;

    int SCOPE_SESSION = 1;

    String REFERENCE_REQUEST = "request";

    Object getAttribute(String name, int scope);
}
