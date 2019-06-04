//package org.springframework.web.context;
//
//import org.springframework.lang.Nullable;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * TODO...
// *
// * @author zwd
// * @since 2019-05-24
// **/
//public class  MyContextLoader {
//
//    private static final Map<ClassLoader, MyWebApplicationContext> currentContextPerThread =
//            new ConcurrentHashMap<>(1);
//
//    /**
//     * The 'current' WebApplicationContext, if the ContextLoader class is
//     * deployed in the web app ClassLoader itself.
//     */
//    @Nullable
//    private static volatile MyWebApplicationContext currentContext;
//
//
//    public static final String GLOBAL_INITIALIZER_CLASSES_PARAM = "globalInitializerClasses";
//
//    public static MyWebApplicationContext getCurrentWebApplicationContext() {
//        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
//        if (ccl != null) {
//            MyWebApplicationContext ccpt = currentContextPerThread.get(ccl);
//            if (ccpt != null) {
//                return ccpt;
//            }
//        }
//        return currentContext;
//    }
//
//}
