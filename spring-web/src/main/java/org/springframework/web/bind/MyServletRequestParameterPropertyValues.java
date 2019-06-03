package org.springframework.web.bind;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.web.util.MyWebUtils;

import javax.servlet.ServletRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyServletRequestParameterPropertyValues extends MutablePropertyValues {

    public MyServletRequestParameterPropertyValues(ServletRequest request) {
        this(request, null, null);
    }

    public MyServletRequestParameterPropertyValues(
            ServletRequest request, @Nullable String prefix, @Nullable String prefixSeparator) {

        super(MyWebUtils.getParametersStartingWith(
                request, (prefix != null ? prefix + prefixSeparator : null)));
    }
}
