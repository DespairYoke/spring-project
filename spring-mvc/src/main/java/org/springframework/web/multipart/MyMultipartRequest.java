package org.springframework.web.multipart;

import org.springframework.util.MultiValueMap;

public interface MyMultipartRequest {

    MultiValueMap<String, MyMultipartFile> getMultiFileMap();
}
