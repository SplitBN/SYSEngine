package dev.splityosis.sysengine.configlib.manager;

import dev.splityosis.sysengine.configlib.ConfigLib;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.configuration.Configuration;
import dev.splityosis.sysengine.configlib.configuration.ConfigProfile;
import dev.splityosis.sysengine.configlib.exceptions.ConfigNotRegisteredException;
import dev.splityosis.sysengine.utils.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class DefaultConfigManager implements ConfigManager {

    private ConfigOptions configOptions;
    private final Map<Configuration, File> registeredConfigs = new HashMap<>();

    // Basic set of classes considered "primitive-like"
    private static final Set<Class<?>> PRIMITIVE_CLASSES = new HashSet<>(Arrays.asList(
            String.class, Integer.class, Long.class, Double.class, Float.class,
            Character.class, Boolean.class, Byte.class, Short.class, Void.class
    ));

    public DefaultConfigManager() {
        this.configOptions = new ConfigOptions();
    }

    /* -------------------------------------------------
     * Registration / Unregistration
     * ------------------------------------------------- */

    @Override
    public void registerConfig(Configuration configuration, File file) throws Exception {
        if (!file.exists()) {
            writeToFile(file, configuration);
        }
        registeredConfigs.put(configuration, file);
        writeToFields(configuration, readFile(file));
    }

    @Override
    public void unregisterConfig(Configuration configuration) {
        if (registeredConfigs.remove(configuration) == null) {
            throw new ConfigNotRegisteredException(configuration);
        }
    }

    /* -------------------------------------------------
     * Reload / Save
     * ------------------------------------------------- */

    @Override
    public void reload(Configuration configuration) throws Exception {
        File file = registeredConfigs.get(configuration);
        if (file == null) {
            throw new ConfigNotRegisteredException(configuration);
        }
        writeToFields(configuration, readFile(file));
    }

    @Override
    public void save(Configuration configuration) throws Exception {
        File file = registeredConfigs.get(configuration);
        if (file == null) {
            throw new ConfigNotRegisteredException(configuration);
        }
        writeToFile(file, configuration);
    }

    @Override
    public void reloadAll() throws Exception {
        for (Configuration configuration : registeredConfigs.keySet()) {
            reload(configuration);
        }
    }

    @Override
    public void saveAll() throws Exception {
        for (Configuration configuration : registeredConfigs.keySet()) {
            save(configuration);
        }
    }

    /* -------------------------------------------------
     * Reading / Writing to File
     * ------------------------------------------------- */

    @Override
    public ConfigurationSection readFile(File file) throws IOException, InvalidConfigurationException {
        FileConfiguration config = new YamlConfiguration();
        config.load(file);
        return config;
    }

    @Override
    public void writeToFile(File file, Configuration configuration)
            throws IllegalAccessException, IOException, InvalidConfigurationException {
        writeToFile(file, configuration, null);
    }

    @Override
    public void writeToFile(File file, Configuration configuration, String path)
            throws IllegalAccessException, IOException, InvalidConfigurationException {
        ConfigProfile ymlProfile = ConfigProfile.readConfigObject(
                configuration,
                configOptions.getSectionSpacing(),
                configOptions.getFieldSpacing()
        );
        writeToFile(file, ymlProfile, path);
    }

    public void writeToFile(File file, ConfigProfile ymlProfile)
            throws IOException, InvalidConfigurationException {
        writeToFile(file, ymlProfile, null);
    }

    public void writeToFile(File file, ConfigProfile ymlProfile, String path)
            throws IOException, InvalidConfigurationException {

        if (!file.exists()) {
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            file.createNewFile();
        }

        FileConfiguration config = (FileConfiguration) readFile(file);
        setProfileInSection(ymlProfile, config, path); // <--- Use the new method

        if (ymlProfile.getConfiguration() != null) {
            ConfigurationSection section = null;
            if (path != null && !path.isBlank())
                section = config.createSection(path);
            
            if (section == null)
                section = config;
            ymlProfile.getConfiguration().onSave(file, section);
        }
        config.save(file);
    }



    @Override
    public void setProfileInSection(ConfigProfile configProfile, ConfigurationSection section, String path) {
        if (path == null) {
            path = "";
        } else if (!path.isBlank() && !path.endsWith(".")) {
            path += ".";
        }
        String finalPath = path.trim();

        configProfile.getConfig().forEach((key, ymlValue) -> {
            String mapper = ymlValue.getMapper();
            String absolutePath = finalPath + key;
            Class<?> fieldClass = ymlValue.getFieldClass();

            // Collection of non-primitive elements
            if ((fieldClass == List.class || fieldClass == Set.class) &&
                    !isPrimitive(ReflectionUtil.getGenericTypes(ymlValue.getField())[0])) {

                Collection<?> collection = (Collection<?>) ymlValue.getValue();
                ConfigurationSection collSec = section.createSection(absolutePath);

                if (collection != null) {
                    Class<?> genericClass = ReflectionUtil.getGenericTypes(ymlValue.getField())[0];
                    int i = 0;
                    for (Object o : collection) {
                        setObjectCorrectly(genericClass, o, collSec, String.valueOf(i++), mapper);
                    }
                }

            }
            // Map of non-primitive values
            else if (fieldClass == Map.class &&
                    !isPrimitive(ReflectionUtil.getGenericTypes(ymlValue.getField())[1])) {

                Class<?>[] generics = ReflectionUtil.getGenericTypes(ymlValue.getField());
                if (generics[0] != String.class) {
                    new RuntimeException(
                            "Map key must be String at '" + ymlValue.getField().getName() + "'"
                    ).printStackTrace();
                    return;
                }

                Map<String, ?> map = (Map<String, ?>) ymlValue.getValue();
                ConfigurationSection mapSec = section.createSection(absolutePath);

                if (map != null) {
                    map.forEach((mapKey, mapValue) -> {
                        setObjectCorrectly(generics[1], mapValue, mapSec, mapKey, mapper);
                    });
                }

            }
            else {
                setObjectCorrectly(fieldClass, ymlValue.getValue(), section, absolutePath, mapper);
            }
        });

        // Write block comments
        configProfile.getComments().forEach((commentKey, commentLines) -> {
            section.setComments(finalPath + commentKey, commentLines);
        });
        // Write inline comments
        configProfile.getInlineComments().forEach((inlineKey, inlineLines) -> {
            section.setInlineComments(finalPath + inlineKey, inlineLines);
        });
    }

    /* -------------------------------------------------
     * Writing to Fields
     * ------------------------------------------------- */

    @Override
    public void writeToFields(Configuration configuration, ConfigurationSection section)
            throws IllegalAccessException {
        if (section == null) return;

        Class<?> clazz = configuration.getClass();
        String currentSectionPath = "";

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Configuration.Field fieldAnnotation = field.getAnnotation(Configuration.Field.class);
            if (fieldAnnotation == null) continue;

            // Handle @Configuration.Section
            Configuration.Section sectionAnnotation = field.getAnnotation(Configuration.Section.class);
            if (sectionAnnotation != null) {
                String value = sectionAnnotation.value();
                currentSectionPath = (value.isBlank()) ? "" :
                        value + (value.endsWith(".") ? "" : ".");
            }

            // Handle @Configuration.Mapper
            Configuration.Mapper mapperAnnotation = field.getAnnotation(Configuration.Mapper.class);
            String mapper = (mapperAnnotation != null) ? mapperAnnotation.value() : "";

            String fieldPath = ConfigProfile.getFieldPath(field, fieldAnnotation);
            String absolutePath = currentSectionPath + fieldPath;

            // If it's a List/Set of non-primitives
            if ((field.getType() == List.class || field.getType() == Set.class) &&
                    !isPrimitive(ReflectionUtil.getGenericTypes(field)[0])) {

                Collection<?> collection = getList(field, section, absolutePath, mapper);
                field.set(configuration, collection);

            }
            // If it's a Map<String, non-primitive>
            else if (field.getType() == Map.class) {

                Class<?>[] genericTypes = ReflectionUtil.getGenericTypes(field);
                if (genericTypes[0] != String.class) {
                    new RuntimeException("Map key must be String at '" + field.getName() + "'")
                            .printStackTrace();
                    return;
                }
                Map<String, ?> map = getMap(genericTypes[1], section, absolutePath, mapper);
                field.set(configuration, map);

            } else {
                Object value = getObjectCorrectly(field.getType(), mapper, section, absolutePath);
                field.set(configuration, value);
            }
        }

        configuration.onLoad(section);
    }

    /* -------------------------------------------------
     * Helper Methods
     * ------------------------------------------------- */

    @Override
    public boolean isRegistered(Configuration configuration) {
        return registeredConfigs.containsKey(configuration);
    }

    @Override
    public File getConfigFile(Configuration configuration) {
        return registeredConfigs.get(configuration);
    }

    @Override
    public MapperRegistry getMapperRegistry() {
        return ConfigLib.getMapperRegistry();
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

    // Example of handling a single object with a mapper or fallback
    private <T> T getObjectCorrectly(Class<T> clazz, String mapper, ConfigurationSection section, String path) {
        AbstractMapper<T> abstractMapper = getMapperRegistry().getMapper(clazz, mapper);
        if (abstractMapper != null) {
            return abstractMapper.getFromConfig(this, section, path);
        } else if (clazz.isEnum()) {
            String enumValue = section.getString(path);
            if (enumValue != null) {
                @SuppressWarnings("unchecked")
                T enumConstant = (T) Enum.valueOf((Class<Enum>) clazz, enumValue);
                return enumConstant;
            }
            return null;
        } else {
            return (T) section.get(path);
        }
    }

    private void setObjectCorrectly(Class<?> clazz, Object object, ConfigurationSection section,
                                    String path, String mapper) {
        AbstractMapper abstractMapper = getMapperRegistry().getMapper(clazz, mapper);
        System.out.println("Setting "+clazz.getName());
        System.out.println("mapper is " + ((abstractMapper != null) ? abstractMapper.getClass().getName() : "null"));
        if (abstractMapper != null) {
            abstractMapper.setInConfig(this, object, section, path);
        } else if (clazz.isEnum() && object != null) {
            section.set(path, ((Enum<?>)object).name());
        } else {
            section.set(path, object);
        }
    }

    // Build a List/Set of complex objects
    private <T> Collection<T> getList(Field field, ConfigurationSection section,
                                      String path, String mapper) {
        Class<T> genericClass = (Class<T>) ReflectionUtil.getGenericTypes(field)[0];
        Collection<T> collection;
        if (field.getType() == List.class) {
            collection = new ArrayList<>();
        } else {
            collection = new LinkedHashSet<>();
        }

        ConfigurationSection collSec = section.getConfigurationSection(path);
        if (collSec != null) {
            for (String key : collSec.getKeys(false)) {
                T elem = getObjectCorrectly(genericClass, mapper, collSec, key);
                collection.add(elem);
            }
        }
        return collection;
    }

    // Build a Map<String, T> of complex objects
    private <T> Map<String, T> getMap(Class<T> clazz, ConfigurationSection section,
                                      String path, String mapper) {
        Map<String, T> map = new LinkedHashMap<>();
        ConfigurationSection mapSec = section.getConfigurationSection(path);
        if (mapSec != null) {
            for (String key : mapSec.getKeys(false)) {
                T value = getObjectCorrectly(clazz, mapper, mapSec, key);
                map.put(key, value);
            }
        }
        return map;
    }

    // Checks if it's considered primitive
    private boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() || PRIMITIVE_CLASSES.contains(clazz);
    }
}
