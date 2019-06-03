package org.springframework.web.accept;

import org.springframework.http.MyMediaType;
import org.springframework.web.MyHttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.MyNativeWebRequest;

import java.util.List;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-27
 **/
public class MyContentNegotiationManager implements MyContentNegotiationStrategy, MyMediaTypeFileExtensionResolver {
    @Override
    public List<MyMediaType> resolveMediaTypes(MyNativeWebRequest webRequest) throws MyHttpMediaTypeNotAcceptableException {
        return null;
    }

    @Override
    public List<String> resolveFileExtensions(MyMediaType mediaType) {
        return null;
    }

    @Override
    public List<String> getAllFileExtensions() {
        return null;
    }
}
