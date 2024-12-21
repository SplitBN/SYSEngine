package dev.splityosis.sysengine.configlib.configuration;

import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Base interface for a mapper that can read/write objects of type T
 * from/to a ConfigurationSection.
 */
public interface AbstractMapper<T> {

    /**
     * Retrieves an object of type T from the given configuration section.
     *
     * @param manager the ConfigManager handling the configuration
     * @param section the ConfigurationSection from which to read
     * @param path    the path within the ConfigurationSection
     * @return an object of type T
     */
    T getFromConfig(ConfigManager manager, ConfigurationSection section, String path);

    /**
     * Sets an object of type T into the given configuration section.
     *
     * @param manager  the ConfigManager handling the configuration
     * @param instance the object instance to be mapped
     * @param section  the ConfigurationSection where data should be written
     * @param path     the path within the ConfigurationSection
     */
    void setInConfig(ConfigManager manager, T instance, ConfigurationSection section, String path);
}
