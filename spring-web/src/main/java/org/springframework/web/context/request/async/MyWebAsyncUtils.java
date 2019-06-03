package org.springframework.web.context.request.async;

import javax.servlet.ServletRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-24
 **/
public abstract class MyWebAsyncUtils {
    public static final String WEB_ASYNC_MANAGER_ATTRIBUTE =
            MyWebAsyncManager.class.getName() + ".WEB_ASYNC_MANAGER";

    public static MyWebAsyncManager getAsyncManager(ServletRequest servletRequest) {
        MyWebAsyncManager asyncManager = null;
        Object asyncManagerAttr = servletRequest.getAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE);
        if (asyncManagerAttr instanceof MyWebAsyncManager) {
            asyncManager = (MyWebAsyncManager) asyncManagerAttr;
        }
        if (asyncManager == null) {
            asyncManager = new MyWebAsyncManager();
            servletRequest.setAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE, asyncManager);
        }
        return asyncManager;
    }
}
