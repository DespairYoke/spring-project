package org.springframework.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.beans.PropertyEditor;
import java.lang.reflect.Field;

public class MyDataBinder implements PropertyEditorRegistry, TypeConverter{


    private ConversionService conversionService;

    protected static final Log logger = LogFactory.getLog(MyDataBinder.class);

    private final Object target;

    private final String objectName;

    public static final String DEFAULT_OBJECT_NAME = "target";

    public MyDataBinder(@Nullable Object target) {
        this(target, DEFAULT_OBJECT_NAME);
    }

    public MyDataBinder(@Nullable Object target, String objectName) {
        this.target = ObjectUtils.unwrapOptional(target);
        this.objectName = objectName;
    }

    public ConversionService getConversionService() {
        return this.conversionService;
    }

    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType) throws TypeMismatchException {
        return getTypeConverter().convertIfNecessary(value, requiredType);
    }

    @Override
    @Nullable
    public <T> T convertIfNecessary(@Nullable Object value, @Nullable Class<T> requiredType,
                                    @Nullable MethodParameter methodParam) throws TypeMismatchException {

        return getTypeConverter().convertIfNecessary(value, requiredType, methodParam);
    }

    @Override
    public <T> T convertIfNecessary(Object o, Class<T> aClass, Field field) throws TypeMismatchException {
        return null;
    }

    protected TypeConverter getTypeConverter() {
        if (getTarget() != null) {
//            return getInternalBindingResult().getPropertyAccessor();
            return null;
        }
        else {
            return null;
        }
    }

    public Object getTarget() {
        return this.target;
    }


    @Override
    public void registerCustomEditor(Class<?> aClass, PropertyEditor propertyEditor) {

    }

    @Override
    public void registerCustomEditor(Class<?> aClass, String s, PropertyEditor propertyEditor) {

    }

    @Override
    public PropertyEditor findCustomEditor(Class<?> aClass, String s) {
        return null;
    }
}
