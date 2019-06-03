package org.springframework.web.servlet.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.*;


import org.springframework.web.method.MyHandlerMethod;

import org.springframework.web.util.MyUrlPathHelper;


import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
public abstract class MyAbstractHandlerMethodMapping<T> extends MyAbstractHandlerMapping implements InitializingBean {


    Log logger = LogFactory.getLog(getClass());

    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";

    private boolean detectHandlerMethodsInAncestorContexts = false;


    private MyUrlPathHelper urlPathHelper = new MyUrlPathHelper();


    static {
        System.out.println("===============");
    }

    private final MappingRegistry mappingRegistry = new MappingRegistry();

    @Nullable
    private MyHandlerMethodMappingNamingStrategy<T> namingStrategy;

    protected MyHandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {

        String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);

        MyHandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, request);

        return (handlerMethod != null ? handlerMethod.createWithResolvedBean() : null);


    }

    protected abstract Set<String> getMappingPathPatterns(T mapping);

    public MyUrlPathHelper getUrlPathHelper() {
        return urlPathHelper;
    }


    @Nullable
    protected MyHandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        List<Match> matches = new ArrayList<>();
        List<T> directPathMatches = this.mappingRegistry.getMappingsByUrl(lookupPath);
        if (directPathMatches != null) {
            addMatchingMappings(directPathMatches, matches, request);
        }
        if (matches.isEmpty()) {
            // No choice but to go through all mappings...
            addMatchingMappings(this.mappingRegistry.getMappings().keySet(), matches, request);
        }
        if (!matches.isEmpty()) {
            Comparator<Match> comparator = new MatchComparator(getMappingComparator(request));
            matches.sort(comparator);
            if (logger.isTraceEnabled()) {
                logger.trace("Found " + matches.size() + " matching mapping(s) for [" + lookupPath + "] : " + matches);
            }
            Match bestMatch = matches.get(0);
//            if (matches.size() > 1) {
//                if (MyCorsUtils.isPreFlightRequest(request)) {
//                    return PREFLIGHT_AMBIGUOUS_MATCH;
//                }
//                Match secondBestMatch = matches.get(1);
//                if (comparator.compare(bestMatch, secondBestMatch) == 0) {
//                    Method m1 = bestMatch.handlerMethod.getMethod();
//                    Method m2 = secondBestMatch.handlerMethod.getMethod();
//                    throw new IllegalStateException("Ambiguous handler methods mapped for HTTP path '" +
//                            request.getRequestURL() + "': {" + m1 + ", " + m2 + "}");
//                }
//            }
//            handleMatch(bestMatch.mapping, lookupPath, request);
            return bestMatch.handlerMethod;
        }
        Match bestMatch = matches.get(0);
        return null;
    }
    private void addMatchingMappings(Collection<T> mappings, List<Match> matches, HttpServletRequest request) {
        for (T mapping : mappings) {
            T match = getMatchingMapping(mapping, request);
            if (match != null) {
                matches.add(new Match(match, this.mappingRegistry.getMappings().get(mapping)));
            }
        }
    }

    @Nullable
    protected abstract T getMatchingMapping(T mapping, HttpServletRequest request);

    protected abstract Comparator<T> getMappingComparator(HttpServletRequest request);

    class MappingRegistry {

        private final Map<T, MappingRegistration<T>> registry = new HashMap<>();

        private final Map<T, MyHandlerMethod> mappingLookup = new LinkedHashMap<>();

        private final MultiValueMap<String, T> urlLookup = new LinkedMultiValueMap<>();

        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();



        private final Map<String, List<MyHandlerMethod>> nameLookup = new ConcurrentHashMap<>();

        @Nullable
        public List<T> getMappingsByUrl(String urlPath) {
            return this.urlLookup.get(urlPath);
        }

        public void register(T mapping, Object handler, Method method) {
            this.readWriteLock.writeLock().lock();
            try {
                MyHandlerMethod handlerMethod = createHandlerMethod(handler, method);
                assertUniqueMethodMapping(handlerMethod, mapping);

                if (logger.isInfoEnabled()) {
                    logger.info("Mapped \"" + mapping + "\" onto " + handlerMethod);
                }
                this.mappingLookup.put(mapping, handlerMethod);

                List<String> directUrls = getDirectUrls(mapping);
                for (String url : directUrls) {
                    this.urlLookup.add(url, mapping);
                }

                String name = null;
                if (getNamingStrategy() != null) {
                    name = getNamingStrategy().getName(handlerMethod, mapping);
                    addMappingName(name, handlerMethod);
                }


                this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod, directUrls, name));
            }
            finally {
                this.readWriteLock.writeLock().unlock();
            }
        }


        private List<String> getDirectUrls(T mapping) {
            List<String> urls = new ArrayList<>(1);
            for (String path : getMappingPathPatterns(mapping)) {
                if (!getPathMatcher().isPattern(path)) {
                    urls.add(path);
                }
            }
            return urls;
        }


        private void assertUniqueMethodMapping(MyHandlerMethod newHandlerMethod, T mapping) {
            MyHandlerMethod handlerMethod = this.mappingLookup.get(mapping);
            if (handlerMethod != null && !handlerMethod.equals(newHandlerMethod)) {
                throw new IllegalStateException(
                        "Ambiguous mapping. Cannot map '" +	newHandlerMethod.getBean() + "' method \n" +
                                newHandlerMethod + "\nto " + mapping + ": There is already '" +
                                handlerMethod.getBean() + "' bean method\n" + handlerMethod + " mapped.");
            }
        }
        private void addMappingName(String name, MyHandlerMethod handlerMethod) {
            List<MyHandlerMethod> oldList = this.nameLookup.get(name);
            if (oldList == null) {
                oldList = Collections.emptyList();
            }

            for (MyHandlerMethod current : oldList) {
                if (handlerMethod.equals(current)) {
                    return;
                }
            }

            if (logger.isTraceEnabled()) {
                logger.trace("Mapping name '" + name + "'");
            }

            List<MyHandlerMethod> newList = new ArrayList<>(oldList.size() + 1);
            newList.addAll(oldList);
            newList.add(handlerMethod);
            this.nameLookup.put(name, newList);

            if (newList.size() > 1) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Mapping name clash for handlerMethods " + newList +
                            ". Consider assigning explicit names.");
                }
            }
        }
        public Map<T, MyHandlerMethod> getMappings() {
            return this.mappingLookup;
        }

    }

    /***
     * 类结束=====================================   ======   ==== =  = = =  = =
     */
    private class Match {

        private final T mapping;

        private final MyHandlerMethod handlerMethod;

        public Match(T mapping, MyHandlerMethod handlerMethod) {
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
        }

        @Override
        public String toString() {
            return this.mapping.toString();
        }
    }

    private class MatchComparator implements Comparator<Match> {

        private final Comparator<T> comparator;

        public MatchComparator(Comparator<T> comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(Match match1, Match match2) {
            return this.comparator.compare(match1.mapping, match2.mapping);
        }
    }

    @Override
    public void afterPropertiesSet() {
        initHandlerMethods();
    }

    protected void initHandlerMethods() {
        if (logger.isDebugEnabled()) {
            logger.debug("Looking for request mappings in application context: " + getApplicationContext());
        }
        // helloController
        String[] beanNames = (this.detectHandlerMethodsInAncestorContexts ?
                BeanFactoryUtils.beanNamesForTypeIncludingAncestors(obtainApplicationContext(), Object.class) :
                obtainApplicationContext().getBeanNamesForType(Object.class));

        for (String beanName : beanNames) {
            if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
                Class<?> beanType = null;
                try {
                    beanType = obtainApplicationContext().getType(beanName);
                }
                catch (Throwable ex) {
                    // An unresolvable bean type, probably from a lazy bean - let's ignore it.
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
                    }
                }
                if (beanType != null && isHandler(beanType)) {
                    detectHandlerMethods(beanName);
                }
            }
        }
