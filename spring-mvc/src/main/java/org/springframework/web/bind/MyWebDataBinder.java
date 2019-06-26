package org.springframework.web.bind;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.CollectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.validation.MyDataBinder;
import org.springframework.web.multipart.MyMultipartFile;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyWebDataBinder extends MyDataBinder {



    public static final String DEFAULT_FIELD_DEFAULT_PREFIX = "!";

    public static final String DEFAULT_FIELD_MARKER_PREFIX = "_";

    private boolean bindEmptyMultipartFiles = true;

    private String fieldDefaultPrefix = DEFAULT_FIELD_DEFAULT_PREFIX;

    private String fieldMarkerPrefix = DEFAULT_FIELD_MARKER_PREFIX;

    public MyWebDataBinder(Object target) {
        super(target);
    }
    public MyWebDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public boolean isBindEmptyMultipartFiles() {
        return this.bindEmptyMultipartFiles;
    }

    public String getFieldDefaultPrefix() {
        return this.fieldDefaultPrefix;
    }

    public String getFieldMarkerPrefix() {
        return this.fieldMarkerPrefix;
    }

    public Object getEmptyValue(Class<?> fieldType) {
        try {
            if (boolean.class == fieldType || Boolean.class == fieldType) {
                // Special handling of boolean property.
                return Boolean.FALSE;
            }
            else if (fieldType.isArray()) {
                // Special handling of array property.
                return Array.newInstance(fieldType.getComponentType(), 0);
            }
            else if (Collection.class.isAssignableFrom(fieldType)) {
                return CollectionFactory.createCollection(fieldType, 0);
            }
            else if (Map.class.isAssignableFrom(fieldType)) {
                return CollectionFactory.createMap(fieldType, 0);
            }
        }
        catch (IllegalArgumentException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to create default value - falling back to null: " + ex.getMessage());
            }
        }
        // Default value: null.
        return null;
    }

//    public void bind(MyWebRequest request) {
//        MutablePropertyValues mpvs = new MutablePropertyValues(request.getParameterMap());
//        if (isMultipartRequest(request) && request instanceof NativeWebRequest) {
//            MultipartRequest multipartRequest = ((NativeWebRequest) request).getNativeRequest(MultipartRequest.class);
//            if (multipartRequest != null) {
//                bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
//            }
//            else {
//                HttpServletRequest servletRequest = ((NativeWebRequest) request).getNativeRequest(HttpServletRequest.class);
//                if (servletRequest != null) {
//                    bindParts(servletRequest, mpvs);
//                }
//            }
//        }
//        doBind(mpvs);
//    }
protected void bindMultipart(Map<String, List<MyMultipartFile>> multipartFiles, MutablePropertyValues mpvs) {
    multipartFiles.forEach((key, values) -> {
        if (values.size() == 1) {
            MyMultipartFile value = values.get(0);
            if (isBindEmptyMultipartFiles() || !value.isEmpty()) {
                mpvs.add(key, value);
            }
        }
        else {
            mpvs.add(key, values);
        }
    });
}
}
