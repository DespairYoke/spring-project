package org.springframework.web.util;

import org.springframework.lang.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-30
 **/
final class MyHierarchicalUriComponents extends MyUriComponents{

    private static final char PATH_DELIMITER = '/';

    private static final String PATH_DELIMITER_STRING = "/";


    @Nullable
    private final String userInfo;

    @Nullable
    private final String host;

    @Nullable
    private final String port;

    private final PathComponent path;

    private final MultiValueMap<String, String> queryParams;

    private final boolean encoded;

    MyHierarchicalUriComponents(@Nullable String scheme, @Nullable String fragment, @Nullable String userInfo,
                              @Nullable String host, @Nullable String port, @Nullable PathComponent path,
                              @Nullable MultiValueMap<String, String> queryParams, boolean encoded, boolean verify) {

        super(scheme, fragment);

        this.userInfo = userInfo;
        this.host = host;
        this.port = port;
        this.path = (path != null ? path : NULL_PATH_COMPONENT);
        this.queryParams = CollectionUtils.unmodifiableMultiValueMap(
                queryParams != null ? queryParams : new LinkedMultiValueMap<>(0));
        this.encoded = encoded;

        if (verify) {
            verify();
        }
    }

    private void verify() {
        if (!this.encoded) {
            return;
        }
        verifyUriComponent(getScheme(), Type.SCHEME);
        verifyUriComponent(this.userInfo, Type.USER_INFO);
        verifyUriComponent(this.host, getHostType());
        this.path.verify();
        this.queryParams.forEach((key, values) -> {
            verifyUriComponent(key, Type.QUERY_PARAM);
            for (String value : values) {
                verifyUriComponent(value, Type.QUERY_PARAM);
            }
        });
        verifyUriComponent(getFragment(), Type.FRAGMENT);
    }
    private Type getHostType() {
        return (this.host != null && this.host.startsWith("[") ? Type.HOST_IPV6 : Type.HOST_IPV4);
    }

    private static void verifyUriComponent(@Nullable String source, Type type) {
        if (source == null) {
            return;
        }
        int length = source.length();
        for (int i = 0; i < length; i++) {
            char ch = source.charAt(i);
            if (ch == '%') {
                if ((i + 2) < length) {
                    char hex1 = source.charAt(i + 1);
                    char hex2 = source.charAt(i + 2);
                    int u = Character.digit(hex1, 16);
                    int l = Character.digit(hex2, 16);
                    if (u == -1 || l == -1) {
                        throw new IllegalArgumentException("Invalid encoded sequence \"" +
                                source.substring(i) + "\"");
                    }
                    i += 2;
                }
                else {
                    throw new IllegalArgumentException("Invalid encoded sequence \"" +
                            source.substring(i) + "\"");
                }
            }
            else if (!type.isAllowed(ch)) {
                throw new IllegalArgumentException("Invalid character '" + ch + "' for " +
                        type.name() + " in \"" + source + "\"");
            }
        }
    }


    static final PathComponent NULL_PATH_COMPONENT = new PathComponent() {
        @Override
        public String getPath() {
            return "";
        }
        @Override
        public List<String> getPathSegments() {
            return Collections.emptyList();
        }
        @Override
        public PathComponent encode(Charset charset) {
            return this;
        }
        @Override
        public void verify() {
        }
        @Override
        public PathComponent expand(UriTemplateVariables uriVariables) {
            return this;
        }
        @Override
        public void copyToUriComponentsBuilder(MyUriComponentsBuilder builder) {
        }
        @Override
        public boolean equals(Object obj) {
            return (this == obj);
        }
        @Override
        public int hashCode() {
            return getClass().hashCode();
        }
    };

    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getHost() {
        return null;
    }

    interface PathComponent extends Serializable {

        String getPath();

        List<String> getPathSegments();

        PathComponent encode(Charset charset);

        void verify();

        PathComponent expand(UriTemplateVariables uriVariables);

        void copyToUriComponentsBuilder(MyUriComponentsBuilder builder);
    }

    enum Type {