//        handlerMethodsInitialized(getHandlerMethods());
    }

    protected abstract boolean isHandler(Class<?> beanType);


    protected abstract T getMappingForMethod(Method method, Class<?> handlerType);


    protected void detectHandlerMethods(final Object handler) {
        Class<?> handlerType = (handler instanceof String ?
                obtainApplicationContext().getType((String) handler) : handler.getClass());

        if (handlerType != null) {
            final Class<?> userType = ClassUtils.getUserClass(handlerType);
            Map<Method, T> methods = MethodIntrospector.selectMethods(userType,
                    (MethodIntrospector.MetadataLookup<T>) method -> {
                        try {
                            return getMappingForMethod(method, userType);
                        }
                        catch (Throwable ex) {
                            throw new IllegalStateException("Invalid mapping on handler class [" +
                                    userType.getName() + "]: " + method, ex);
                        }
                    });
            if (logger.isDebugEnabled()) {
                logger.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
            }
            methods.forEach((method, mapping) -> {
                Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
                registerHandlerMethod(handler, invocableMethod, mapping); //mapping为RequestMappingInfo
            });
        }
    }


    protected void registerHandlerMethod(Object handler, Method method, T mapping) {
        this.mappingRegistry.register(mapping, handler, method);
        System.out.println("ssss");
    }
    protected MyHandlerMethod createHandlerMethod(Object handler, Method method) {
        MyHandlerMethod handlerMethod;
        if (handler instanceof String) {
            String beanName = (String) handler;
            handlerMethod = new MyHandlerMethod(beanName,
                    obtainApplicationContext().getAutowireCapableBeanFactory(), method);
        }
        else {
            handlerMethod = new MyHandlerMethod(handler, method);
        }
        return handlerMethod;
    }



    @Nullable
    public MyHandlerMethodMappingNamingStrategy<T> getNamingStrategy() {
        return this.namingStrategy;
    }



    private static class MappingRegistration<T> {

        private final T mapping;

        private final MyHandlerMethod handlerMethod;

        private final List<String> directUrls;

        @Nullable
        private final String mappingName;

        public MappingRegistration(T mapping, MyHandlerMethod handlerMethod,
                                   @Nullable List<String> directUrls, @Nullable String mappingName) {

            Assert.notNull(mapping, "Mapping must not be null");
            Assert.notNull(handlerMethod, "HandlerMethod must not be null");
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
            this.directUrls = (directUrls != null ? directUrls : Collections.emptyList());
            this.mappingName = mappingName;
        }

        public T getMapping() {
            return this.mapping;
        }

        public MyHandlerMethod getHandlerMethod() {
            return this.handlerMethod;
        }

        public List<String> getDirectUrls() {
            return this.directUrls;
        }

        @Nullable
        public String getMappingName() {
            return this.mappingName;
        }
    }


}
