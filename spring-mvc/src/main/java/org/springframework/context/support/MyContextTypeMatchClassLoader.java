package org.springframework.context.support;

import org.springframework.core.DecoratingClassLoader;
import org.springframework.core.SmartClassLoader;
import org.springframework.lang.Nullable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
class MyContextTypeMatchClassLoader extends DecoratingClassLoader implements SmartClassLoader {

    public MyContextTypeMatchClassLoader(@Nullable ClassLoader parent) {
        super(parent);
    }

}
