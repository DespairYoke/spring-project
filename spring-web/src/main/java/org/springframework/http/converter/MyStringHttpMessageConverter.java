package org.springframework.http.converter;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-29
 **/
public class MyStringHttpMessageConverter extends MyAbstractHttpMessageConverter<String>{

    private boolean writeAcceptCharset = true;


    public void setWriteAcceptCharset(boolean writeAcceptCharset) {
        this.writeAcceptCharset = writeAcceptCharset;
    }
}
