package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;

import java.util.UUID;

public class UUIDMapper implements AbstractMapper<UUID> {

    @Override
    public UUID getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String uuidStr = section.getString(path);
        return (uuidStr == null || uuidStr.isEmpty()) ? null : UUID.fromString(uuidStr);
    }

    @Override
    public void setInConfig(ConfigManager manager, UUID instance, ConfigurationSection section, String path) {
        section.set(path, (instance == null) ? "" : instance.toString());
    }
}
