package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.util.StringValueResolver;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-06-06
 **/
public interface MyEmbeddedValueResolverAware extends Aware {

    void setEmbeddedValueResolver(StringValueResolver resolver);
}
