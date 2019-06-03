package org.springframework.web.servlet.mvc.condition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MyMediaType;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-25
 **/
abstract class MyAbstractMediaTypeExpression implements MyMediaTypeExpression, Comparable<MyAbstractMediaTypeExpression>{

    protected final Log logger = LogFactory.getLog(getClass());

    private final MyMediaType mediaType;

    private final boolean isNegated;

    MyAbstractMediaTypeExpression(String expression) {
        if (expression.startsWith("!")) {
            this.isNegated = true;
            expression = expression.substring(1);
        }
        else {
            this.isNegated = false;
        }
        this.mediaType = MyMediaType.parseMediaType(expression);
    }

    MyAbstractMediaTypeExpression(MyMediaType mediaType, boolean negated) {
        this.mediaType = mediaType;
        this.isNegated = negated;
    }

    @Override
    public MyMediaType getMediaType() {
        return this.mediaType;
    }

    @Override
    public boolean isNegated() {
        return this.isNegated;
    }

    @Override
    public int compareTo(MyAbstractMediaTypeExpression other) {
        return MyMediaType.SPECIFICITY_COMPARATOR.compare(this.getMediaType(), other.getMediaType());
    }
}
