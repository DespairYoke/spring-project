package org.springframework.context;

import org.springframework.lang.Nullable;

import java.util.Locale;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
public interface MyMessageSource {

    String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);


    String getMessage(String code, @Nullable Object[] args, Locale locale);


    String getMessage(MyMessageSourceResolvable resolvable, Locale locale);

}
