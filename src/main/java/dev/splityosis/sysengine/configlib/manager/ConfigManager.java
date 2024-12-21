package dev.splityosis.sysengine.configlib.manager;

import dev.splityosis.sysengine.configlib.configuration.Configuration;
import dev.splityosis.sysengine.configlib.configuration.ConfigProfile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.*;

/**
 * Interface for managing configurations in the system.
 * Provides methods for registering, unregistering, reloading, and saving configurations.
 * Supports writing and reading configuration data to/from files, as well as managing configuration fields.
 */
public interface ConfigManager {

    /**
     * Registers a configuration with a specific file.
     * If the file doesn't exist, it will be created.
     *
     * @param configuration the configuration to register
     * @param file the file to associate with the configuration
     * @throws Exception if an error occurs while registering the config
     */
    void registerConfig(Configuration configuration, File file) throws Exception;

    /**
     * Unregisters a configuration, removing its association with a file.
     *
     * @param configuration the configuration to unregister
     */
    void unregisterConfig(Configuration configuration);

    /**
     * Reloads the configuration from its file, updating the fields in the configuration object.
     *
     * @param configuration the configuration to reload
     * @throws Exception if an error occurs during reload
     */
    void reload(Configuration configuration) throws Exception;

    /**
     * Saves the configuration back to its file.
     *
     * @param configuration the configuration to save
     * @throws Exception if an error occurs while saving the config
     */
    void save(Configuration configuration) throws Exception;

    /**
     * Reloads all registered configurations.
     *
     * @throws Exception if an error occurs while reloading all configurations
     */
    void reloadAll() throws Exception;

    /**
     * Saves all registered configurations to their respective files.
     *
     * @throws Exception if an error occurs while saving all configurations
     */
    void saveAll() throws Exception;

    /**
     * Retrieves the MapperRegistry, which manages the mappers used for configuration data.
     *
     * @return the MapperRegistry instance
     */
    MapperRegistry getMapperRegistry();

    /**
     * Checks if a configuration is already registered.
     *
     * @param configuration the configuration to check
     * @return true if the configuration is registered, false otherwise
     */
    boolean isRegistered(Configuration configuration);

    /**
     * Gets the file associated with a configuration.
     *
     * @param configuration the configuration to get the file for
     * @return the file associated with the configuration
     */
    File getConfigFile(Configuration configuration);

    /**
     * Writes the configuration data to a file.
     * The configuration's annotated fields will be serialized to the file.
     *
     * @param file the file to write to
     * @param configuration the configuration to write
     * @throws IllegalAccessException if a field cannot be accessed
     * @throws IOException if an error occurs while writing to the file
     * @throws InvalidConfigurationException if the configuration is invalid
     */
    void writeToFile(File file, Configuration configuration) throws IllegalAccessException, IOException, InvalidConfigurationException;

    /**
     * Writes the configuration data to a file at a specific path.
     * The configuration's annotated fields will be serialized to the file at the specified path.
     *
     * @param file the file to write to
     * @param configuration the configuration to write
     * @param path the path within the file to write the configuration
     * @throws IllegalAccessException if a field cannot be accessed
     * @throws IOException if an error occurs while writing to the file
     * @throws InvalidConfigurationException if the configuration is invalid
     */
    void writeToFile(File file, Configuration configuration, String path) throws IllegalAccessException, IOException, InvalidConfigurationException;


    /**
     * Write the given YMLProfile into an existing ConfigurationSection,
     * reusing the same logic that handles lists, maps, enums, etc.
     *
     * @param configProfile the ConfigProfile to write
     * @param section    the ConfigurationSection to write into
     * @param path       the subpath within section (can be null)
     */
    void setProfileInSection(ConfigProfile configProfile, ConfigurationSection section, String path);

    /**
     * Writes the fields of the configuration to a ConfigurationSection.
     * Fields are mapped based on the @Field annotations.
     *
     * @param configuration the configuration whose fields are to be written
     * @param section the section to write the fields to
     * @throws IllegalAccessException if a field cannot be accessed
     */
    void writeToFields(Configuration configuration, ConfigurationSection section) throws IllegalAccessException;

    /**
     * Reads configuration data from a file.
     * The data is returned as a ConfigurationSection.
     *
     * @param file the file to read from
     * @return the ConfigurationSection containing the configuration data
     * @throws IOException if an error occurs while reading the file
     * @throws InvalidConfigurationException if the configuration is invalid
     */
    ConfigurationSection readFile(File file) throws IOException, InvalidConfigurationException;

    /**
     * Sets the configuration options for the ConfigManager.
     *
     * @param configOptions the configuration options to set
     * @return the ConfigManager instance with the updated options
     */
    ConfigManager setConfigOptions(ConfigOptions configOptions);

    /**
     * Gets the current configuration options used by the ConfigManager.
     *
     * @return the current configuration options
     */
    ConfigOptions getConfigOptions();
}
