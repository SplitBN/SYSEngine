package dev.splityosis.sysengine.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ReflectionUtil {

    public static Class<?>[] getGenericTypes(Field field) {
        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            Class<?>[] genericClasses = new Class<?>[typeArguments.length];
            for (int i = 0; i < typeArguments.length; i++) {
                if (typeArguments[i] instanceof Class<?>) {
                    genericClasses[i] = (Class<?>) typeArguments[i];
                } else {
                    genericClasses[i] = Object.class;
                }
            }
            return genericClasses;
        }
        return new Class<?>[0];
    }
}
