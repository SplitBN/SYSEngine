package dev.splityosis.sysengine.configlib.configuration;

import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

public interface ConfigMapper<T> extends Configuration, AbstractMapper<T>{

    T compile(ConfigManager manager, ConfigurationSection section, String path);

    void decompile(ConfigManager manager, T instance, ConfigurationSection section, String path);

    /**
     * Sets the configuration values in the provided ConfigurationSection based on the given instance.
     *
     * @param manager The ConfigManager instance managing the configuration.
     * @param instance The instance of the object to be mapped to the configuration.
     * @param section The ConfigurationSection where the configuration values will be set.
     * @param path The path within the ConfigurationSection where the values will be set.
     * @throws RuntimeException If an IllegalAccessException occurs during the process.
     */
    @Override
    default void setInConfig(ConfigManager manager, T instance, ConfigurationSection section, String path){
        decompile(manager, instance, section, path);
        try {
            section.set(path, null);
            YMLProfile ymlProfile = YMLProfile.readConfigObject(instance, manager.getConfigOptions().getSectionSpacing(), manager.getConfigOptions().getFieldSpacing());
            ymlProfile.getConfig().forEach((string, object) -> {
                section.set(path + "." + string, object);
            });

            ymlProfile.getComments().forEach((string, comments) -> {
                section.setComments(path + "." + string, comments);
            });

            ymlProfile.getInlineComments().forEach((string, comments) -> {
                section.setInlineComments(path + "." + string, comments);
            });

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves an object of type T from the given configuration section.
     *
     * @param manager the ConfigManager instance used to manage the configuration
     * @param section the ConfigurationSection from which to retrieve the data
     * @param path the path within the configuration section to read the data from
     * @return an object of type T populated with the data from the configuration
     * @throws RuntimeException if there is an error accessing the fields
     */
    @Override
    default T getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        try {
            manager.writeToFields(this, section.getConfigurationSection(path));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return compile(manager, section, path);
    }
}
