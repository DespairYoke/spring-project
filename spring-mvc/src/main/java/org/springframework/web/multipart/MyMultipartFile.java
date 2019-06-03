package org.springframework.web.multipart;

import org.springframework.core.io.InputStreamSource;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public interface MyMultipartFile extends InputStreamSource {

    boolean isEmpty();
}