        SCHEME {
            @Override
            public boolean isAllowed(int c) {
                return isAlpha(c) || isDigit(c) || '+' == c || '-' == c || '.' == c;
            }
        },
        AUTHORITY {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c) || ':' == c || '@' == c;
            }
        },
        USER_INFO {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c) || ':' == c;
            }
        },
        HOST_IPV4 {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c);
            }
        },
        HOST_IPV6 {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c) || isSubDelimiter(c) || '[' == c || ']' == c || ':' == c;
            }
        },
        PORT {
            @Override
            public boolean isAllowed(int c) {
                return isDigit(c);
            }
        },
        PATH {
            @Override
            public boolean isAllowed(int c) {
                return isPchar(c) || '/' == c;
            }
        },
        PATH_SEGMENT {
            @Override
            public boolean isAllowed(int c) {
                return isPchar(c);
            }
        },
        QUERY {
            @Override
            public boolean isAllowed(int c) {
                return isPchar(c) || '/' == c || '?' == c;
            }
        },
        QUERY_PARAM {
            @Override
            public boolean isAllowed(int c) {
                if ('=' == c || '&' == c) {
                    return false;
                }
                else {
                    return isPchar(c) || '/' == c || '?' == c;
                }
            }
        },
        FRAGMENT {
            @Override
            public boolean isAllowed(int c) {
                return isPchar(c) || '/' == c || '?' == c;
            }
        },
        URI {
            @Override
            public boolean isAllowed(int c) {
                return isUnreserved(c);
            }
        };

        /**
         * Indicates whether the given character is allowed in this URI component.
         * @return {@code true} if the character is allowed; {@code false} otherwise
         */
        public abstract boolean isAllowed(int c);

        /**
         * Indicates whether the given character is in the {@code ALPHA} set.
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isAlpha(int c) {
            return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z');
        }

        /**
         * Indicates whether the given character is in the {@code DIGIT} set.
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isDigit(int c) {
            return (c >= '0' && c <= '9');
        }

        /**
         * Indicates whether the given character is in the {@code gen-delims} set.
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isGenericDelimiter(int c) {
            return (':' == c || '/' == c || '?' == c || '#' == c || '[' == c || ']' == c || '@' == c);
        }

        /**
         * Indicates whether the given character is in the {@code sub-delims} set.
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isSubDelimiter(int c) {
            return ('!' == c || '$' == c || '&' == c || '\'' == c || '(' == c || ')' == c || '*' == c || '+' == c ||
                    ',' == c || ';' == c || '=' == c);
        }

        /**
         * Indicates whether the given character is in the {@code reserved} set.
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isReserved(int c) {
            return (isGenericDelimiter(c) || isSubDelimiter(c));
        }

        /**
         * Indicates whether the given character is in the {@code unreserved} set.
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isUnreserved(int c) {
            return (isAlpha(c) || isDigit(c) || '-' == c || '.' == c || '_' == c || '~' == c);
        }

        /**
         * Indicates whether the given character is in the {@code pchar} set.
         * @see <a href="http://www.ietf.org/rfc/rfc3986.txt">RFC 3986, appendix A</a>
         */
        protected boolean isPchar(int c) {
            return (isUnreserved(c) || isSubDelimiter(c) || ':' == c || '@' == c);
        }
    }

    static final class PathComponentComposite implements PathComponent {

        private final List<PathComponent> pathComponents;

        public PathComponentComposite(List<PathComponent> pathComponents) {
            Assert.notNull(pathComponents, "PathComponent List must not be null");
            this.pathComponents = pathComponents;
        }

        @Override
        public String getPath() {
            StringBuilder pathBuilder = new StringBuilder();
            for (PathComponent pathComponent : this.pathComponents) {
                pathBuilder.append(pathComponent.getPath());
            }
            return pathBuilder.toString();
        }

        @Override
        public List<String> getPathSegments() {
            List<String> result = new ArrayList<>();
            for (PathComponent pathComponent : this.pathComponents) {
                result.addAll(pathComponent.getPathSegments());
            }
            return result;
        }

        @Override
        public PathComponent encode(Charset charset) {
            List<PathComponent> encodedComponents = new ArrayList<>(this.pathComponents.size());
            for (PathComponent pathComponent : this.pathComponents) {
                encodedComponents.add(pathComponent.encode(charset));
            }
            return new PathComponentComposite(encodedComponents);
        }

        @Override
        public void verify() {
            for (PathComponent pathComponent : this.pathComponents) {
                pathComponent.verify();
            }
        }

        @Override
        public PathComponent expand(UriTemplateVariables uriVariables) {
            List<PathComponent> expandedComponents = new ArrayList<>(this.pathComponents.size());
            for (PathComponent pathComponent : this.pathComponents) {
                expandedComponents.add(pathComponent.expand(uriVariables));
            }
            return new PathComponentComposite(expandedComponents);
        }

        @Override
        public void copyToUriComponentsBuilder(MyUriComponentsBuilder builder) {
            for (PathComponent pathComponent : this.pathComponents) {
                pathComponent.copyToUriComponentsBuilder(builder);
            }
        }
    }
    static final class FullPathComponent implements PathComponent {

        private final String path;

        public FullPathComponent(@Nullable String path) {
            this.path = (path != null ? path : "");
        }

        @Override
        public String getPath() {
            return this.path;
        }

        @Override
        public List<String> getPathSegments() {
            String[] segments = StringUtils.tokenizeToStringArray(getPath(), PATH_DELIMITER_STRING);
            return Collections.unmodifiableList(Arrays.asList(segments));
        }

        @Override
        public PathComponent encode(Charset charset) {
            String encodedPath = encodeUriComponent(getPath(), charset, Type.PATH);
            return new FullPathComponent(encodedPath);
        }

        @Override
        public void verify() {
            verifyUriComponent(getPath(), Type.PATH);
        }

        @Override
        public PathComponent expand(UriTemplateVariables uriVariables) {
            String expandedPath = expandUriComponent(getPath(), uriVariables);
            return new FullPathComponent(expandedPath);
        }

        @Override
        public void copyToUriComponentsBuilder(MyUriComponentsBuilder builder) {

        }


        @Override
        public boolean equals(Object obj) {
            return (this == obj || (obj instanceof FullPathComponent &&
                    getPath().equals(((FullPathComponent) obj).getPath())));
        }

        @Override
        public int hashCode() {
            return getPath().hashCode();
        }
    }
    static String encodeUriComponent(String source, String encoding, Type type) {
        return encodeUriComponent(source, Charset.forName(encoding), type);
    }


    static String encodeUriComponent(String source, Charset charset, Type type) {
        if (!StringUtils.hasLength(source)) {
            return source;
        }
        Assert.notNull(charset, "Charset must not be null");
        Assert.notNull(type, "Type must not be null");

        byte[] bytes = source.getBytes(charset);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
        boolean changed = false;
        for (byte b : bytes) {
            if (b < 0) {
                b += 256;
            }
            if (type.isAllowed(b)) {
                bos.write(b);
            }
            else {
                bos.write('%');
                char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
                char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
                bos.write(hex1);
                bos.write(hex2);
                changed = true;
            }
        }
        return (changed ? new String(bos.toByteArray(), charset) : source);
    }

    static final class PathSegmentComponent implements PathComponent {

        private final List<String> pathSegments;

        public PathSegmentComponent(List<String> pathSegments) {
            Assert.notNull(pathSegments, "List must not be null");
            this.pathSegments = Collections.unmodifiableList(new ArrayList<>(pathSegments));
        }

        @Override
        public String getPath() {
            StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append(PATH_DELIMITER);
            for (Iterator<String> iterator = this.pathSegments.iterator(); iterator.hasNext(); ) {
                String pathSegment = iterator.next();
                pathBuilder.append(pathSegment);
                if (iterator.hasNext()) {
                    pathBuilder.append(PATH_DELIMITER);
                }
            }
            return pathBuilder.toString();
        }

        @Override
        public List<String> getPathSegments() {
            return this.pathSegments;
        }

        @Override
        public PathComponent encode(Charset charset) {
            List<String> pathSegments = getPathSegments();
            List<String> encodedPathSegments = new ArrayList<>(pathSegments.size());
            for (String pathSegment : pathSegments) {
                String encodedPathSegment = encodeUriComponent(pathSegment, charset, Type.PATH_SEGMENT);
                encodedPathSegments.add(encodedPathSegment);
            }
            return new PathSegmentComponent(encodedPathSegments);
        }

        @Override
        public void verify() {
            for (String pathSegment : getPathSegments()) {
                verifyUriComponent(pathSegment, Type.PATH_SEGMENT);
            }
        }

        @Override
        public PathComponent expand(UriTemplateVariables uriVariables) {
            List<String> pathSegments = getPathSegments();
            List<String> expandedPathSegments = new ArrayList<>(pathSegments.size());
            for (String pathSegment : pathSegments) {
                String expandedPathSegment = expandUriComponent(pathSegment, uriVariables);
                expandedPathSegments.add(expandedPathSegment);
            }
            return new PathSegmentComponent(expandedPathSegments);
        }

        @Override
        public void copyToUriComponentsBuilder(MyUriComponentsBuilder builder) {
            builder.pathSegment(StringUtils.toStringArray(getPathSegments()));
        }

        @Override
        public boolean equals(Object obj) {
            return (this == obj || (obj instanceof PathSegmentComponent &&
                    getPathSegments().equals(((PathSegmentComponent) obj).getPathSegments())));
        }

        @Override
        public int hashCode() {
            return getPathSegments().hashCode();
        }
    }
}
