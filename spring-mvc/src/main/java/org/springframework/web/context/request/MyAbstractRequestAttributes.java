package org.springframework.web.context.request;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public abstract class MyAbstractRequestAttributes implements MyRequestAttributes {

    protected final Map<String, Runnable> requestDestructionCallbacks = new LinkedHashMap<>(8);

    private volatile boolean requestActive = true;

    public void requestCompleted() {
        executeRequestDestructionCallbacks();
        updateAccessedSessionAttributes();
        this.requestActive = false;
    }
    private void executeRequestDestructionCallbacks() {
        synchronized (this.requestDestructionCallbacks) {
            for (Runnable runnable : this.requestDestructionCallbacks.values()) {
                runnable.run();
            }
            this.requestDestructionCallbacks.clear();
        }
    }

    /**
     * Update all session attributes that have been accessed during request processing,
     * to expose their potentially updated state to the underlying session manager.
     */
    protected abstract void updateAccessedSessionAttributes();

}
