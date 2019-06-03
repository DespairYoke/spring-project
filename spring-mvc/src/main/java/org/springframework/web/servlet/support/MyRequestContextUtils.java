package org.springframework.web.servlet.support;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.MyContextLoader;
import org.springframework.web.context.MyWebApplicationContext;
import org.springframework.web.context.support.MyWebApplicationContextUtils;

import org.springframework.web.servlet.*;
import org.springframework.web.util.MyUriComponents;
import org.springframework.web.util.MyUriComponentsBuilder;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-09
 **/
public abstract class MyRequestContextUtils {

    public static final String REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME = "requestDataValueProcessor";

    @Nullable
    public static Map<String, ?> getInputFlashMap(HttpServletRequest request) {
        return (Map<String, ?>) request.getAttribute(MyDispatcherServlet.INPUT_FLASH_MAP_ATTRIBUTE);
    }

    public static MyFlashMap getOutputFlashMap(HttpServletRequest request) {
        return (MyFlashMap) request.getAttribute(MyDispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE);
    }
    public static Locale getLocale(HttpServletRequest request) {
        MyLocaleResolver localeResolver = getLocaleResolver(request);
        return (localeResolver != null ? localeResolver.resolveLocale(request) : request.getLocale());
    }

    @Nullable
    public static TimeZone getTimeZone(HttpServletRequest request) {
        MyLocaleResolver localeResolver = getLocaleResolver(request);
        if (localeResolver instanceof MyLocaleContextResolver) {
            LocaleContext localeContext = ((MyLocaleContextResolver) localeResolver).resolveLocaleContext(request);
            if (localeContext instanceof TimeZoneAwareLocaleContext) {
                return ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
            }
        }
        return null;
    }

    @Nullable
    public static MyWebApplicationContext findWebApplicationContext(
            HttpServletRequest request, @Nullable ServletContext servletContext) {

        MyWebApplicationContext webApplicationContext = (MyWebApplicationContext) request.getAttribute(
                MyDispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (webApplicationContext == null) {
            if (servletContext != null) {
                webApplicationContext = MyWebApplicationContextUtils.getWebApplicationContext(servletContext);
            }
            if (webApplicationContext == null) {
                webApplicationContext = MyContextLoader.getCurrentWebApplicationContext();
            }
        }
        return webApplicationContext;
    }

    @Nullable
    public static MyFlashMapManager getFlashMapManager(HttpServletRequest request) {
        return (MyFlashMapManager) request.getAttribute(MyDispatcherServlet.FLASH_MAP_MANAGER_ATTRIBUTE);
    }

    @Nullable
    public static MyLocaleResolver getLocaleResolver(HttpServletRequest request) {
        return (MyLocaleResolver) request.getAttribute(MyDispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
    }

    public static void saveOutputFlashMap(String location, HttpServletRequest request, HttpServletResponse response) {
        MyFlashMap flashMap = getOutputFlashMap(request);
        if (CollectionUtils.isEmpty(flashMap)) {
            return;
        }

        MyUriComponents uriComponents = MyUriComponentsBuilder.fromUriString(location).build();
        flashMap.setTargetRequestPath(uriComponents.getPath());
        flashMap.addTargetRequestParams(uriComponents.getQueryParams());

        MyFlashMapManager manager = getFlashMapManager(request);
        Assert.state(manager != null, "No FlashMapManager. Is this a DispatcherServlet handled request?");
        manager.saveOutputFlashMap(flashMap, request, response);
    }
}
