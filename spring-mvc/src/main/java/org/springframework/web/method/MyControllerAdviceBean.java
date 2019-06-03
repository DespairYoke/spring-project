package org.springframework.web.method;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.MyControllerAdvice;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyControllerAdviceBean implements Ordered {

    private final Object bean;

    private final BeanFactory beanFactory;

    private final Set<String> basePackages;

    private final int order;

    private final List<Class<?>> assignableTypes;

    private final List<Class<? extends Annotation>> annotations;


    public MyControllerAdviceBean(Object bean) {
        this(bean, null);
    }

    /**
     * Create a {@code ControllerAdviceBean} using the given bean name.
     * @param beanName the name of the bean
     * @param beanFactory a BeanFactory that can be used later to resolve the bean
     */
    public MyControllerAdviceBean(String beanName, @Nullable BeanFactory beanFactory) {
        this((Object) beanName, beanFactory);
    }

    public Object resolveBean() {
        return (this.bean instanceof String ? obtainBeanFactory().getBean((String) this.bean) : this.bean);
    }

    private BeanFactory obtainBeanFactory() {
        Assert.state(this.beanFactory != null, "No BeanFactory set");
        return this.beanFactory;
    }


    private MyControllerAdviceBean(Object bean, @Nullable BeanFactory beanFactory) {
        this.bean = bean;
        this.beanFactory = beanFactory;
        Class<?> beanType;

        if (bean instanceof String) {
            String beanName = (String) bean;
            Assert.hasText(beanName, "Bean name must not be null");
            Assert.notNull(beanFactory, "BeanFactory must not be null");
            if (!beanFactory.containsBean(beanName)) {
                throw new IllegalArgumentException("BeanFactory [" + beanFactory +
                        "] does not contain specified controller advice bean '" + beanName + "'");
            }
            beanType = this.beanFactory.getType(beanName);
            this.order = initOrderFromBeanType(beanType);
        }
        else {
            Assert.notNull(bean, "Bean must not be null");
            beanType = bean.getClass();
            this.order = initOrderFromBean(bean);
        }

        MyControllerAdvice annotation = (beanType != null ?
                AnnotatedElementUtils.findMergedAnnotation(beanType, MyControllerAdvice.class) : null);

        if (annotation != null) {
            this.basePackages = initBasePackages(annotation);
            this.assignableTypes = Arrays.asList(annotation.assignableTypes());
            this.annotations = Arrays.asList(annotation.annotations());
        }
        else {
            this.basePackages = Collections.emptySet();
            this.assignableTypes = Collections.emptyList();
            this.annotations = Collections.emptyList();
        }
    }
    @Override
    public int getOrder() {
        return 0;
    }

    private static Set<String> initBasePackages(MyControllerAdvice annotation) {
        Set<String> basePackages = new LinkedHashSet<>();
        for (String basePackage : annotation.basePackages()) {
            if (StringUtils.hasText(basePackage)) {
                basePackages.add(adaptBasePackage(basePackage));
            }
        }
        for (Class<?> markerClass : annotation.basePackageClasses()) {
            basePackages.add(adaptBasePackage(ClassUtils.getPackageName(markerClass)));
        }
        return basePackages;
    }


    private static String adaptBasePackage(String basePackage) {
        return (basePackage.endsWith(".") ? basePackage : basePackage + ".");
    }

    private static int initOrderFromBean(Object bean) {
        return (bean instanceof Ordered ? ((Ordered) bean).getOrder() : initOrderFromBeanType(bean.getClass()));
    }

    private static int initOrderFromBeanType(@Nullable Class<?> beanType) {
        Integer order = null;
        if (beanType != null) {
            order = OrderUtils.getOrder(beanType);
        }
        return (order != null ? order : Ordered.LOWEST_PRECEDENCE);
    }

    public boolean isApplicableToBeanType(@Nullable Class<?> beanType) {
        if (!hasSelectors()) {
            return true;
        }
        else if (beanType != null) {
            for (String basePackage : this.basePackages) {
                if (beanType.getName().startsWith(basePackage)) {
                    return true;
                }
            }
            for (Class<?> clazz : this.assignableTypes) {
                if (ClassUtils.isAssignable(clazz, beanType)) {
                    return true;
                }
            }
            for (Class<? extends Annotation> annotationClass : this.annotations) {
                if (AnnotationUtils.findAnnotation(beanType, annotationClass) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasSelectors() {
        return (!this.basePackages.isEmpty() || !this.assignableTypes.isEmpty() || !this.annotations.isEmpty());
    }
}
