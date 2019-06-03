package org.springframework.web.multipart;

import org.springframework.core.io.InputStreamSource;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public interface MyMultipartFile extends InputStreamSource {

    boolean isEmpty();
}
