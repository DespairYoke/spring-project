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

import java.util.Collections;
import java.util.Map;
import javax.servlet.ServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.MyDataBinder;

import org.springframework.web.bind.MyServletRequestDataBinder;
import org.springframework.web.bind.MyWebDataBinder;
import org.springframework.web.bind.support.MyWebDataBinderFactory;
import org.springframework.web.context.request.MyNativeWebRequest;


import org.springframework.web.context.request.MyRequestAttributes;
import org.springframework.web.method.annotation.MyModelAttributeMethodProcessor;
import org.springframework.web.servlet.MyHandlerMapping;


public class MyServletModelAttributeMethodProcessor extends MyModelAttributeMethodProcessor {

    /**
     * Class constructor.
     * @param annotationNotRequired if "true", non-simple method arguments and
     * return values are considered model attributes with or without a
     * {@code @ModelAttribute} annotation
     */
    public MyServletModelAttributeMethodProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }


    /**
     * Instantiate the model attribute from a URI template variable or from a
     * request parameter if the name matches to the model attribute name and
     * if there is an appropriate type conversion strategy. If none of these
     * are true delegate back to the base class.
     * @see #createAttributeFromRequestValue
     */
    @Override
    protected final Object createAttribute(String attributeName, MethodParameter parameter,
                                           MyWebDataBinderFactory binderFactory, MyNativeWebRequest request) throws Exception {

        String value = getRequestValueForAttribute(attributeName, request);
        if (value != null) {
            Object attribute = createAttributeFromRequestValue(
                    value, attributeName, parameter, binderFactory, request);
            if (attribute != null) {
                return attribute;
            }
        }

        return super.createAttribute(attributeName, parameter, binderFactory, request);
    }

    /**
     * Obtain a value from the request that may be used to instantiate the
     * model attribute through type conversion from String to the target type.
     * <p>The default implementation looks for the attribute name to match
     * a URI variable first and then a request parameter.
     * @param attributeName the model attribute name
     * @param request the current request
     * @return the request value to try to convert, or {@code null} if none
     */
    @Nullable
    protected String getRequestValueForAttribute(String attributeName, MyNativeWebRequest request) {
        Map<String, String> variables = getUriTemplateVariables(request);
        String variableValue = variables.get(attributeName);
        if (StringUtils.hasText(variableValue)) {
            return variableValue;
        }
        String parameterValue = request.getParameter(attributeName);
        if (StringUtils.hasText(parameterValue)) {
            return parameterValue;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected final Map<String, String> getUriTemplateVariables(MyNativeWebRequest request) {
        Map<String, String> variables = (Map<String, String>) request.getAttribute(
                MyHandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, MyRequestAttributes.SCOPE_REQUEST);
        return (variables != null ? variables : Collections.emptyMap());
    }

    /**
     * Create a model attribute from a String request value (e.g. URI template
     * variable, request parameter) using type conversion.
     * <p>The default implementation converts only if there a registered
     * {@link Converter} that can perform the conversion.
     * @param sourceValue the source value to create the model attribute from
     * @param attributeName the name of the attribute (never {@code null})
     * @param parameter the method parameter
     * @param binderFactory for creating WebDataBinder instance
     * @param request the current request
     * @return the created model attribute, or {@code null} if no suitable
     * conversion found
     */
    @Nullable
    protected Object createAttributeFromRequestValue(String sourceValue, String attributeName,
                                                     MethodParameter parameter, MyWebDataBinderFactory binderFactory, MyNativeWebRequest request)
            throws Exception {

        MyDataBinder binder = binderFactory.createBinder(request, null, attributeName);
        ConversionService conversionService = binder.getConversionService();
        if (conversionService != null) {
            TypeDescriptor source = TypeDescriptor.valueOf(String.class);
            TypeDescriptor target = new TypeDescriptor(parameter);
            if (conversionService.canConvert(source, target)) {
                return binder.convertIfNecessary(sourceValue, parameter.getParameterType(), parameter);
            }
        }
        return null;
    }

    @Override
    protected void bindRequestParameters(MyWebDataBinder binder, MyNativeWebRequest request) {
        ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
        Assert.state(servletRequest != null, "No ServletRequest");
        MyServletRequestDataBinder servletBinder = (MyServletRequestDataBinder) binder;
        servletBinder.bind(servletRequest);
    }
}
