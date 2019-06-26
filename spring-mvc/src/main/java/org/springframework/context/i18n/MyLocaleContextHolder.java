package org.springframework.context.i18n;

import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

public abstract class MyLocaleContextHolder {
    private static final ThreadLocal<MyLocaleContext> localeContextHolder =
            new NamedThreadLocal<>("LocaleContext");

    private static final ThreadLocal<MyLocaleContext> inheritableLocaleContextHolder =
            new NamedInheritableThreadLocal<>("LocaleContext");
    public static MyLocaleContext getLocaleContext() {
        MyLocaleContext localeContext = localeContextHolder.get();
        if (localeContext == null) {
            localeContext = inheritableLocaleContextHolder.get();
        }
        return localeContext;
    }

    public static void setLocaleContext(@Nullable MyLocaleContext localeContext) {
        setLocaleContext(localeContext, false);
    }
    public static void resetLocaleContext() {
        localeContextHolder.remove();
        inheritableLocaleContextHolder.remove();
    }

    public static void setLocaleContext(@Nullable MyLocaleContext localeContext, boolean inheritable) {
        if (localeContext == null) {
            resetLocaleContext();
        }
        else {
            if (inheritable) {
                inheritableLocaleContextHolder.set(localeContext);
                localeContextHolder.remove();
            }
            else {
                localeContextHolder.set(localeContext);
                inheritableLocaleContextHolder.remove();
            }
        }
    }
}
