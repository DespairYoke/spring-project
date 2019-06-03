/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.util;

import java.io.Serializable;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.springframework.http.MyHttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.MyUriComponents.UriTemplateVariables;

import org.springframework.web.util.MyHierarchicalUriComponents.PathComponent;

public class MyUriComponentsBuilder implements MyUriBuilder, Cloneable {

    private static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");

    private static final String SCHEME_PATTERN = "([^:/?#]+):";

    private static final String HTTP_PATTERN = "(?i)(http|https):";

    private static final String USERINFO_PATTERN = "([^@\\[/?#]*)";

    private static final String HOST_IPV4_PATTERN = "[^\\[/?#:]*";

    private static final String HOST_IPV6_PATTERN = "\\[[\\p{XDigit}\\:\\.]*[%\\p{Alnum}]*\\]";

    private static final String HOST_PATTERN = "(" + HOST_IPV6_PATTERN + "|" + HOST_IPV4_PATTERN + ")";

    private static final String PORT_PATTERN = "(\\d*(?:\\{[^/]+?\\})?)";

    private static final String PATH_PATTERN = "([^?#]*)";

    private static final String QUERY_PATTERN = "([^#]*)";

    private static final String LAST_PATTERN = "(.*)";

    // Regex patterns that matches URIs. See RFC 3986, appendix B
    private static final Pattern URI_PATTERN = Pattern.compile(
            "^(" + SCHEME_PATTERN + ")?" + "(//(" + USERINFO_PATTERN + "@)?" + HOST_PATTERN + "(:" + PORT_PATTERN +
                    ")?" + ")?" + PATH_PATTERN + "(\\?" + QUERY_PATTERN + ")?" + "(#" + LAST_PATTERN + ")?");

    private static final Pattern HTTP_URL_PATTERN = Pattern.compile(
            "^" + HTTP_PATTERN + "(//(" + USERINFO_PATTERN + "@)?" + HOST_PATTERN + "(:" + PORT_PATTERN + ")?" + ")?" +
                    PATH_PATTERN + "(\\?" + LAST_PATTERN + ")?");

    private static final Pattern FORWARDED_HOST_PATTERN = Pattern.compile("host=\"?([^;,\"]+)\"?");

    private static final Pattern FORWARDED_PROTO_PATTERN = Pattern.compile("proto=\"?([^;,\"]+)\"?");


    @Nullable
    private String scheme;

    @Nullable
    private String ssp;

    @Nullable
    private String userInfo;

    @Nullable
    private String host;

    @Nullable
    private String port;

    private CompositePathComponentBuilder pathBuilder;

    private final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

    @Nullable
    private String fragment;


    protected MyUriComponentsBuilder() {
        this.pathBuilder = new CompositePathComponentBuilder();
    }

    /**
     * Create a deep copy of the given UriComponentsBuilder.
     * @param other the other builder to copy from
     * @since 4.1.3
     */
    protected MyUriComponentsBuilder(MyUriComponentsBuilder other) {
        this.scheme = other.scheme;
        this.ssp = other.ssp;
        this.userInfo = other.userInfo;
        this.host = other.host;
        this.port = other.port;
        this.pathBuilder = other.pathBuilder.cloneBuilder();
        this.queryParams.putAll(other.queryParams);
        this.fragment = other.fragment;
    }




    public static MyUriComponentsBuilder fromUriString(String uri) {
        Assert.notNull(uri, "URI must not be null");
        Matcher matcher = URI_PATTERN.matcher(uri);
        if (matcher.matches()) {
            MyUriComponentsBuilder builder = new MyUriComponentsBuilder();
            String scheme = matcher.group(2);
            String userInfo = matcher.group(5);
            String host = matcher.group(6);
            String port = matcher.group(8);
            String path = matcher.group(9);
            String query = matcher.group(11);
            String fragment = matcher.group(13);
            boolean opaque = false;
            if (StringUtils.hasLength(scheme)) {
                String rest = uri.substring(scheme.length());
                if (!rest.startsWith(":/")) {
                    opaque = true;
                }
            }
            builder.scheme(scheme);
            if (opaque) {
                String ssp = uri.substring(scheme.length()).substring(1);
                if (StringUtils.hasLength(fragment)) {
                    ssp = ssp.substring(0, ssp.length() - (fragment.length() + 1));
                }
                builder.schemeSpecificPart(ssp);
            }
            else {
                builder.userInfo(userInfo);
                builder.host(host);
                if (StringUtils.hasLength(port)) {
                    builder.port(port);
                }
                builder.path(path);
                builder.query(query);
            }
            if (StringUtils.hasText(fragment)) {
                builder.fragment(fragment);
            }
            return builder;
        }
        else {
            throw new IllegalArgumentException("[" + uri + "] is not a valid URI");
        }
    }





