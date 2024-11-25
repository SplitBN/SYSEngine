package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeMapper implements AbstractMapper<LocalTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    @Override
    public LocalTime getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String timeStr = section.getString(path);
        return (timeStr == null || timeStr.isEmpty()) ? null : LocalTime.parse(timeStr, FORMATTER);
    }

    @Override
    public void setInConfig(ConfigManager manager, LocalTime instance, ConfigurationSection section, String path) {
        section.set(path, (instance == null) ? "" : instance.format(FORMATTER));
    }
}