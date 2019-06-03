package org.springframework.http;

import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
public class MyCacheControl {
    private long maxAge = -1;

    private boolean noCache = false;

    private boolean noStore = false;

    private boolean mustRevalidate = false;

    private boolean noTransform = false;

    private boolean cachePublic = false;

    private boolean cachePrivate = false;

    private boolean proxyRevalidate = false;

    private long staleWhileRevalidate = -1;

    private long staleIfError = -1;

    private long sMaxAge = -1;



    public static MyCacheControl empty() {
        return new MyCacheControl();
    }

    public static MyCacheControl maxAge(long maxAge, TimeUnit unit) {
        MyCacheControl cc = new MyCacheControl();
        cc.maxAge = unit.toSeconds(maxAge);
        return cc;
    }

    public MyCacheControl mustRevalidate() {
        this.mustRevalidate = true;
        return this;
    }

    public static MyCacheControl noStore() {
        MyCacheControl cc = new MyCacheControl();
        cc.noStore = true;
        return cc;
    }
    public static MyCacheControl noCache() {
        MyCacheControl cc = new MyCacheControl();
        cc.noCache = true;
        return cc;
    }

    public String getHeaderValue() {
        StringBuilder ccValue = new StringBuilder();
        if (this.maxAge != -1) {
            appendDirective(ccValue, "max-age=" + Long.toString(this.maxAge));
        }
        if (this.noCache) {
            appendDirective(ccValue, "no-cache");
        }
        if (this.noStore) {
            appendDirective(ccValue, "no-store");
        }
        if (this.mustRevalidate) {
            appendDirective(ccValue, "must-revalidate");
        }
        if (this.noTransform) {
            appendDirective(ccValue, "no-transform");
        }
        if (this.cachePublic) {
            appendDirective(ccValue, "public");
        }
        if (this.cachePrivate) {
            appendDirective(ccValue, "private");
        }
        if (this.proxyRevalidate) {
            appendDirective(ccValue, "proxy-revalidate");
        }
        if (this.sMaxAge != -1) {
            appendDirective(ccValue, "s-maxage=" + Long.toString(this.sMaxAge));
        }
        if (this.staleIfError != -1) {
            appendDirective(ccValue, "stale-if-error=" + Long.toString(this.staleIfError));
        }
        if (this.staleWhileRevalidate != -1) {
            appendDirective(ccValue, "stale-while-revalidate=" + Long.toString(this.staleWhileRevalidate));
        }

        String ccHeaderValue = ccValue.toString();
        return (StringUtils.hasText(ccHeaderValue) ? ccHeaderValue : null);
    }


    private void appendDirective(StringBuilder builder, String value) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(value);
    }

}
