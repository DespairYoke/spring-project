package org.springframework.web.bind;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MyMultipartFile;
import org.springframework.web.multipart.MyMultipartRequest;
import org.springframework.web.util.MyWebUtils;

import javax.servlet.ServletRequest;
import java.util.List;
import java.util.Map;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyServletRequestDataBinder extends MyWebDataBinder{
    public MyServletRequestDataBinder(Object target) {
        super(target);
    }

    public MyServletRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public void bind(ServletRequest request) {
        MutablePropertyValues mpvs = new MyServletRequestParameterPropertyValues(request);
        MyMultipartRequest multipartRequest = MyWebUtils.getNativeRequest(request, MyMultipartRequest.class);
        if (multipartRequest != null) {
            bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
        }
        addBindValues(mpvs, request);
//        doBind(mpvs);
    }

    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
    }

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
