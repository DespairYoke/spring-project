package org.springframework.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MyApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class MyApplicationObjectSupport {

    private MyApplicationContext applicationContext;

    protected final Log logger = LogFactory.getLog(getClass());
    @Nullable
    public final MyApplicationContext getApplicationContext() throws IllegalStateException {
        if (this.applicationContext == null && isContextRequired()) {
            throw new IllegalStateException(
                    "ApplicationObjectSupport instance [" + this + "] does not run in an ApplicationContext");
        }
        return this.applicationContext;
    }
    protected boolean isContextRequired() {
        return false;
    }

    protected final MyApplicationContext obtainApplicationContext() {
        MyApplicationContext applicationContext = getApplicationContext();
        Assert.state(applicationContext != null, "No ApplicationContext");
        return applicationContext;
    }
}
