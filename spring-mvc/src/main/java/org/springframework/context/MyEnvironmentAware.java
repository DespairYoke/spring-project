package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.env.Environment;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
public interface MyEnvironmentAware extends Aware {

    void setEnvironment(Environment environment);
}
