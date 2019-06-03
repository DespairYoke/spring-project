package org.springframework.web.servlet.mvc.condition;


import org.springframework.http.MyInvalidMediaTypeException;
import org.springframework.http.MyMediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.MyCorsUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
public class MyConsumesRequestCondition extends MyAbstractRequestCondition<MyConsumesRequestCondition> {

    private static final MyConsumesRequestCondition PRE_FLIGHT_MATCH = new MyConsumesRequestCondition();

    private final List<ConsumeMediaTypeExpression> expressions;

    public MyConsumesRequestCondition(String... consumes) {
        this(consumes, null);
    }

    public MyConsumesRequestCondition(String[] consumes, @Nullable String[] headers) {
        this(parseExpressions(consumes, headers));
    }


    private static Set<ConsumeMediaTypeExpression> parseExpressions(String[] consumes, @Nullable String[] headers) {
        Set<ConsumeMediaTypeExpression> result = new LinkedHashSet<>();
        if (headers != null) {
            for (String header : headers) {
                HeaderExpression expr = new HeaderExpression(header);
                if ("Content-Type".equalsIgnoreCase(expr.name) && expr.value != null) {
                    for (MyMediaType mediaType : MyMediaType.parseMediaTypes(expr.value)) {
                        result.add(new ConsumeMediaTypeExpression(mediaType, expr.isNegated));
                    }
                }
            }
        }
        for (String consume : consumes) {
            result.add(new ConsumeMediaTypeExpression(consume));
        }
        return result;
    }
    /**
     * Private constructor accepting parsed media type expressions.
     */
    private MyConsumesRequestCondition(Collection<ConsumeMediaTypeExpression> expressions) {
        this.expressions = new ArrayList<>(expressions);
        Collections.sort(this.expressions);
    }


    @Override
    public MyConsumesRequestCondition combine(MyConsumesRequestCondition other) {
        return !other.expressions.isEmpty() ? other : this;
    }

    @Override
    public boolean isEmpty() {
        return this.expressions.isEmpty();
    }

    @Override
    protected Collection<ConsumeMediaTypeExpression> getContent() {
        return this.expressions;
    }


    @Override
    @Nullable
    public MyConsumesRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (MyCorsUtils.isPreFlightRequest(request)) {
            return PRE_FLIGHT_MATCH;
        }
        if (isEmpty()) {
            return this;
        }

        MyMediaType contentType;
        try {
            contentType = (StringUtils.hasLength(request.getContentType()) ?
                    MyMediaType.parseMediaType(request.getContentType()) :
                    MyMediaType.APPLICATION_OCTET_STREAM);
        }
        catch (MyInvalidMediaTypeException ex) {
            return null;
        }

        Set<ConsumeMediaTypeExpression> result = new LinkedHashSet<>(this.expressions);
        result.removeIf(expression -> !expression.match(contentType));
        return (!result.isEmpty() ? new MyConsumesRequestCondition(result) : null);
    }

    @Override
    public int compareTo(MyConsumesRequestCondition other, HttpServletRequest request) {
        return 0;
    }

    static class ConsumeMediaTypeExpression extends MyAbstractMediaTypeExpression {

        ConsumeMediaTypeExpression(String expression) {
            super(expression);
        }

        ConsumeMediaTypeExpression(MyMediaType mediaType, boolean negated) {
            super(mediaType, negated);
        }

        public final boolean match(MyMediaType contentType) {
            boolean match = getMediaType().includes(contentType);
            return (!isNegated() ? match : !match);
        }

        @Override
        public int compareTo(MyAbstractMediaTypeExpression other) {
            return MyMediaType.SPECIFICITY_COMPARATOR.compare(this.getMediaType(), other.getMediaType());
        }

    }

    static class HeaderExpression extends MyAbstractNameValueExpression<String> {

        public HeaderExpression(String expression) {
            super(expression);
        }

        @Override
        protected boolean isCaseSensitiveName() {
            return false;
        }

        @Override
        protected String parseValue(String valueExpression) {
            return valueExpression;
        }

        @Override
        protected boolean matchName(HttpServletRequest request) {
            return (request.getHeader(this.name) != null);
        }

        @Override
        protected boolean matchValue(HttpServletRequest request) {
            return ObjectUtils.nullSafeEquals(this.value, request.getHeader(this.name));
        }
    }
}