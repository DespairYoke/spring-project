package org.springframework.context.i18n;

import org.springframework.lang.Nullable;

import java.util.Locale;

public class MySimpleLocaleContext implements MyLocaleContext{

    private final Locale locale;

    public MySimpleLocaleContext(@Nullable Locale locale) {
        this.locale = locale;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }
}
