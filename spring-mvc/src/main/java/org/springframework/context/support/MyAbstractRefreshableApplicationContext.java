package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.MyApplicationContextException;

import java.io.IOException;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public abstract class MyAbstractRefreshableApplicationContext extends MyAbstractApplicationContext{

    private DefaultListableBeanFactory beanFactory;

    private final Object beanFactoryMonitor = new Object();
    @Override
    public final ConfigurableListableBeanFactory getBeanFactory() {
        synchronized (this.beanFactoryMonitor) {
            if (this.beanFactory == null) {
                throw new IllegalStateException("BeanFactory not initialized or already closed - " +
                        "call 'refresh' before accessing beans via the ApplicationContext");
            }
            return this.beanFactory;
        }
    }

    //创建默认bean工厂
    @Override
    protected final void refreshBeanFactory() throws BeansException {
        if (hasBeanFactory()) {
            destroyBeans();
            closeBeanFactory();
        }
        try {
            DefaultListableBeanFactory beanFactory = createBeanFactory();
            beanFactory.setSerializationId(getId());
            loadBeanDefinitions(beanFactory);
            synchronized (this.beanFactoryMonitor) {
                this.beanFactory = beanFactory;
            }
        }
        catch (IOException ex) {
            throw new MyApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
        }
    }

    //创建工厂
    protected DefaultListableBeanFactory createBeanFactory() {
        //getInternalParentBeanFactory() 返回值为空
        return new DefaultListableBeanFactory(null);
    }


    protected final boolean hasBeanFactory() {
        synchronized (this.beanFactoryMonitor) {
            return (this.beanFactory != null);
        }
    }

    @Override
    protected final void closeBeanFactory() {
        synchronized (this.beanFactoryMonitor) {
            if (this.beanFactory != null) {
                this.beanFactory.setSerializationId(null);
                this.beanFactory = null;
            }
        }
    }


    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
            throws BeansException, IOException;


}
