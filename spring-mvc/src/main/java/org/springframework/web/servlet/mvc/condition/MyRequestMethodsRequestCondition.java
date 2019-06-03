package org.springframework.web.servlet.mvc.condition;


import org.springframework.http.MyHttpHeaders;
import org.springframework.http.MyHttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.MyRequestMethod;

import org.springframework.web.cors.MyCorsUtils;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
public class MyRequestMethodsRequestCondition  extends MyAbstractRequestCondition<MyRequestMethodsRequestCondition>{

    private static final MyRequestMethodsRequestCondition GET_CONDITION =
            new MyRequestMethodsRequestCondition(MyRequestMethod.GET);

    private final Set<MyRequestMethod> methods;

    public MyRequestMethodsRequestCondition(MyRequestMethod... requestMethods) {
        this(Arrays.asList(requestMethods));
    }

    private MyRequestMethodsRequestCondition(Collection<MyRequestMethod> requestMethods) {
        this.methods = Collections.unmodifiableSet(new LinkedHashSet<>(requestMethods));
    }

    @Override
    protected Collection<MyRequestMethod> getContent() {
        return this.methods;
    }
    @Override
    public MyRequestMethodsRequestCondition combine(MyRequestMethodsRequestCondition other) {
        Set<MyRequestMethod> set = new LinkedHashSet<>(this.methods);
        set.addAll(other.methods);
        return new MyRequestMethodsRequestCondition(set);
    }

    @Override
    public MyRequestMethodsRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (MyCorsUtils.isPreFlightRequest(request)) {
            return matchPreFlight(request);
        }

        if (getMethods().isEmpty()) {
            if (MyRequestMethod.OPTIONS.name().equals(request.getMethod()) &&
                    !DispatcherType.ERROR.equals(request.getDispatcherType())) {

                return null; // No implicit match for OPTIONS (we handle it)
            }
            return this;
        }

        return matchRequestMethod(request.getMethod());
    }

    @Nullable
    private MyRequestMethodsRequestCondition matchPreFlight(HttpServletRequest request) {
        if (getMethods().isEmpty()) {
            return this;
        }
        String expectedMethod = request.getHeader(MyHttpHeaders.ACCESS_CONTROL_REQUEST_METHOD);
        return matchRequestMethod(expectedMethod);
    }

    @Nullable
    private MyRequestMethodsRequestCondition matchRequestMethod(String httpMethodValue) {
        MyHttpMethod httpMethod = MyHttpMethod.resolve(httpMethodValue);
        if (httpMethod != null) {
            for (MyRequestMethod method : getMethods()) {
                if (httpMethod.matches(method.name())) {
                    return new MyRequestMethodsRequestCondition(method);
                }
            }
            if (httpMethod == MyHttpMethod.HEAD && getMethods().contains(MyRequestMethod.GET)) {
                return GET_CONDITION;
            }
        }
        return null;
    }

    @Override
    public int compareTo(MyRequestMethodsRequestCondition other, HttpServletRequest request) {
        return 0;
    }
    public Set<MyRequestMethod> getMethods() {
        return this.methods;
    }
}
