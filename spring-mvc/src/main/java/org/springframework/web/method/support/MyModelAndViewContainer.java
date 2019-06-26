package org.springframework.web.method.support;

import org.springframework.http.MyHttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.ui.MyModelMap;
import org.springframework.validation.support.MyBindingAwareModelMap;
import org.springframework.web.bind.support.MySessionStatus;
import org.springframework.web.bind.support.MySimpleSessionStatus;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private MyModelMap redirectModel;

    private boolean redirectModelScenario = false;

    private boolean requestHandled = false;

    private final Set<String> bindingDisabled = new HashSet<>(4);

    private MyHttpStatus status;

    private final MySessionStatus sessionStatus = new MySimpleSessionStatus();

    private final Set<String> noBinding = new HashSet<>(4);

    public MyModelMap getDefaultModel() {
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

    public MyModelMap getModel() {
        if (useDefaultModel()) {
            return this.defaultModel;
        }
        else {
            if (this.redirectModel == null) {
                this.redirectModel = new MyModelMap();
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

    public boolean isBindingDisabled(String name) {
        return (this.bindingDisabled.contains(name) || this.noBinding.contains(name));
    }

    public void setIgnoreDefaultModelOnRedirect(boolean ignoreDefaultModelOnRedirect) {
        this.ignoreDefaultModelOnRedirect = ignoreDefaultModelOnRedirect;
    }

    public void setBinding(String attributeName, boolean enabled) {
        if (!enabled) {
            this.noBinding.add(attributeName);
        }
        else {
            this.noBinding.remove(attributeName);
        }
    }
}
