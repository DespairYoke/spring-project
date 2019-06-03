package org.springframework.web.accept;

import org.springframework.http.MyMediaType;

import java.util.List;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-27
 **/
public interface MyMediaTypeFileExtensionResolver {

    List<String> resolveFileExtensions(MyMediaType mediaType);

    /**
     * Return all registered file extensions.
     * @return a list of extensions or an empty list (never {@code null})
     */
    List<String> getAllFileExtensions();
}
