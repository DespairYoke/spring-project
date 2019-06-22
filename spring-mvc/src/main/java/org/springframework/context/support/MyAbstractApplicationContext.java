package org.springframework.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.context.*;
import org.springframework.context.event.MyApplicationEventMulticaster;
import org.springframework.context.event.MySimpleApplicationEventMulticaster;
import org.springframework.context.expression.MyStandardBeanExpressionResolver;
import org.springframework.context.weaving.MyLoadTimeWeaverAware;
import org.springframework.context.weaving.MyLoadTimeWeaverAwareProcessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public abstract class MyAbstractApplicationContext extends DefaultResourceLoader
        implements MyConfigurableApplicationContext {

    private final AtomicBoolean active = new AtomicBoolean();

    public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";

    protected final Log logger = LogFactory.getLog(getClass());
    /** Flag that indicates whether this context has been closed already */
    private final AtomicBoolean closed = new AtomicBoolean();

    private final Object startupShutdownMonitor = new Object();

    private ConfigurableEnvironment environment;

    private Set<MyApplicationEvent> earlyApplicationEvents;

    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";



    private final Set<MyApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

    private MyMessageSource messageSource;

    private MyApplicationContext parent;

    private String displayName = ObjectUtils.identityToString(this);

    private MyApplicationEventMulticaster applicationEventMulticaster;


    @Override
    public void refresh() throws BeansException, IllegalStateException {
        synchronized (this.startupShutdownMonitor) {
            // Prepare this context for refreshing.
            prepareRefresh();

            // Tell the subclass to refresh the internal bean factory.
            ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

            // Prepare the bean factory for use in this context.
            prepareBeanFactory(beanFactory);

            try {
                // Allows post-processing of the bean factory in context subclasses.
                postProcessBeanFactory(beanFactory);

                // Invoke factory processors registered as beans in the context.
                invokeBeanFactoryPostProcessors(beanFactory);

                // Register bean processors that intercept bean creation.
                registerBeanPostProcessors(beanFactory);

                // Initialize message source for this context.
                initMessageSource();

                // Initialize event multicaster for this context.
                initApplicationEventMulticaster();

                // Initialize other special beans in specific context subclasses.
                onRefresh();

                // Check for listener beans and register them.
                registerListeners();

                // Instantiate all remaining (non-lazy-init) singletons.
                finishBeanFactoryInitialization(beanFactory);

                // Last step: publish corresponding event.
                finishRefresh();
            }

            catch (BeansException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Exception encountered during context initialization - " +
                            "cancelling refresh attempt: " + ex);
                }

                // Destroy already created singletons to avoid dangling resources.
                destroyBeans();

                // Reset 'active' flag.
                cancelRefresh(ex);

                // Propagate exception to caller.
                throw ex;
            }

            finally {
                // Reset common introspection caches in Spring's core, since we
                // might not ever need metadata for singleton beans anymore...
                resetCommonCaches();
            }
        }
    }

    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        // Initialize conversion service for this context.
        if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
                beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
            beanFactory.setConversionService(
                    beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
        }

        // Register a default embedded value resolver if no bean post-processor
        // (such as a PropertyPlaceholderConfigurer bean) registered any before:
        // at this point, primarily for resolution in annotation attribute values.
        if (!beanFactory.hasEmbeddedValueResolver()) {
            beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
        }

        // Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
        String[] weaverAwareNames = beanFactory.getBeanNamesForType(MyLoadTimeWeaverAware.class, false, false);
        for (String weaverAwareName : weaverAwareNames) {
            getBean(weaverAwareName);
        }

        // Stop using the temporary ClassLoader for type matching.
        beanFactory.setTempClassLoader(null);

        // Allow for caching all bean definition metadata, not expecting further changes.
        beanFactory.freezeConfiguration();

        // Instantiate all remaining (non-lazy-init) singletons.
        beanFactory.preInstantiateSingletons();
    }

    protected void onRefresh() throws BeansException {
        // For subclasses: do nothing by default.
    }

    public Collection<MyApplicationListener<?>> getApplicationListeners() {
        return this.applicationListeners;
    }

    MyApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
        if (this.applicationEventMulticaster == null) {
            throw new IllegalStateException("ApplicationEventMulticaster not initialized - " +
                    "call 'refresh' before multicasting events via the context: " + this);
        }
        return this.applicationEventMulticaster;
    }


    protected void registerListeners() {
        // Register statically specified listeners first.
        for (MyApplicationListener<?> listener : getApplicationListeners()) {
            getApplicationEventMulticaster().addApplicationListener(listener);
        }

        // Do not initialize FactoryBeans here: We need to leave all regular beans
        // uninitialized to let post-processors apply to them!
        String[] listenerBeanNames = getBeanNamesForType(MyApplicationListener.class, true, false);
        for (String listenerBeanName : listenerBeanNames) {
            getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
        }

        // Publish early application events now that we finally have a multicaster...
        Set<MyApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
        this.earlyApplicationEvents = null;
        if (earlyEventsToProcess != null) {
            for (MyApplicationEvent earlyEvent : earlyEventsToProcess) {
                getApplicationEventMulticaster().multicastEvent(earlyEvent);
            }
        }
    }

    protected void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
            this.applicationEventMulticaster =
                    beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, MyApplicationEventMulticaster.class);
            if (logger.isDebugEnabled()) {
                logger.debug("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
            }
        }
        else {
            this.applicationEventMulticaster = new MySimpleApplicationEventMulticaster(beanFactory);
            beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to locate ApplicationEventMulticaster with name '" +
                        APPLICATION_EVENT_MULTICASTER_BEAN_NAME +
                        "': using default [" + this.applicationEventMulticaster + "]");
            }
        }
    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    }

    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        MyPostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
    }

    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        MyPostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());

        // Detect a LoadTimeWeaver and prepare for weaving, if found in the meantime
        // (e.g. through an @Bean method registered by ConfigurationClassPostProcessor)
        if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
            beanFactory.addBeanPostProcessor(new MyLoadTimeWeaverAwareProcessor(beanFactory));
            beanFactory.setTempClassLoader(new MyContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }
    }

    public MyApplicationContext getParent() {
        return this.parent;
    }

    protected MyMessageSource getInternalParentMessageSource() {
        return (getParent() instanceof MyAbstractApplicationContext) ?
                ((MyAbstractApplicationContext) getParent()).messageSource : getParent();
    }

    protected void initMessageSource() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
            this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MyMessageSource.class);
            // Make MessageSource aware of parent MessageSource.
            if (this.parent != null && this.messageSource instanceof MyHierarchicalMessageSource) {
                MyHierarchicalMessageSource hms = (MyHierarchicalMessageSource) this.messageSource;
                if (hms.getParentMessageSource() == null) {
                    // Only set parent context as parent MessageSource if no parent MessageSource
                    // registered already.
                    hms.setParentMessageSource(getInternalParentMessageSource());
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Using MessageSource [" + this.messageSource + "]");
            }
        }
        else {
            // Use empty MessageSource to be able to accept getMessage calls.
            MyDelegatingMessageSource dms = new MyDelegatingMessageSource();
            dms.setParentMessageSource(getInternalParentMessageSource());
            this.messageSource = dms;
            beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
            if (logger.isDebugEnabled()) {
                logger.debug("Unable to locate MessageSource with name '" + MESSAGE_SOURCE_BEAN_NAME +
                        "': using default [" + this.messageSource + "]");
            }
        }
    }
    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardEnvironment();
    }


    protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;

    public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

    @Override
    public ConfigurableEnvironment getEnvironment() {
        if (this.environment == null) {
            this.environment = createEnvironment();
        }
        return this.environment;
    }
    protected void prepareRefresh() {
        this.closed.set(false);
        this.active.set(true);

        if (logger.isInfoEnabled()) {
            logger.info("Refreshing " + this);
        }

        // Initialize any placeholder property sources in the context environment
        initPropertySources();

        // Validate that all properties marked as required are resolvable
        // see ConfigurablePropertyResolver#setRequiredProperties
        getEnvironment().validateRequiredProperties();

        // Allow for the collection of early ApplicationEvents,
        // to be published once the multicaster is available...
        this.earlyApplicationEvents = new LinkedHashSet<>();
    }

    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        // // 设置classLoader(用于加载bean)
        beanFactory.setBeanClassLoader(getClassLoader());
        // 设置表达式解析器StandardBeanExpressionResolver(解析bean定义中的一些表达式)
        beanFactory.setBeanExpressionResolver(new MyStandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
        // 添加属性编辑注册器(注册属性编辑器)
        beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

        // 添加ApplicationContextAwareProcessor这个BeanPostProcessor，保存在beanFactory的beanPostProcessors列表中
        beanFactory.addBeanPostProcessor(new MyApplicationContextAwareProcessor(this));
        /**
         * 取消
         * EnvironmentAware,
         * EmbeddedValueResolverAware,
         * ResourceLoaderAware,
         * ApplicationEventPublisherAware,
         * MessageSourceAware,
         * ApplicationContextAware这6个接口的自动注入。
         * 因为ApplicatioinContextAwareProcessor把这6这个接口的实现工作做了
         *
         * 保存在beanFactory的ignoredDependencyInterfaces集合中
         *
         * ApplicatioinContextAwareProcessor的作用在于为实现*Aware接口的bean调用该Aware接口定义的方法，并传入对应的参数。
         * 比如实现EnvironmentAware接口的bean在该Processor内部会调用EnvironmentAware接口的setEnvironment方法，并把Spring容器内部的ConfigurationEnvironment传递进去。
         */
        beanFactory.ignoreDependencyInterface(MyEnvironmentAware.class);
        beanFactory.ignoreDependencyInterface(MyEmbeddedValueResolverAware.class);
        beanFactory.ignoreDependencyInterface(MyResourceLoaderAware.class);
        beanFactory.ignoreDependencyInterface(MyApplicationEventPublisherAware.class);
        beanFactory.ignoreDependencyInterface(MyMessageSourceAware.class);
        beanFactory.ignoreDependencyInterface(MyApplicationContextAware.class);

        // BeanFactory interface not registered as resolvable type in a plain factory.
        // MessageSource registered (and found for autowiring) as a bean.
        beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
        beanFactory.registerResolvableDependency(ResourceLoader.class, this);
        beanFactory.registerResolvableDependency(MyApplicationEventPublisher.class, this);
        beanFactory.registerResolvableDependency(MyApplicationContext.class, this);

        // 注册ApplicationListenerDetector，用于发现实现了ApplicationListener接口的bean
        beanFactory.addBeanPostProcessor(new MyApplicationListenerDetector(this));

        // 检查代码织入
        if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
            beanFactory.addBeanPostProcessor(new MyLoadTimeWeaverAwareProcessor(beanFactory));
            // Set a temporary ClassLoader for type matching.
            beanFactory.setTempClassLoader(new MyContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }

        /**
         * 注册默认的environment, systemProperties, systemEnvironment
         * 保存在beanFactory的singletonObjects(ConcurrentHashMap)、registeredSingletons(LinkedHashSet)、manualSingletonNames(LinkedHashSet)中
         */
        if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
            beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
        }
        if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
            beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
        }
        if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
            beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
        }
    }
    protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        refreshBeanFactory();
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        if (logger.isDebugEnabled()) {
            logger.debug("Bean factory for " + getDisplayName() + ": " + beanFactory);
        }
        return beanFactory;
    }

    protected void initPropertySources() {
        // For subclasses: do nothing by default.
    }
}
