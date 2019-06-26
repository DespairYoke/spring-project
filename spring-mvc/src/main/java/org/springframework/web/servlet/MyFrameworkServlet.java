package org.springframework.web.servlet;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.*;
import org.springframework.context.event.MyContextRefreshedEvent;
import org.springframework.context.event.MySourceFilteringListener;
import org.springframework.context.i18n.MyLocaleContext;
import org.springframework.context.i18n.MyLocaleContextHolder;
import org.springframework.context.i18n.MySimpleLocaleContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;

import org.springframework.http.MyHttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import org.springframework.web.context.MyConfigurableWebApplicationContext;
import org.springframework.web.context.MyConfigurableWebEnvironment;
//import org.springframework.web.context.MyContextLoader;
import org.springframework.web.context.MyWebApplicationContext;
import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.context.request.MyRequestAttributes;
import org.springframework.web.context.request.MyRequestContextHolder;
import org.springframework.web.context.request.MyServletRequestAttributes;
import org.springframework.web.context.request.async.MyCallableProcessingInterceptor;
import org.springframework.web.context.request.async.MyWebAsyncManager;
import org.springframework.web.context.request.async.MyWebAsyncUtils;

import org.springframework.web.context.support.MyServletRequestHandledEvent;
//import org.springframework.web.context.support.MyWebApplicationContextUtils;
import org.springframework.web.context.support.MyXmlWebApplicationContext;

import org.springframework.web.util.MyNestedServletException;
import org.springframework.web.util.MyWebUtils;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-18
 **/
public abstract class MyFrameworkServlet extends MyHttpServletBean{

    @Nullable
    private MyWebApplicationContext webApplicationContext;

    private Log logger = LogFactory.getLog(getClass());

    public static final Class<?> DEFAULT_CONTEXT_CLASS = MyXmlWebApplicationContext.class;

    private Class<?> contextClass = DEFAULT_CONTEXT_CLASS;

    /** Flag used to detect whether onRefresh has already been called */
    private boolean refreshEventReceived = false;

    @Nullable
    private String contextConfigLocation;

    @Nullable
    private String contextId;

    @Nullable
    private String namespace;

    @Nullable
    private String contextInitializerClasses;

    private boolean publishEvents = true;

    public static final String DEFAULT_NAMESPACE_SUFFIX = "-servlet";

    private static final String INIT_PARAM_DELIMITERS = ",; \t\n";

    private boolean threadContextInheritable = false;


    private final List<MyApplicationContextInitializer<MyConfigurableApplicationContext>> contextInitializers =
            new ArrayList<>();

    @Override
    protected final void initServletBean() throws ServletException {
        getServletContext().log("Initializing Spring FrameworkServlet '" + getServletName() + "'");
        if (this.logger.isInfoEnabled()) {
            this.logger.info("FrameworkServlet '" + getServletName() + "': initialization started");
        }
        long startTime = System.currentTimeMillis();

        try {
            this.webApplicationContext = initWebApplicationContext();
            initFrameworkServlet();
        }
        catch (ServletException | RuntimeException ex) {
            this.logger.error("Context initialization failed", ex);
            throw ex;
        }

        if (this.logger.isInfoEnabled()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            this.logger.info("FrameworkServlet '" + getServletName() + "': initialization completed in " +
                    elapsedTime + " ms");
        }
    }


    protected MyWebApplicationContext initWebApplicationContext() {
        MyWebApplicationContext rootContext = null; //null
        MyWebApplicationContext wac = null;


        if (wac == null) {
            // No context instance is defined for this servlet -> create a local one
            wac = createWebApplicationContext(rootContext);
        }


        return wac;
    }


    protected MyWebApplicationContext createWebApplicationContext(@Nullable MyWebApplicationContext parent) {
        return createWebApplicationContext((MyApplicationContext) parent);
    }

    protected MyWebApplicationContext createWebApplicationContext(@Nullable MyApplicationContext parent) {
        /**
         * 获取加载方式，默认为{@link MyXmlWebApplicationContext}
         */
        Class<?> contextClass = getContextClass();

        MyConfigurableWebApplicationContext wac =
                (MyConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);

        /**
         * 获取环境 {@link StandardEnvironment}
         */
        wac.setEnvironment(getEnvironment());  //StandardServletEnvironment
        wac.setParent(parent);            //null
        String configLocation = getContextConfigLocation();  //null
        if (configLocation != null) {
            wac.setConfigLocation(configLocation);
        }
        configureAndRefreshWebApplicationContext(wac);

        return wac;
    }

    public Class<?> getContextClass() {
        return this.contextClass;
    }

