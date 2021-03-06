package org.springframework.web.method.support;

import org.springframework.http.MyHttpStatus;
import org.springframework.lang.Nullable;

import org.springframework.web.bind.support.MySessionStatus;
import org.springframework.web.bind.support.MySimpleSessionStatus;

import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyModelAndViewContainer {

    private boolean ignoreDefaultModelOnRedirect = false;

    private final MyModelMap defaultModel = new MyBindingAwareModelMap();

    private Object view;
    @Nullable
    private ModelMap redirectModel;

    private boolean redirectModelScenario = false;

    private boolean requestHandled = false;

    private MyHttpStatus status;

    private final MySessionStatus sessionStatus = new MySimpleSessionStatus();

    public ModelMap getDefaultModel() {
        return this.defaultModel;
    }

    public boolean isRequestHandled() {
        return this.requestHandled;
    }

    public void setView(@Nullable Object view) {
        this.view = view;
    }

    public void setRequestHandled(boolean requestHandled) {
        this.requestHandled = requestHandled;
    }

    public boolean containsAttribute(String name) {
        return getModel().containsAttribute(name);
    }

    public MySessionStatus getSessionStatus() {
        return this.sessionStatus;
    }

    public Object getView() {
        return this.view;
    }
    public void setViewName(@Nullable String viewName) {
        this.view = viewName;
    }
    public void setStatus(@Nullable MyHttpStatus status) {
        this.status = status;
    }
    public void setRedirectModelScenario(boolean redirectModelScenario) {
        this.redirectModelScenario = redirectModelScenario;
    }
    public String getViewName() {
        return (this.view instanceof String ? (String) this.view : null);
    }

    public MyHttpStatus getStatus() {
        return this.status;
    }

    public boolean isViewReference() {
        return (this.view instanceof String);
    }

    public ModelMap getModel() {
        if (useDefaultModel()) {
            return this.defaultModel;
        }
        else {
            if (this.redirectModel == null) {
                this.redirectModel = new ModelMap();
            }
            return this.redirectModel;
        }
    }

    private boolean useDefaultModel() {
        return (!this.redirectModelScenario || (this.redirectModel == null && !this.ignoreDefaultModelOnRedirect));
    }
    public MyModelAndViewContainer addAllAttributes(@Nullable Map<String, ?> attributes) {
        getModel().addAllAttributes(attributes);
        return this;
    }

    public void setIgnoreDefaultModelOnRedirect(boolean ignoreDefaultModelOnRedirect) {
        this.ignoreDefaultModelOnRedirect = ignoreDefaultModelOnRedirect;
    }

}
