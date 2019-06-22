package org.springframework.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

public interface MyApplicationContextAware  extends Aware {

    void setApplicationContext(MyApplicationContext applicationContext) throws BeansException;
}
