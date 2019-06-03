package org.springframework.http;

import org.springframework.util.InvalidMimeTypeException;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-27
 **/
public class MyInvalidMediaTypeException extends IllegalArgumentException{

    private String mediaType;


    MyInvalidMediaTypeException(InvalidMimeTypeException ex) {
        super(ex.getMessage(), ex);
        this.mediaType = ex.getMimeType();
    }

    public MyInvalidMediaTypeException(String mediaType, String message) {
        super("Invalid media type \"" + mediaType + "\": " + message);
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return this.mediaType;
    }
}
