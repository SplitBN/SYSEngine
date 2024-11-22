package dev.splityosis.sysengine.configlib.manager;

import dev.splityosis.sysengine.configlib.exceptions.ConfigNotRegisteredException;
import dev.splityosis.sysengine.configlib.ConfigLib;
import dev.splityosis.sysengine.configlib.configuration.YMLProfile;
import dev.splityosis.sysengine.configlib.configuration.Configuration;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
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

            if (value.getFieldClass() == List.class) {
                // TODO Handle list
            }

            else if (value.getFieldClass() == Set.class) {
                // TODO Handle Set
            }

            else if (value.getFieldClass() == Map.class) {
                // TODO Handle Map
            }

            else {
                setObjectCorrectly(value, config, finalPath + string, mapper);
            }
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

            if (declaredField.getType() == List.class) {
                // TODO handle List
            }

            else if (declaredField.getType() == Set.class) {
                // TODO handle set
            }

            else if (declaredField.getType() == Map.class) {
                // TODO handle Map
            }

            else {
                Object o = getObjectCorrectly(declaredField, mapper, section, absolutePath);
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

    private void setObjectCorrectly(YMLProfile.MapperClassValue value, ConfigurationSection section, String path, String mapper) {
        AbstractMapper abstractMapper = getMapperRegistry().getMapper(value.getFieldClass(), mapper);
        if (abstractMapper != null) {
            // Handle custom Mapper
            abstractMapper.setInConfig(this, value.getValue(), section, path);
        }
        if (value.getFieldClass().isEnum()) {
            // Handle enum
            Enum<?> enumValue = (Enum<?>) value.getValue();
            section.set(path, enumValue.name());
        }
        else {
            // Let the fallback types be handled
            section.set(path, value.getValue());
        }
    }

    private Object getObjectCorrectly(Field declaredField, String mapper, ConfigurationSection section, String absolutePath) {
        AbstractMapper abstractMapper = getMapperRegistry().getMapper(declaredField.getType(), mapper);
        Object o;
        if (abstractMapper != null) {
            // Handle custom Mapper
            o = abstractMapper.getFromConfig(this, section, absolutePath);
        }
        else if (declaredField.getType().isEnum()) {
            // Handle enum
            String enumValue = section.getString(absolutePath);
            if (enumValue != null) {
                @SuppressWarnings("unchecked")
                Enum<?> enumConstant = Enum.valueOf((Class<Enum>) declaredField.getType(), enumValue);
                o = enumConstant;
            } else
                o = null;
        }
        else {
            // Let the fallback types be handled
            o = section.get(absolutePath);
        }
        return o;
    }

}
