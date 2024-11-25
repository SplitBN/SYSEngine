package dev.splityosis.sysengine.configlib.manager;

import com.cryptomorin.xseries.XItemStack;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.utils.ReflectionUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * The MapperRegistry class serves as a registry for storing and retrieving instances of AbstractMapper implementations.
 * It supports identifying mappers by both their class and an optional string identifier.
 * When the identifier is an empty string (""), that mapper is treated as the main or default mapper for that class.
 */
public class MapperRegistry {

    /**
     * Map that holds registered mappers. The outer map uses the target class type as the key, and the inner map
     * associates string identifiers with specific AbstractMapper instances of that type.
     * The identifier "" is treated as the main or default mapper for that type.
     */
    private final Map<Class<?>, Map<String, AbstractMapper<?>>> mappersMap = new HashMap<>();

    /**
     * Registers an AbstractMapper instance with a specified identifier.
     *
     * @param mapper            the mapper instance to register
     * @param mapperIdentifier  an optional identifier for the mapper; if null, an empty string ("") is used as the identifier,
     *                          making this mapper the main or default mapper for this type.
     */
    public void registerMapper(AbstractMapper<?> mapper, String mapperIdentifier) {
        Class<?> claz = getMapperGenericType(mapper.getClass());
        if (claz == null)
            throw new RuntimeException("Mapper is not if any generic type");

        Map<String, AbstractMapper<?>> mappers = mappersMap.get(claz);
        if (mappers == null) {
            mappers = new HashMap<>();
        }

        mapperIdentifier = (mapperIdentifier == null) ? "" : mapperIdentifier.trim();

        mappers.put(mapperIdentifier.toLowerCase(), mapper);
        mappersMap.put(claz, mappers);
    }

    /**
     * Registers an AbstractMapper instance with a default identifier (empty string).
     * This will treat the mapper as the main or default mapper for this class type.
     *
     * @param mapper  the mapper instance to register
     */
    public void registerMapper(AbstractMapper<?> mapper) {
        registerMapper(mapper, null);
    }

    /**
     * Retrieves a registered AbstractMapper instance by its class and an optional identifier.
     *
     * @param clazz             the target class of the mapper
     * @param mapperIdentifier  an optional identifier for the mapper; if null, an empty string ("") is used as the identifier,
     *                          which corresponds to the main or default mapper for that class.
     * @param <T>               the type of the target class
     * @return                  the mapper instance if found; otherwise, null
     */
    @SuppressWarnings("unchecked")
    public <T> AbstractMapper<T> getMapper(Class<T> clazz, String mapperIdentifier) {
        Map<String, AbstractMapper<?>> mappers = mappersMap.get(clazz);
        if (mappers == null) {
            return null;
        }

        mapperIdentifier = (mapperIdentifier == null) ? "" : mapperIdentifier.trim();

        return (AbstractMapper<T>) mappers.get(mapperIdentifier.toLowerCase());
    }

    /**
     * Retrieves a registered AbstractMapper instance by its class using the default identifier (empty string).
     * This will retrieve the main or default mapper for the specified class.
     *
     * @param clazz  the target class of the mapper
     * @param <T>    the type of the target class
     * @return       the mapper instance if found; otherwise, null
     */
    public <T> AbstractMapper<T> getMapper(Class<T> clazz) {
        return getMapper(clazz, null);
    }

    /**
     * Retrieves the map of all registered mappers for all types and identifiers.
     *
     * @return a map where the keys are classes and the values are maps of identifiers to mappers.
     */
    public Map<Class<?>, Map<String, AbstractMapper<?>>> getMappersMap() {
        return mappersMap;
    }

    public static <T> Class<T> getMapperGenericType(Class<?> clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;

                if (parameterizedType.getRawType() == AbstractMapper.class) {
                    return (Class<T>) parameterizedType.getActualTypeArguments()[0];
                }
            }
        }
        return null;
    }
}
