package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;

import java.time.Instant;

public class InstantMapper implements AbstractMapper<Instant> {

    @Override
    public Instant getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String instantStr = section.getString(path);
        return (instantStr == null || instantStr.isEmpty()) ? null : Instant.parse(instantStr);
    }

    @Override
    public void setInConfig(ConfigManager manager, Instant instance, ConfigurationSection section, String path) {
        section.set(path, (instance == null) ? "" : instance.toString());
    }
}