    // build methods


    public MyUriComponents build() {
        return build(false);
    }

    public MyUriComponents build(boolean encoded) {
        if (this.ssp != null) {
            return new MyOpaqueUriComponents(this.scheme, this.ssp, this.fragment);
        }
        else {
            return new MyHierarchicalUriComponents(this.scheme, this.fragment, this.userInfo,
                    this.host, this.port, this.pathBuilder.build(), this.queryParams, encoded, true);
        }
    }






    /**
     * Set the URI scheme. The given scheme may contain URI template variables,
     * and may also be {@code null} to clear the scheme of this builder.
     * @param scheme the URI scheme
     * @return this UriComponentsBuilder
     */
    @Override
    public MyUriComponentsBuilder scheme(@Nullable String scheme) {
        this.scheme = scheme;
        return this;
    }

    /**
     * Set the URI scheme-specific-part. When invoked, this method overwrites
     * {@linkplain #userInfo(String) user-info}, {@linkplain #host(String) host},
     * {@linkplain #port(int) port}, {@linkplain #path(String) path}, and
     * {@link #query(String) query}.
     * @param ssp the URI scheme-specific-part, may contain URI template parameters
     * @return this UriComponentsBuilder
     */
    public MyUriComponentsBuilder schemeSpecificPart(String ssp) {
        this.ssp = ssp;
        resetHierarchicalComponents();
        return this;
    }

    /**
     * Set the URI user info. The given user info may contain URI template variables,
     * and may also be {@code null} to clear the user info of this builder.
     * @param userInfo the URI user info
     * @return this UriComponentsBuilder
     */
    @Override
    public MyUriComponentsBuilder userInfo(@Nullable String userInfo) {
        this.userInfo = userInfo;
        resetSchemeSpecificPart();
        return this;
    }

    /**
     * Set the URI host. The given host may contain URI template variables,
     * and may also be {@code null} to clear the host of this builder.
     * @param host the URI host
     * @return this UriComponentsBuilder
     */
    @Override
    public MyUriComponentsBuilder host(@Nullable String host) {
        this.host = host;
        resetSchemeSpecificPart();
        return this;
    }

    @Override
    public MyUriComponentsBuilder port(int port) {
        Assert.isTrue(port >= -1, "Port must be >= -1");
        this.port = String.valueOf(port);
        resetSchemeSpecificPart();
        return this;
    }

    @Override
    public MyUriComponentsBuilder path(String path) {
        this.pathBuilder.addPath(path);
        resetSchemeSpecificPart();
        return this;
    }

    @Override
    public MyUriComponentsBuilder port(@Nullable String port) {
        this.port = port;
        resetSchemeSpecificPart();
        return this;
    }


    @Override
    public MyUriComponentsBuilder query(@Nullable String query) {
        if (query != null) {
            Matcher matcher = QUERY_PARAM_PATTERN.matcher(query);
            while (matcher.find()) {
                String name = matcher.group(1);
                String eq = matcher.group(2);
                String value = matcher.group(3);
                queryParam(name, (value != null ? value : (StringUtils.hasLength(eq) ? "" : null)));
            }
        }
        else {
            this.queryParams.clear();
        }
        resetSchemeSpecificPart();
        return this;
    }


    @Override
    public MyUriComponentsBuilder queryParam(String name, Object... values) {
        Assert.notNull(name, "Name must not be null");
        if (!ObjectUtils.isEmpty(values)) {
            for (Object value : values) {
                String valueAsString = (value != null ? value.toString() : null);
                this.queryParams.add(name, valueAsString);
            }
        }
        else {
            this.queryParams.add(name, null);
        }
        resetSchemeSpecificPart();
        return this;
    }



    @Override
    public MyUriComponentsBuilder fragment(@Nullable String fragment) {
        if (fragment != null) {
            Assert.hasLength(fragment, "Fragment must not be empty");
            this.fragment = fragment;
        }
        else {
            this.fragment = null;
        }
        return this;
    }




    private void resetHierarchicalComponents() {
        this.userInfo = null;
        this.host = null;
        this.port = null;
        this.pathBuilder = new CompositePathComponentBuilder();
        this.queryParams.clear();
    }

    private void resetSchemeSpecificPart() {
        this.ssp = null;
    }


    /**
     * Public declaration of Object's {@code clone()} method.
     * Delegates to {@link #cloneBuilder()}.
     */
    @Override
    public Object clone() {
        return cloneBuilder();
    }

    /**
     * Clone this {@code UriComponentsBuilder}.
     * @return the cloned {@code UriComponentsBuilder} object
     * @since 4.2.7
     */
    public MyUriComponentsBuilder cloneBuilder() {
        return new MyUriComponentsBuilder(this);
    }


