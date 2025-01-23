package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateMapper implements AbstractMapper<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public LocalDate getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String dateStr = section.getString(path);
        return (dateStr == null || dateStr.isEmpty()) ? null : LocalDate.parse(dateStr, FORMATTER);
    }

    @Override
    public void setInConfig(ConfigManager manager, LocalDate instance, ConfigurationSection section, String path) {
        section.set(path, (instance == null) ? "" : instance.format(FORMATTER));
    }
}