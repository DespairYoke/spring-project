package org.springframework.http;

import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.*;


/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-27
 **/
public class MyMediaType extends MimeType implements Serializable {

    public static final String ALL_VALUE = "*/*";

    public static final MyMediaType ALL;

    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

    private static final String PARAM_QUALITY_FACTOR = "q";

    public static final MyMediaType APPLICATION_OCTET_STREAM;

    static {
        ALL = valueOf(ALL_VALUE);
        APPLICATION_OCTET_STREAM = valueOf(APPLICATION_OCTET_STREAM_VALUE);
    }

    public MyMediaType(String type, String subtype, @Nullable Map<String, String> parameters) {
        super(type, subtype, parameters);
    }

    public static MyMediaType valueOf(String value) {
        return parseMediaType(value);
    }

    public static MyMediaType parseMediaType(String mediaType) {
        MimeType type;
        try {
            type = MimeTypeUtils.parseMimeType(mediaType);
        }
        catch (InvalidMimeTypeException ex) {
            throw new MyInvalidMediaTypeException(ex);
        }
        try {
            return new MyMediaType(type.getType(), type.getSubtype(), type.getParameters());
        }
        catch (IllegalArgumentException ex) {
            throw new MyInvalidMediaTypeException(mediaType, ex.getMessage());
        }
    }


    public static List<MyMediaType> parseMediaTypes(@Nullable String mediaTypes) {
        if (!StringUtils.hasLength(mediaTypes)) {
            return Collections.emptyList();
        }
        String[] tokens = StringUtils.tokenizeToStringArray(mediaTypes, ",");
        List<MyMediaType> result = new ArrayList<>(tokens.length);
        for (String token : tokens) {
            result.add(parseMediaType(token));
        }
        return result;
    }

    public static String toString(Collection<MyMediaType> mediaTypes) {
        return MimeTypeUtils.toString(mediaTypes);
    }

    public static List<MyMediaType> parseMediaTypes(@Nullable List<String> mediaTypes) {
        if (CollectionUtils.isEmpty(mediaTypes)) {
            return Collections.emptyList();
        }
        else if (mediaTypes.size() == 1) {
            return parseMediaTypes(mediaTypes.get(0));
        }
        else {
            List<MyMediaType> result = new ArrayList<>(8);
            for (String mediaType : mediaTypes) {
                result.addAll(parseMediaTypes(mediaType));
            }
            return result;
        }
    }

    public static final Comparator<MyMediaType> SPECIFICITY_COMPARATOR = new SpecificityComparator<MyMediaType>() {

        @Override
        protected int compareParameters(MyMediaType mediaType1, MyMediaType mediaType2) {
            double quality1 = mediaType1.getQualityValue();
            double quality2 = mediaType2.getQualityValue();
            int qualityComparison = Double.compare(quality2, quality1);
            if (qualityComparison != 0) {
                return qualityComparison;  // audio/*;q=0.7 < audio/*;q=0.3
            }
            return super.compareParameters(mediaType1, mediaType2);
        }
    };
    public double getQualityValue() {
        String qualityFactor = getParameter(PARAM_QUALITY_FACTOR);
        return (qualityFactor != null ? Double.parseDouble(unquote(qualityFactor)) : 1D);
    }
}
