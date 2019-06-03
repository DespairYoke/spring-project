package org.springframework.web.util;

import org.springframework.util.StringUtils;

import java.nio.charset.Charset;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-27
 **/
public class MyUriUtils {

    public static String decode(String source, String encoding) {
        return StringUtils.uriDecode(source, Charset.forName(encoding));
    }

    public static String encodePathSegment(String segment, String encoding) {
        return encode(segment, encoding, MyHierarchicalUriComponents.Type.PATH_SEGMENT);
    }


    private static String encode(String scheme, String encoding, MyHierarchicalUriComponents.Type type) {
        return MyHierarchicalUriComponents.encodeUriComponent(scheme, encoding, type);
    }

}
