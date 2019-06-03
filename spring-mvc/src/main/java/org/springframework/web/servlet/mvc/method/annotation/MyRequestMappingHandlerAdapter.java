package org.springframework.web.servlet.mvc.method.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import org.springframework.http.converter.MyByteArrayHttpMessageConverter;
import org.springframework.http.converter.MyHttpMessageConverter;
import org.springframework.http.converter.MyStringHttpMessageConverter;

import org.springframework.http.converter.support.MyAllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.MySourceHttpMessageConverter;

import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;

import org.springframework.web.bind.annotation.MyInitBinder;
import org.springframework.web.bind.annotation.MyModelAttribute;
import org.springframework.web.bind.annotation.MyRequestMapping;

import org.springframework.web.bind.support.*;
import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.context.request.MyServletWebRequest;

import org.springframework.web.context.request.async.*;
import org.springframework.web.method.MyControllerAdviceBean;
import org.springframework.web.method.MyHandlerMethod;
import org.springframework.web.method.annotation.*;
import org.springframework.web.method.support.*;
import org.springframework.web.servlet.MyModelAndView;
import org.springframework.web.servlet.MyView;
import org.springframework.web.servlet.mvc.method.MyAbstractHandlerMethodAdapter;
import org.springframework.web.servlet.support.MyRedirectAttributes;
import org.springframework.web.servlet.support.MyRequestContextUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-28
 **/
