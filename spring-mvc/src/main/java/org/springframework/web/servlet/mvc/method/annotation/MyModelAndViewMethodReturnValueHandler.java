/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.method.support.MyHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.MyModelAndViewContainer;
import org.springframework.web.servlet.MyModelAndView;
import org.springframework.web.servlet.MySmartView;
import org.springframework.web.servlet.MyView;

public class MyModelAndViewMethodReturnValueHandler implements MyHandlerMethodReturnValueHandler {

    @Nullable
    private String[] redirectPatterns;


    public void setRedirectPatterns(@Nullable String... redirectPatterns) {
        this.redirectPatterns = redirectPatterns;
    }

    /**
     * Return the configured redirect patterns, if any.
     * @since 4.1
     */
    @Nullable
    public String[] getRedirectPatterns() {
        return this.redirectPatterns;
    }


    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return MyModelAndView.class.isAssignableFrom(returnType.getParameterType());
    }



    @Override
    public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
                                  MyModelAndViewContainer mavContainer, MyNativeWebRequest webRequest) throws Exception {

        if (returnValue == null) {
            mavContainer.setRequestHandled(true);
            return;
        }

        MyModelAndView mav = (MyModelAndView) returnValue;
        if (mav.isReference()) {
            String viewName = mav.getViewName();
            mavContainer.setViewName(viewName);
            if (viewName != null && isRedirectViewName(viewName)) {
                mavContainer.setRedirectModelScenario(true);
            }
        }
        else {
            MyView view = mav.getView();
            mavContainer.setView(view);
            if (view instanceof MySmartView && ((MySmartView) view).isRedirectView()) {
                mavContainer.setRedirectModelScenario(true);
            }
        }
        mavContainer.setStatus(mav.getStatus());
        mavContainer.addAllAttributes(mav.getModel());
    }

    /**
     * Whether the given view name is a redirect view reference.
     * The default implementation checks the configured redirect patterns and
     * also if the view name starts with the "redirect:" prefix.
     * @param viewName the view name to check, never {@code null}
     * @return "true" if the given view name is recognized as a redirect view
     * reference; "false" otherwise.
     */
    protected boolean isRedirectViewName(String viewName) {
        return (PatternMatchUtils.simpleMatch(this.redirectPatterns, viewName) || viewName.startsWith("redirect:"));
    }

}
