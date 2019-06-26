package org.springframework.validation;

import org.springframework.util.Assert;

public class MyBindException extends Exception{


    private final MyBindingResult bindingResult;

    public MyBindException(MyBindingResult bindingResult) {
        Assert.notNull(bindingResult, "BindingResult must not be null");
        this.bindingResult = bindingResult;
    }

    public final MyBindingResult getBindingResult() {
        return this.bindingResult;
    }
}
