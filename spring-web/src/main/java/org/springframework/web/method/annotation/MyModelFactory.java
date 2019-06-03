package org.springframework.web.method.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MyWebDataBinder;
import org.springframework.web.bind.annotation.MyModelAttribute;
import org.springframework.web.bind.support.MyWebDataBinderFactory;
import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.method.support.MyInvocableHandlerMethod;
import org.springframework.web.method.support.MyModelAndViewContainer;

import java.util.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public final class MyModelFactory {


    private static final Log logger = LogFactory.getLog(MyModelFactory.class);

    private final List<ModelMethod> modelMethods = new ArrayList<>();

    private final MyWebDataBinderFactory dataBinderFactory;

    private final MySessionAttributesHandler sessionAttributesHandler;


    public MyModelFactory(@Nullable List<MyInvocableHandlerMethod> handlerMethods,
                        MyWebDataBinderFactory binderFactory, MySessionAttributesHandler attributeHandler) {

        if (handlerMethods != null) {
            for (MyInvocableHandlerMethod handlerMethod : handlerMethods) {
                this.modelMethods.add(new ModelMethod(handlerMethod));
            }
        }
        this.dataBinderFactory = binderFactory;
        this.sessionAttributesHandler = attributeHandler;
    }


    public void updateModel(MyNativeWebRequest request, MyModelAndViewContainer container) throws Exception {
        ModelMap defaultModel = container.getDefaultModel();
        if (container.getSessionStatus().isComplete()){
            this.sessionAttributesHandler.cleanupAttributes(request);
        }
        else {
            this.sessionAttributesHandler.storeAttributes(request, defaultModel);
        }
        if (!container.isRequestHandled() && container.getModel() == defaultModel) {
            updateBindingResult(request, defaultModel);
        }
    }

    private void updateBindingResult(MyNativeWebRequest request, ModelMap model) throws Exception {
        List<String> keyNames = new ArrayList<>(model.keySet());
        for (String name : keyNames) {
            Object value = model.get(name);
            if (value != null && isBindingCandidate(name, value)) {
                String bindingResultKey = BindingResult.MODEL_KEY_PREFIX + name;
                if (!model.containsAttribute(bindingResultKey)) {
                    MyWebDataBinder dataBinder = this.dataBinderFactory.createBinder(request, value, name);
                    model.put(bindingResultKey, dataBinder.getBindingResult());
                }
            }
        }
    }

    private boolean isBindingCandidate(String attributeName, Object value) {
        if (attributeName.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
            return false;
        }

        if (this.sessionAttributesHandler.isHandlerSessionAttribute(attributeName, value.getClass())) {
            return true;
        }

        return (!value.getClass().isArray() && !(value instanceof Collection) &&
                !(value instanceof Map) && !BeanUtils.isSimpleValueType(value.getClass()));
    }
    private static class ModelMethod {

        private final MyInvocableHandlerMethod handlerMethod;

        private final Set<String> dependencies = new HashSet<>();

        public ModelMethod(MyInvocableHandlerMethod handlerMethod) {
            this.handlerMethod = handlerMethod;
            for (MethodParameter parameter : handlerMethod.getMethodParameters()) {
                if (parameter.hasParameterAnnotation(MyModelAttribute.class)) {
                    this.dependencies.add(getNameForParameter(parameter));
                }
            }
        }

        public static String getNameForParameter(MethodParameter parameter) {
            MyModelAttribute ann = parameter.getParameterAnnotation(MyModelAttribute.class);
            String name = (ann != null ? ann.value() : null);
            return (StringUtils.hasText(name) ? name : Conventions.getVariableNameForParameter(parameter));
        }

        public MyInvocableHandlerMethod getHandlerMethod() {
            return this.handlerMethod;
        }

        public boolean checkDependencies(MyModelAndViewContainer mavContainer) {
            for (String name : this.dependencies) {
                if (!mavContainer.containsAttribute(name)) {
                    return false;
                }
            }
            return true;
        }

        public List<String> getUnresolvedDependencies(MyModelAndViewContainer mavContainer) {
            List<String> result = new ArrayList<>(this.dependencies.size());
            for (String name : this.dependencies) {
                if (!mavContainer.containsAttribute(name)) {
                    result.add(name);
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return this.handlerMethod.getMethod().toGenericString();
        }
    }

}
