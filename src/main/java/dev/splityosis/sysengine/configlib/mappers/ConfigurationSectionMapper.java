package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;

public class ConfigurationSectionMapper implements AbstractMapper<ConfigurationSection> {

    @Override
    public ConfigurationSection getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        return section.getConfigurationSection(path);
    }

    @Override
    public void setInConfig(ConfigManager manager, ConfigurationSection instance, ConfigurationSection section, String path) {
        for (String key : instance.getKeys(false)) {
            Object value = instance.get(key);
            if (value instanceof ConfigurationSection) {
                ConfigurationSection newSection = section.createSection(path + "." + key);
                setInConfig(manager, (ConfigurationSection) value, newSection, "");
            } else
                section.set(path.isEmpty() ? key : path + "." + key, value);
        }
    }
}