    private interface PathComponentBuilder {

        @Nullable
        PathComponent build();

        PathComponentBuilder cloneBuilder();
    }


    private static class CompositePathComponentBuilder implements PathComponentBuilder {

        private final LinkedList<PathComponentBuilder> builders = new LinkedList<>();

        public void addPathSegments(String... pathSegments) {
            if (!ObjectUtils.isEmpty(pathSegments)) {
                PathSegmentComponentBuilder psBuilder = getLastBuilder(PathSegmentComponentBuilder.class);
                FullPathComponentBuilder fpBuilder = getLastBuilder(FullPathComponentBuilder.class);
                if (psBuilder == null) {
                    psBuilder = new PathSegmentComponentBuilder();
                    this.builders.add(psBuilder);
                    if (fpBuilder != null) {
                        fpBuilder.removeTrailingSlash();
                    }
                }
                psBuilder.append(pathSegments);
            }
        }

        public void addPath(String path) {
            if (StringUtils.hasText(path)) {
                PathSegmentComponentBuilder psBuilder = getLastBuilder(PathSegmentComponentBuilder.class);
                FullPathComponentBuilder fpBuilder = getLastBuilder(FullPathComponentBuilder.class);
                if (psBuilder != null) {
                    path = (path.startsWith("/") ? path : "/" + path);
                }
                if (fpBuilder == null) {
                    fpBuilder = new FullPathComponentBuilder();
                    this.builders.add(fpBuilder);
                }
                fpBuilder.append(path);
            }
        }

        @SuppressWarnings("unchecked")
        @Nullable
        private <T> T getLastBuilder(Class<T> builderClass) {
            if (!this.builders.isEmpty()) {
                PathComponentBuilder last = this.builders.getLast();
                if (builderClass.isInstance(last)) {
                    return (T) last;
                }
            }
            return null;
        }

        @Override
        public PathComponent build() {
            int size = this.builders.size();
            List<PathComponent> components = new ArrayList<>(size);
            for (PathComponentBuilder componentBuilder : this.builders) {
                PathComponent pathComponent = componentBuilder.build();
                if (pathComponent != null) {
                    components.add(pathComponent);
                }
            }
            if (components.isEmpty()) {
                return MyHierarchicalUriComponents.NULL_PATH_COMPONENT;
            }
            if (components.size() == 1) {
                return components.get(0);
            }
            return new MyHierarchicalUriComponents.PathComponentComposite(components);
        }

        @Override
        public CompositePathComponentBuilder cloneBuilder() {
            CompositePathComponentBuilder compositeBuilder = new CompositePathComponentBuilder();
            for (PathComponentBuilder builder : this.builders) {
                compositeBuilder.builders.add(builder.cloneBuilder());
            }
            return compositeBuilder;
        }
    }


    private static class FullPathComponentBuilder implements PathComponentBuilder {

        private final StringBuilder path = new StringBuilder();

        public void append(String path) {
            this.path.append(path);
        }

        @Override
        public PathComponent build() {
            if (this.path.length() == 0) {
                return null;
            }
            String path = this.path.toString();
            while (true) {
                int index = path.indexOf("//");
                if (index == -1) {
                    break;
                }
                path = path.substring(0, index) + path.substring(index + 1);
            }
            return new MyHierarchicalUriComponents.FullPathComponent(path);
        }

        public void removeTrailingSlash() {
            int index = this.path.length() - 1;
            if (this.path.charAt(index) == '/') {
                this.path.deleteCharAt(index);
            }
        }

        @Override
        public FullPathComponentBuilder cloneBuilder() {
            FullPathComponentBuilder builder = new FullPathComponentBuilder();
            builder.append(this.path.toString());
            return builder;
        }
    }

    @Override
    public MyUriComponentsBuilder pathSegment(String... pathSegments) throws IllegalArgumentException {
        this.pathBuilder.addPathSegments(pathSegments);
        resetSchemeSpecificPart();
        return this;
    }

    private static class PathSegmentComponentBuilder implements PathComponentBuilder {

        private final List<String> pathSegments = new LinkedList<>();

        public void append(String... pathSegments) {
            for (String pathSegment : pathSegments) {
                if (StringUtils.hasText(pathSegment)) {
                    this.pathSegments.add(pathSegment);
                }
            }
        }

        @Override
        public PathComponent build() {
            return (this.pathSegments.isEmpty() ? null :
                    new MyHierarchicalUriComponents.PathSegmentComponent(this.pathSegments));
        }

        @Override
        public PathSegmentComponentBuilder cloneBuilder() {
            PathSegmentComponentBuilder builder = new PathSegmentComponentBuilder();
            builder.pathSegments.addAll(this.pathSegments);
            return builder;
        }
    }



}
