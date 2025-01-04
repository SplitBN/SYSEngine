package dev.splityosis.sysengine.utils;

import dev.splityosis.sysengine.configlib.configuration.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    public static List<Field> getAnnotatedFields(Class<?> clazz, Class<? extends Annotation>... annotation) {
        List<Field> fields = new ArrayList<>();
        collectFieldsRecursively(clazz, fields, annotation);
        return fields;
    }

    private static void collectFieldsRecursively(Class<?> clazz, List<Field> fields, Class<? extends Annotation>... annotation) {
        if (clazz == null || clazz == Object.class)
            return;

        collectFieldsRecursively(clazz.getSuperclass(), fields, annotation);

        for (Field field : clazz.getDeclaredFields())
            for (Class<? extends Annotation> aClass : annotation)
                if (field.isAnnotationPresent(aClass)) {
                    fields.add(field);
                    break;
                }
    }
}