public class MyRequestMappingHandlerAdapter extends MyAbstractHandlerMethodAdapter
        implements BeanFactoryAware, InitializingBean {

    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private ConfigurableBeanFactory beanFactory;

    private final Map<Class<?>, Set<Method>> initBinderCache = new ConcurrentHashMap<>(64);

    private final Map<MyControllerAdviceBean, Set<Method>> initBinderAdviceCache = new LinkedHashMap<>();

    @Nullable
    private MyHandlerMethodArgumentResolverComposite argumentResolvers;

    @Nullable
    private MyHandlerMethodArgumentResolverComposite initBinderArgumentResolvers;

    private MyCallableProcessingInterceptor[] callableInterceptors = new MyCallableProcessingInterceptor[0];
    @Nullable
    private MyWebBindingInitializer webBindingInitializer;

    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    @Nullable
    private MyHandlerMethodReturnValueHandlerComposite returnValueHandlers;

    protected static final String HEADER_CACHE_CONTROL = "Cache-Control";

    private boolean ignoreDefaultModelOnRedirect = false;

    private MyDeferredResultProcessingInterceptor[] deferredResultInterceptors = new MyDeferredResultProcessingInterceptor[0];

    private AsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("MvcAsync");

    private List<MyHttpMessageConverter<?>> messageConverters;

    @Nullable
    private Long asyncRequestTimeout;

    private int cacheSecondsForSessionAttributeHandlers = 0;

    private MySessionAttributeStore sessionAttributeStore = new MyDefaultSessionAttributeStore();

    private final Map<Class<?>, MySessionAttributesHandler> sessionAttributesHandlerCache = new ConcurrentHashMap<>(64);

    private final Map<Class<?>, Set<Method>> modelAttributeCache = new ConcurrentHashMap<>(64);

    private final Map<MyControllerAdviceBean, Set<Method>> modelAttributeAdviceCache = new LinkedHashMap<>();


    public MyRequestMappingHandlerAdapter() {
        MyStringHttpMessageConverter stringHttpMessageConverter = new MyStringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false);  // see SPR-7316

        this.messageConverters = new ArrayList<>(4);
        this.messageConverters.add(new MyByteArrayHttpMessageConverter());
        this.messageConverters.add(stringHttpMessageConverter);
        this.messageConverters.add(new MySourceHttpMessageConverter<>());
        this.messageConverters.add(new MyAllEncompassingFormHttpMessageConverter());
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.beanFactory = (ConfigurableBeanFactory) beanFactory;
        }
    }


    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean supports(Object handler) {
        return true;
    }

    @Override
    protected MyModelAndView handleInternal(HttpServletRequest request,
                                            HttpServletResponse response, MyHandlerMethod handlerMethod) throws Exception {

        MyModelAndView mav;

        mav = invokeHandlerMethod(request, response, handlerMethod);

        return mav;
    }




    protected MyServletInvocableHandlerMethod createInvocableHandlerMethod(MyHandlerMethod handlerMethod) {
        return new MyServletInvocableHandlerMethod(handlerMethod);
    }
    private MyModelFactory getModelFactory(MyHandlerMethod handlerMethod, MyWebDataBinderFactory binderFactory) {
        MySessionAttributesHandler sessionAttrHandler = getSessionAttributesHandler(handlerMethod);
        Class<?> handlerType = handlerMethod.getBeanType();
        Set<Method> methods = this.modelAttributeCache.get(handlerType);
        if (methods == null) {
            methods = MethodIntrospector.selectMethods(handlerType, MODEL_ATTRIBUTE_METHODS);
            this.modelAttributeCache.put(handlerType, methods);
        }
        List<MyInvocableHandlerMethod> attrMethods = new ArrayList<>();
        // Global methods first
        this.modelAttributeAdviceCache.forEach((clazz, methodSet) -> {
            if (clazz.isApplicableToBeanType(handlerType)) {
                Object bean = clazz.resolveBean();
                for (Method method : methodSet) {
                    attrMethods.add(createModelAttributeMethod(binderFactory, bean, method));
                }
            }
        });
        for (Method method : methods) {
            Object bean = handlerMethod.getBean();
            attrMethods.add(createModelAttributeMethod(binderFactory, bean, method));
        }
        return new MyModelFactory(attrMethods, binderFactory, sessionAttrHandler);
    }

    private MyInvocableHandlerMethod createModelAttributeMethod(MyWebDataBinderFactory factory, Object bean, Method method) {
        MyInvocableHandlerMethod attrMethod = new MyInvocableHandlerMethod(bean, method);
        if (this.argumentResolvers != null) {
            attrMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        }
        attrMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
        attrMethod.setDataBinderFactory(factory);
        return attrMethod;
    }

    private MySessionAttributesHandler getSessionAttributesHandler(MyHandlerMethod handlerMethod) {
        Class<?> handlerType = handlerMethod.getBeanType();
        MySessionAttributesHandler sessionAttrHandler = this.sessionAttributesHandlerCache.get(handlerType);
        if (sessionAttrHandler == null) {
            synchronized (this.sessionAttributesHandlerCache) {
                sessionAttrHandler = this.sessionAttributesHandlerCache.get(handlerType);
                if (sessionAttrHandler == null) {
                    sessionAttrHandler = new MySessionAttributesHandler(handlerType, sessionAttributeStore);
                    this.sessionAttributesHandlerCache.put(handlerType, sessionAttrHandler);
                }
            }
        }
        return sessionAttrHandler;
    }

    protected MyModelAndView invokeHandlerMethod(HttpServletRequest request,
                                               HttpServletResponse response, MyHandlerMethod handlerMethod) throws Exception {

        MyServletWebRequest webRequest = new MyServletWebRequest(request, response);
        try {
            //@InitBinder处理
            MyWebDataBinderFactory binderFactory = getDataBinderFactory(handlerMethod);
            //@ModelAttribute处理
            MyModelFactory modelFactory = getModelFactory(handlerMethod, binderFactory);

            MyServletInvocableHandlerMethod invocableMethod = createInvocableHandlerMethod(handlerMethod);
            if (this.argumentResolvers != null) {
                invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
            }
            if (this.returnValueHandlers != null) {
                invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
            }
            invocableMethod.setDataBinderFactory(binderFactory);
            invocableMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);

            MyModelAndViewContainer mavContainer = new MyModelAndViewContainer();
            mavContainer.addAllAttributes(MyRequestContextUtils.getInputFlashMap(request));
//            modelFactory.initModel(webRequest, mavContainer, invocableMethod);
            mavContainer.setIgnoreDefaultModelOnRedirect(this.ignoreDefaultModelOnRedirect);


            //执行Controller中的RequestMapping注释的方法
            invocableMethod.invokeAndHandle(webRequest, mavContainer);

            return getModelAndView(mavContainer, modelFactory, webRequest);
        }
        finally {
            webRequest.requestCompleted();
        }
    }

    @Nullable
    private MyModelAndView getModelAndView(MyModelAndViewContainer mavContainer,
                                           MyModelFactory modelFactory, MyNativeWebRequest webRequest) throws Exception {

        modelFactory.updateModel(webRequest, mavContainer);
        if (mavContainer.isRequestHandled()) {
            return null;
        }
        ModelMap model = mavContainer.getModel();
        MyModelAndView mav = new MyModelAndView(mavContainer.getViewName(), model, mavContainer.getStatus());
        if (!mavContainer.isViewReference()) {
            mav.setView((MyView) mavContainer.getView());
        }
        if (model instanceof MyRedirectAttributes) {
            Map<String, ?> flashAttributes = ((MyRedirectAttributes) model).getFlashAttributes();
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                MyRequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
            }
        }
        return mav;
    }

    private MyWebDataBinderFactory getDataBinderFactory(MyHandlerMethod handlerMethod) throws Exception {
        Class<?> handlerType = handlerMethod.getBeanType();
        Set<Method> methods = this.initBinderCache.get(handlerType);
        if (methods == null) {
            methods = MethodIntrospector.selectMethods(handlerType, INIT_BINDER_METHODS);
            this.initBinderCache.put(handlerType, methods);
        }
        List<MyInvocableHandlerMethod> initBinderMethods = new ArrayList<>();
        // Global methods first
        this.initBinderAdviceCache.forEach((clazz, methodSet) -> {
            if (clazz.isApplicableToBeanType(handlerType)) {
                Object bean = clazz.resolveBean();
                for (Method method : methodSet) {
                    initBinderMethods.add(createInitBinderMethod(bean, method));
                }
            }
        });
        for (Method method : methods) {
            Object bean = handlerMethod.getBean();
            initBinderMethods.add(createInitBinderMethod(bean, method));
        }
        return createDataBinderFactory(initBinderMethods);
    }



    private MyInvocableHandlerMethod createInitBinderMethod(Object bean, Method method) {
        MyInvocableHandlerMethod binderMethod = new MyInvocableHandlerMethod(bean, method);
        if (this.initBinderArgumentResolvers != null) {
            binderMethod.setHandlerMethodArgumentResolvers(this.initBinderArgumentResolvers);
        }
        binderMethod.setDataBinderFactory(new MyDefaultDataBinderFactory(this.webBindingInitializer));
        binderMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
        return binderMethod;
    }

    protected MyInitBinderDataBinderFactory createDataBinderFactory(List<MyInvocableHandlerMethod> binderMethods)
            throws Exception {

        return new MyServletRequestDataBinderFactory(binderMethods, getWebBindingInitializer());
    }

    @Nullable
    public MyWebBindingInitializer getWebBindingInitializer() {
        return this.webBindingInitializer;
    }
    @Override
    public void afterPropertiesSet() {
        // Do this first, it may add ResponseBody advice beans

        if (this.argumentResolvers == null) {
            //为后续参数处理做准备
            List<MyHandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
            this.argumentResolvers = new MyHandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.initBinderArgumentResolvers == null) {
            List<MyHandlerMethodArgumentResolver> resolvers = getDefaultInitBinderArgumentResolvers();
            this.initBinderArgumentResolvers = new MyHandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.returnValueHandlers == null) {
            List<MyHandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
            this.returnValueHandlers = new MyHandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
        }
    }


    private List<MyHandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
        List<MyHandlerMethodArgumentResolver> resolvers = new ArrayList<>();

        // Annotation-based argument resolution
        resolvers.add(new MyRequestParamMethodArgumentResolver(getBeanFactory(), false));
        resolvers.add(new MyRequestParamMethodArgumentResolver(getBeanFactory(), true));
        resolvers.add(new MyServletModelAttributeMethodProcessor(false));
        resolvers.add(new MyServletModelAttributeMethodProcessor(true));
        return resolvers;
    }

    @Nullable
    protected ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }
    /**
     * Return the list of argument resolvers to use for {@code @InitBinder}
     * methods including built-in and custom resolvers.
     */
    private List<MyHandlerMethodArgumentResolver> getDefaultInitBinderArgumentResolvers() {
        return null;
    }

    /**
     * Return the list of return value handlers to use including built-in and
     * custom handlers provided via {@link #}.
     */
    private List<MyHandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {

        List<MyHandlerMethodReturnValueHandler> handlers = new ArrayList<>();

        handlers.add(new MyModelAndViewMethodReturnValueHandler());

        handlers.add(new MyViewMethodReturnValueHandler());

        handlers.add(new  MyViewNameMethodReturnValueHandler());
        return handlers;
    }

    public static final MethodFilter INIT_BINDER_METHODS = method ->
            AnnotationUtils.findAnnotation(method, MyInitBinder.class) != null;

    public static final MethodFilter MODEL_ATTRIBUTE_METHODS = method ->
            ((AnnotationUtils.findAnnotation(method, MyRequestMapping.class) == null) &&
                    (AnnotationUtils.findAnnotation(method, MyModelAttribute.class) != null));

    public void setWebBindingInitializer(@Nullable MyWebBindingInitializer webBindingInitializer) {


    }
}


