package org.springframework.web.accept;

import org.springframework.http.MyMediaType;
import org.springframework.web.MyHttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.MyNativeWebRequest;

import java.util.Collections;
import java.util.List;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-27
 **/
@FunctionalInterface
public interface MyContentNegotiationStrategy {


    List<MyMediaType> MEDIA_TYPE_ALL_LIST = Collections.singletonList(MyMediaType.ALL);

    List<MyMediaType> resolveMediaTypes(MyNativeWebRequest webRequest)
            throws MyHttpMediaTypeNotAcceptableException;
}
