package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeMapper implements AbstractMapper<LocalDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public LocalDateTime getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String dateTimeStr = section.getString(path);
        return (dateTimeStr == null || dateTimeStr.isEmpty()) ? null : LocalDateTime.parse(dateTimeStr, FORMATTER);
    }

    @Override
    public void setInConfig(ConfigManager manager, LocalDateTime instance, ConfigurationSection section, String path) {
        section.set(path, (instance == null) ? "" : instance.format(FORMATTER));
    }
}