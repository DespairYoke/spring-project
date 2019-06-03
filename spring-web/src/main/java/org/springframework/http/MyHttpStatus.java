package org.springframework.http;

import org.springframework.lang.Nullable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-25
 **/
public enum  MyHttpStatus {

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SEE_OTHER(303, "See Other"),
    ;

    private final int value;

    private final String reasonPhrase;


    MyHttpStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }
    @Nullable
    public static MyHttpStatus resolve(int statusCode) {
        for (MyHttpStatus status : values()) {
            if (status.value == statusCode) {
                return status;
            }
        }
        return null;
    }

    public int value() {
        return this.value;
    }
}