    @Nullable
    public String getContextConfigLocation() {
        return this.contextConfigLocation;
    }

    protected void configureAndRefreshWebApplicationContext(MyConfigurableWebApplicationContext wac) {
        if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
            // The application context id is still set to its original default value
            // -> assign a more useful id based on available information
            if (this.contextId != null) {
                wac.setId(this.contextId);
            }
            else {
                // Generate default id...
                wac.setId(MyConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
                        ObjectUtils.getDisplayString(getServletContext().getContextPath()) + '/' + getServletName());
            }
        }

        wac.setServletContext(getServletContext());
        wac.setServletConfig(getServletConfig());
        wac.setNamespace(getNamespace());
        wac.addApplicationListener(new MySourceFilteringListener(wac, new ContextRefreshListener()));

        // The wac environment's #initPropertySources will be called in any case when the context
        // is refreshed; do it eagerly here to ensure servlet property sources are in place for
        // use in any post-processing or initialization that occurs below prior to #refresh
        ConfigurableEnvironment env = wac.getEnvironment();
        if (env instanceof MyConfigurableWebEnvironment) {
            ((MyConfigurableWebEnvironment) env).initPropertySources(getServletContext(), getServletConfig());
        }

        postProcessWebApplicationContext(wac);
        applyInitializers(wac);
        wac.refresh();
    }


    public String getNamespace() {
        return (this.namespace != null ? this.namespace : getServletName() + DEFAULT_NAMESPACE_SUFFIX);
    }


    private class ContextRefreshListener implements MyApplicationListener<MyContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(MyContextRefreshedEvent event) {
            MyFrameworkServlet.this.onApplicationEvent(event);
        }
    }


    public void onApplicationEvent(MyContextRefreshedEvent event) {
        this.refreshEventReceived = true;
        onRefresh(event.getApplicationContext());
    }

    protected void onRefresh(MyApplicationContext context) {
        // For subclasses: do nothing by default.
    }

    protected void initFrameworkServlet() throws ServletException {
    }

    protected void postProcessWebApplicationContext(MyConfigurableWebApplicationContext wac) {
    }


    protected void applyInitializers(MyConfigurableApplicationContext wac) {
        String globalClassNames = null;
        if (globalClassNames != null) {
            for (String className : StringUtils.tokenizeToStringArray(globalClassNames, INIT_PARAM_DELIMITERS)) {
                this.contextInitializers.add(loadInitializer(className, wac));
            }
        }

        if (this.contextInitializerClasses != null) {
            for (String className : StringUtils.tokenizeToStringArray(this.contextInitializerClasses, INIT_PARAM_DELIMITERS)) {
                this.contextInitializers.add(loadInitializer(className, wac));
            }
        }

        AnnotationAwareOrderComparator.sort(this.contextInitializers);
        for (MyApplicationContextInitializer<MyConfigurableApplicationContext> initializer : this.contextInitializers) {
            initializer.initialize(wac);
        }
    }

    private MyApplicationContextInitializer<MyConfigurableApplicationContext> loadInitializer(
            String className, MyConfigurableApplicationContext wac) {
        try {
            Class<?> initializerClass = ClassUtils.forName(className, wac.getClassLoader());
            Class<?> initializerContextClass =
                    GenericTypeResolver.resolveTypeArgument(initializerClass, MyApplicationContextInitializer.class);
            if (initializerContextClass != null && !initializerContextClass.isInstance(wac)) {
//                throw new MyApplicationContextException(String.format(
//                        "Could not apply context initializer [%s] since its generic parameter [%s] " +
//                                "is not assignable from the type of application context used by this " +
//                                "framework servlet: [%s]", initializerClass.getName(), initializerContextClass.getName(),
//                        wac.getClass().getName()));
            }
            return BeanUtils.instantiateClass(initializerClass, MyApplicationContextInitializer.class);
        }
        catch (ClassNotFoundException ex) {
            throw new MyApplicationContextException(String.format("Could not load class [%s] specified " +
                    "via 'contextInitializerClasses' init-param", className), ex);
        }
    }

    /**
     * 通过反射初始化ContextConfigLocation
     */
    public void setContextConfigLocation(@Nullable String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }

    /**
     * 通过继承重写httpServletBean的serivce
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        MyHttpMethod httpMethod = MyHttpMethod.resolve(request.getMethod());

        if (httpMethod == MyHttpMethod.PATCH || httpMethod == null) {
            processRequest(request, response);
        }
        else {
            super.service(request, response);
        }
    }

    /**
     * 对请求近一步处理 {@link #service}
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        Throwable failureCause = null;

        MyLocaleContext previousLocaleContext = MyLocaleContextHolder.getLocaleContext();
        MyLocaleContext localeContext = buildLocaleContext(request);

        MyRequestAttributes previousAttributes = MyRequestContextHolder.getRequestAttributes();
        MyServletRequestAttributes requestAttributes = buildRequestAttributes(request, response, previousAttributes);

        MyWebAsyncManager asyncManager = MyWebAsyncUtils.getAsyncManager(request);
        asyncManager.registerCallableInterceptor(MyFrameworkServlet.class.getName(), new RequestBindingInterceptor());

        initContextHolders(request, localeContext, requestAttributes);

        try {
            doService(request, response);
        }
        catch (ServletException | IOException ex) {
            failureCause = ex;
            throw ex;
        }
        catch (Throwable ex) {
            failureCause = ex;
            throw new MyNestedServletException("Request processing failed", ex);
        }

        finally {
            resetContextHolders(request, previousLocaleContext, previousAttributes);
            if (requestAttributes != null) {
                requestAttributes.requestCompleted();
            }

            if (logger.isDebugEnabled()) {
                if (failureCause != null) {
                    this.logger.debug("Could not complete request", failureCause);
                }
                else {
                    if (asyncManager.isConcurrentHandlingStarted()) {
                        logger.debug("Leaving response open for concurrent processing");
                    }
                    else {
                        this.logger.debug("Successfully completed request");
                    }
                }
            }

            publishRequestHandledEvent(request, response, startTime, failureCause);
        }
    }

    @Nullable
    protected MyLocaleContext buildLocaleContext(HttpServletRequest request) {
        return new MySimpleLocaleContext(request.getLocale());
    }

    @Nullable
    protected MyServletRequestAttributes buildRequestAttributes(HttpServletRequest request,
                                                              @Nullable HttpServletResponse response, @Nullable MyRequestAttributes previousAttributes) {

        if (previousAttributes == null || previousAttributes instanceof MyServletRequestAttributes) {
            return new MyServletRequestAttributes(request, response);
        }
        else {
            return null;  // preserve the pre-bound RequestAttributes instance
        }
    }

    private class RequestBindingInterceptor implements MyCallableProcessingInterceptor {

        @Override
        public <T> void preProcess(MyNativeWebRequest webRequest, Callable<T> task) {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
                initContextHolders(request, buildLocaleContext(request),
                        buildRequestAttributes(request, response, null));
            }
        }
        @Override
        public <T> void postProcess(MyNativeWebRequest webRequest, Callable<T> task, Object concurrentResult) {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                resetContextHolders(request, null, null);
            }
        }
    }


    private void initContextHolders(HttpServletRequest request,
                                    @Nullable MyLocaleContext localeContext, @Nullable MyRequestAttributes requestAttributes) {

        if (localeContext != null) {
            MyLocaleContextHolder.setLocaleContext(localeContext, this.threadContextInheritable);
        }
        if (requestAttributes != null) {
            MyRequestContextHolder.setRequestAttributes(requestAttributes, this.threadContextInheritable);
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Bound request context to thread: " + request);
        }
    }

    protected abstract void doService(HttpServletRequest request, HttpServletResponse response)
            throws Exception;


    private void resetContextHolders(HttpServletRequest request,
                                     @Nullable MyLocaleContext prevLocaleContext, @Nullable MyRequestAttributes previousAttributes) {

        MyLocaleContextHolder.setLocaleContext(prevLocaleContext, this.threadContextInheritable);
        MyRequestContextHolder.setRequestAttributes(previousAttributes, this.threadContextInheritable);
        if (logger.isTraceEnabled()) {
            logger.trace("Cleared thread-bound request context: " + request);
        }
    }

    private void publishRequestHandledEvent(HttpServletRequest request, HttpServletResponse response,
                                            long startTime, @Nullable Throwable failureCause) {

        if (this.publishEvents && this.webApplicationContext != null) {
            // Whether or not we succeeded, publish an event.
            long processingTime = System.currentTimeMillis() - startTime;
            this.webApplicationContext.publishEvent(
                    new MyServletRequestHandledEvent(this,
                            request.getRequestURI(), request.getRemoteAddr(),
                            request.getMethod(), getServletConfig().getServletName(),
                            MyWebUtils.getSessionId(request), getUsernameForRequest(request),
                            processingTime, failureCause, response.getStatus()));
        }
    }

    @Nullable
    protected String getUsernameForRequest(HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        return (userPrincipal != null ? userPrincipal.getName() : null);
    }

    @Override
    protected final void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    @Nullable
    public final MyWebApplicationContext getWebApplicationContext() {
        return this.webApplicationContext;
    }
}
