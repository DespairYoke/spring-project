package org.springframework.context.support;

import org.springframework.context.MyMessageSource;
import org.springframework.context.MyMessageSourceResolvable;

import java.util.Locale;

public class MyResourceBundleMessageSource extends MyAbstractResourceBasedMessageSource {
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
