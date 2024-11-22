package dev.splityosis.sysengine.configlib.configuration;

import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

public interface ConfigMapper<T> extends Configuration, AbstractMapper<T>{

    T compile();

    void decompile(T instance);

    @Override
    default void setInConfig(ConfigManager manager, T instance, ConfigurationSection section, String path){
        decompile(instance);
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
                section.setComments(path + "." + string, comments);
            });

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    default T getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        try {
            manager.writeToFields(this, section.getConfigurationSection(path));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return compile();
    }
}
