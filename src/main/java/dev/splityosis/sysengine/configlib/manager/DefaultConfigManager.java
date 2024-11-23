package dev.splityosis.sysengine.configlib.manager;

import dev.splityosis.sysengine.configlib.exceptions.ConfigNotRegisteredException;
import dev.splityosis.sysengine.configlib.ConfigLib;
import dev.splityosis.sysengine.configlib.configuration.YMLProfile;
import dev.splityosis.sysengine.configlib.configuration.Configuration;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.utils.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class DefaultConfigManager implements ConfigManager {

    private ConfigOptions configOptions;
    private Map<Configuration, File> registeredConfigs;

    public DefaultConfigManager() {
        configOptions = new ConfigOptions();
        registeredConfigs = new HashMap<>();
    }

    @Override
    public void registerConfig(Configuration configuration, File file) throws Exception {
        if (!file.exists())
            writeToFile(file, configuration);

        registeredConfigs.put(configuration, file);

        writeToFields(configuration, readFile(file));
    }

    @Override
    public void unregisterConfig(Configuration configuration) {
        if (registeredConfigs.remove(configuration) == null)
            throw new ConfigNotRegisteredException(configuration);
    }

    @Override
    public void reload(Configuration configuration) throws Exception {
        File file = registeredConfigs.get(configuration);
        if (file == null)
            throw new ConfigNotRegisteredException(configuration);

        writeToFields(configuration, readFile(file));
    }

    @Override
    public void save(Configuration configuration) throws Exception {
        File file = registeredConfigs.get(configuration);
        if (file == null)
            throw new ConfigNotRegisteredException(configuration);
        writeToFile(file, configuration);
    }

    @Override
    public void reloadAll() throws Exception {
        for (Configuration configuration : registeredConfigs.keySet())
            reload(configuration);
    }

    @Override
    public void saveAll() throws Exception {
        for (Configuration configuration : registeredConfigs.keySet())
            save(configuration);
    }

    @Override
    public MapperRegistry getMapperRegistry() {
        return ConfigLib.getMapperRegistry();
    }

    @Override
    public boolean isRegistered(Configuration configuration) {
        return registeredConfigs.containsKey(configuration);
    }

    @Override
    public File getConfigFile(Configuration configuration) {
        return registeredConfigs.get(configuration);
    }

    @Override
    public void writeToFile(File file, Configuration configuration) throws IllegalAccessException, IOException, InvalidConfigurationException {
        writeToFile(file, configuration, null);
    }

    @Override
    public void writeToFile(File file, Configuration configuration, String path) throws IllegalAccessException, IOException, InvalidConfigurationException {
        writeToFile(file, YMLProfile.readConfigObject(configuration, configOptions.getSectionSpacing(), configOptions.getFieldSpacing()), path);
    }

    public void writeToFile(File file, YMLProfile ymlProfile) throws IOException, InvalidConfigurationException {
        writeToFile(file, ymlProfile, null);
    }

    public void writeToFile(File file, YMLProfile ymlProfile, String path) throws IOException, InvalidConfigurationException {
        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists())
                parentDir.mkdirs();

            file.createNewFile();
        }

        if (path == null ) path = "";
        else if (!path.isBlank() && !path.endsWith(".")) path += '.';
        String finalPath = path.trim();

        FileConfiguration config = (FileConfiguration) readFile(file);

        ymlProfile.getConfig().forEach((string, value) -> {
            String mapper = value.getMapper();
            String absolutePath = finalPath + string;

            if ((value.getFieldClass() == List.class || value.getFieldClass() == Set.class) && !isPrimative(ReflectionUtil.getGenericTypes(value.getField())[0])) {
                Collection<?> collection = (Collection<?>) value.getValue();

                ConfigurationSection collectionSection = config.createSection(absolutePath);

                if (collection != null) {
                    Class<?> genericClass = ReflectionUtil.getGenericTypes(value.getField())[0];
                    int i = 0;
                    for (Object o : collection)
                        setObjectCorrectly(genericClass, o, collectionSection, String.valueOf(i++), mapper);
                }
            }

            else if (value.getFieldClass() == Map.class && !isPrimative(ReflectionUtil.getGenericTypes(value.getField())[1])) {
                Class<?>[] genericClasses = ReflectionUtil.getGenericTypes(value.getField());
                if (genericClasses[0] != String.class) {
                    new RuntimeException("Map key class must be of type String at '"+value.getField().getName()+"'").printStackTrace();
                    return;
                }
                Map<String, ?> map = (Map<String, ?>) value.getValue();

                ConfigurationSection mapSection = config.createSection(absolutePath);

                if (map != null)
                    map.forEach((key, object) -> {
                        setObjectCorrectly(genericClasses[1], object, mapSection, key, mapper);
                    });
            }

            else
                setObjectCorrectly(value.getFieldClass(), value.getValue(), config, absolutePath, mapper);
        });

        ymlProfile.getComments().forEach((string, strings) -> config.setComments(finalPath + string, strings));
        ymlProfile.getInlineComments().forEach((string, strings) -> config.setInlineComments(finalPath + string, strings));

        config.save(file);
    }

    @Override
    public void writeToFields(Configuration configuration, ConfigurationSection section) throws IllegalAccessException {
        Class<?> clazz = configuration.getClass();

        String currentSectionPath = "";

        for (Field declaredField : clazz.getDeclaredFields()) {
            declaredField.setAccessible(true);

            Configuration.Field fieldAnnotation = declaredField.getAnnotation(Configuration.Field.class);
            if (fieldAnnotation == null) continue;

            Configuration.Section sectionAnnotation = declaredField.getAnnotation(Configuration.Section.class);
            if (sectionAnnotation != null)
                currentSectionPath = sectionAnnotation.value().isBlank() ? "" : sectionAnnotation.value() + (sectionAnnotation.value().endsWith(".") ? "" : ".");

            Configuration.Mapper mapperAnnotation = declaredField.getAnnotation(Configuration.Mapper.class);
            String mapper = "";
            if (mapperAnnotation != null)
                mapper = mapperAnnotation.value();

            declaredField.setAccessible(true);
            String absolutePath = currentSectionPath + YMLProfile.getFieldPath(declaredField, fieldAnnotation);

            if ((declaredField.getType() == List.class || declaredField.getDeclaringClass() == Set.class) && ! isPrimative(ReflectionUtil.getGenericTypes(declaredField)[0])) {
                Collection<?> collection = getList(declaredField, section, absolutePath, mapper);
                declaredField.set(configuration, collection);
            }
            else if (declaredField.getType() == Map.class && ! isPrimative(ReflectionUtil.getGenericTypes(declaredField)[1])) {
                Class<?>[] genericClasses = ReflectionUtil.getGenericTypes(declaredField);
                if (genericClasses[0] != String.class) {
                    new RuntimeException("Map key class must be of type String at '"+declaredField.getName()+"'").printStackTrace();
                    return;
                }

                Map<String, ?> map = getMap(genericClasses[1], section, absolutePath, mapper);
                declaredField.set(configuration, map);
            }

            else {
                Object o = getObjectCorrectly(declaredField.getType(), mapper, section, absolutePath);
                declaredField.set(configuration, o);
            }
        }
    }

    @Override
    public ConfigurationSection readFile(File file) throws IOException, InvalidConfigurationException {
        FileConfiguration config = new YamlConfiguration();
        config.load(file);
        return config;
    }

    @Override
    public DefaultConfigManager setConfigOptions(ConfigOptions configOptions) {
        this.configOptions = configOptions;
        return this;
    }

    @Override
    public ConfigOptions getConfigOptions() {
        return configOptions;
    }

    private void setObjectCorrectly(Class<?> clazz, Object object, ConfigurationSection section, String path, String mapper) {
        AbstractMapper abstractMapper = getMapperRegistry().getMapper(clazz, mapper);
        if (abstractMapper != null) {
            // Handle custom Mapper
            abstractMapper.setInConfig(this, clazz, section, path);
        }
        if (clazz.isEnum()) {
            // Handle enum
            Enum<?> enumValue = (Enum<?>) object;
            section.set(path, enumValue.name());
        } else {
            // Let the fallback types be handled
            section.set(path, object);
        }
    }

    private <T> T getObjectCorrectly(Class<T> clazz, String mapper, ConfigurationSection section, String absolutePath) {
        AbstractMapper<T> abstractMapper = getMapperRegistry().getMapper(clazz, mapper);
        T o;
        if (abstractMapper != null) {
            // Handle custom Mapper
            o = abstractMapper.getFromConfig(this, section, absolutePath);
        }
        else if (clazz.isEnum()) {
            // Handle enum
            String enumValue = section.getString(absolutePath);
            if (enumValue != null) {
                @SuppressWarnings("unchecked")
                Enum<?> enumConstant = Enum.valueOf((Class<Enum>) clazz, enumValue);
                o = (T) enumConstant;
            } else
                o = null;
        }
        else {
            // Let the fallback types be handled
            o = (T) section.get(absolutePath);
        }
        return o;
    }

    private <T> Collection<?> getList(Field declaredField, ConfigurationSection section, String absolutePath, String mapper) {
        Class<T> genericClass = (Class<T>) ReflectionUtil.getGenericTypes(declaredField)[0];
        Collection<T> collection;

        if (declaredField.getType() == List.class)
            collection = new ArrayList<>();
        else
            collection = new LinkedHashSet<>();

        ConfigurationSection collectionSection = section.getConfigurationSection(absolutePath);

        if (collectionSection != null) {
            for (String key : collectionSection.getKeys(false)) {
                collection.add(getObjectCorrectly(genericClass, mapper, collectionSection, key));
            }
        }

        return collection;
    }

    private <T> Map<String, T> getMap(Class<T> value, ConfigurationSection section, String absolutePath, String mapper) {
        Map<String, T> map = new LinkedHashMap<>();
        ConfigurationSection mapSection = section.getConfigurationSection(absolutePath);

        if (mapSection != null)
            for (String key : mapSection.getKeys(false))
                map.put(key, getObjectCorrectly(value, mapper, mapSection, key));

        return map;
    }

    private static Set<Class<?>> primitiveClasses = new HashSet<>(Arrays.asList(
            String.class,
            Integer.class,
            Long.class,
            Double.class,
            Float.class,
            Character.class,
            Boolean.class,
            Byte.class,
            Short.class,
            Void.class
    ));

    private boolean isPrimative(Class<?> clazz) {
        if (clazz.isPrimitive()) return true;
        return primitiveClasses.contains(clazz);
    }

}
