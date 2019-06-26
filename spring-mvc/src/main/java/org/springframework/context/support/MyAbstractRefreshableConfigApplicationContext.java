package org.springframework.context.support;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-04
 **/
public abstract class MyAbstractRefreshableConfigApplicationContext extends MyAbstractRefreshableApplicationContext
        implements BeanNameAware, InitializingBean {

    private String[] configLocations;

    protected String[] getConfigLocations() {
        return (this.configLocations != null ? this.configLocations : getDefaultConfigLocations());
    }
    protected String[] getDefaultConfigLocations() {
        return null;
    }
}
