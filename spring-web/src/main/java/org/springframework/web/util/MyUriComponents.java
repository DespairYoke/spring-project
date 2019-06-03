package org.springframework.web.util;

import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
public abstract class MyUriComponents implements Serializable {

    @Nullable
    private final String scheme;

    @Nullable
    private final String fragment;

    private static final Pattern NAMES_PATTERN = Pattern.compile("\\{([^/]+?)\\}");


    protected MyUriComponents(@Nullable String scheme, @Nullable String fragment) {
        this.scheme = scheme;
        this.fragment = fragment;
    }
    public abstract MultiValueMap<String, String> getQueryParams();

    public abstract String getPath();

    public interface UriTemplateVariables {

        Object SKIP_VALUE = UriTemplateVariables.class;

        /**
         * Get the value for the given URI variable name.
         * If the value is {@code null}, an empty String is expanded.
         * If the value is {@link #SKIP_VALUE}, the URI variable is not expanded.
         * @param name the variable name
         * @return the variable value, possibly {@code null} or {@link #SKIP_VALUE}
         */
        @Nullable
        Object getValue(@Nullable String name);
    }

    public final String getScheme() {
        return this.scheme;
    }
    public final String getFragment() {
        return this.fragment;
    }

    public abstract String getHost();

    static String expandUriComponent(@Nullable String source, UriTemplateVariables uriVariables) {
        if (source == null) {
            return null;
        }
        if (source.indexOf('{') == -1) {
            return source;
        }
        if (source.indexOf(':') != -1) {
            source = sanitizeSource(source);
        }
        Matcher matcher = NAMES_PATTERN.matcher(source);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String match = matcher.group(1);
            String variableName = getVariableName(match);
            Object variableValue = uriVariables.getValue(variableName);
            if (UriTemplateVariables.SKIP_VALUE.equals(variableValue)) {
                continue;
            }
            String variableValueString = getVariableValueAsString(variableValue);
            String replacement = Matcher.quoteReplacement(variableValueString);
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    private static String getVariableName(String match) {
        int colonIdx = match.indexOf(':');
        return (colonIdx != -1 ? match.substring(0, colonIdx) : match);
    }


    private static String getVariableValueAsString(@Nullable Object variableValue) {
        return (variableValue != null ? variableValue.toString() : "");
    }


    private static String sanitizeSource(String source) {
        int level = 0;
        StringBuilder sb = new StringBuilder();
        for (char c : source.toCharArray()) {
            if (c == '{') {
                level++;
            }
            if (c == '}') {
                level--;
            }
            if (level > 1 || (level == 1 && c == '}')) {
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }



}
