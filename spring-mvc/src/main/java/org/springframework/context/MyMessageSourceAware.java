package org.springframework.context;

import org.springframework.beans.factory.Aware;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
public interface MyMessageSourceAware extends Aware {

    void setMessageSource(MyMessageSource messageSource);

}
