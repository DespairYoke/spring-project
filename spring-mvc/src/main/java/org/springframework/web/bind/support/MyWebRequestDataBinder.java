package org.springframework.web.bind.support;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MyWebDataBinder;
import org.springframework.web.context.request.MyNativeWebRequest;
import org.springframework.web.context.request.MyWebRequest;
import org.springframework.web.multipart.MyMultipartFile;
import org.springframework.web.multipart.MyMultipartRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyWebRequestDataBinder extends MyWebDataBinder {
    public MyWebRequestDataBinder(Object target) {
        super(target);
    }
    public MyWebRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }
    public void bind(MyWebRequest request) {
        MutablePropertyValues mpvs = new MutablePropertyValues(request.getParameterMap());
        if (isMultipartRequest(request) && request instanceof MyNativeWebRequest) {
            MyMultipartRequest multipartRequest = ((MyNativeWebRequest) request).getNativeRequest(MyMultipartRequest.class);
            if (multipartRequest != null) {
                bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
            }
            else {
                HttpServletRequest servletRequest = ((MyNativeWebRequest) request).getNativeRequest(HttpServletRequest.class);
                if (servletRequest != null) {
//                    bindParts(servletRequest, mpvs);
                }
            }
        }
//        doBind(mpvs);
    }


    private boolean isMultipartRequest(MyWebRequest request) {
        String contentType = request.getHeader("Content-Type");
        return (contentType != null && StringUtils.startsWithIgnoreCase(contentType, "multipart"));
    }


}
