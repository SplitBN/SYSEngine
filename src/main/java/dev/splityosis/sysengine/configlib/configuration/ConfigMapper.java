package dev.splityosis.sysengine.configlib.configuration;

import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A specialized interface that behaves like a normal Configuration object
 * but also implements AbstractMapper. This lets you annotate fields within
 * the mapper itself and use them to load/save a particular object of type T.
 */
public interface ConfigMapper<T> extends Configuration, AbstractMapper<T> {

    /**
     * Called after the ConfigManager has written fields into 'this'.
     * Allows you to compile the final T object from the data in 'this'.
     */
    T compile(ConfigManager manager, ConfigurationSection section, String path);

    /**
     * Called before writing 'instance' to config. Lets you decompile
     * the T object into fields of 'this' if needed, or do transformations.
     */
    void decompile(ConfigManager manager, T instance, ConfigurationSection section, String path);

    /**
     * Write 'instance' of T into the configuration, using the manager's logic
     * for handling nested lists/maps/enums/custom mappers, etc.
     */
    @Override
    default void setInConfig(ConfigManager manager, T instance, ConfigurationSection section, String path) {
        decompile(manager, instance, section, path);

        section.set(path, null);

        try {
            ConfigProfile ymlProfile = ConfigProfile.readConfigObject(
                    this,
                    manager.getConfigOptions().getSectionSpacing(),
                    manager.getConfigOptions().getFieldSpacing()
            );

            manager.setProfileInSection(ymlProfile, section, path);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read data from the config into 'this' object, then compile
     * a new T from that data.
     */
    @Override
    default T getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        try {
            ConfigurationSection subSection = section.getConfigurationSection(path);
            if (subSection != null) {
                manager.writeToFields(this, subSection);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return compile(manager, section, path);
    }
}
