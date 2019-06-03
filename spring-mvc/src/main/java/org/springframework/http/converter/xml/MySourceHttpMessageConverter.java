package org.springframework.http.converter.xml;

import org.springframework.http.converter.MyAbstractHttpMessageConverter;

import javax.xml.transform.Source;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MySourceHttpMessageConverter<T extends Source> extends MyAbstractHttpMessageConverter<T> {

}
