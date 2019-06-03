package org.springframework.web.bind.support;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
public class MySimpleSessionStatus implements MySessionStatus{

    private boolean complete = false;


    @Override
    public void setComplete() {
        this.complete = true;
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }
}
