package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;

import java.time.ZoneId;

public class ZoneIdMapper implements AbstractMapper<ZoneId> {

    @Override
    public ZoneId getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String zoneIdStr = section.getString(path);

        if (zoneIdStr == null || zoneIdStr.isEmpty())
            return null;

        return ZoneId.of(zoneIdStr);
    }

    @Override
    public void setInConfig(ConfigManager manager, ZoneId instance, ConfigurationSection section, String path) {
        section.set(path, (instance == null) ? "" : instance.getId());
    }

}
