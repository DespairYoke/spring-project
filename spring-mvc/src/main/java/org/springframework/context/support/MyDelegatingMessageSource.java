package org.springframework.context.support;

import org.springframework.context.MyHierarchicalMessageSource;
import org.springframework.context.MyMessageSource;
import org.springframework.context.MyMessageSourceResolvable;

import java.util.Locale;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
public class MyDelegatingMessageSource extends MyMessageSourceSupport implements MyHierarchicalMessageSource {
    @Override
    public void setParentMessageSource(MyMessageSource parent) {
        
    }

    @Override
    public MyMessageSource getParentMessageSource() {
        return null;
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return null;
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) {
        return null;
    }

    @Override
    public String getMessage(MyMessageSourceResolvable resolvable, Locale locale) {
        return null;
    }
}
