package dev.splityosis.sysengine.common.io;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigFile {

    private File file;
    private final FileConfiguration config;

    public ConfigFile() {
        this(null);
    }

    public ConfigFile(File file) {
        this.file = file;
        if (file == null) {
            config = new YamlConfiguration();
            return;
        }

        try {
            if (!file.exists()) {
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists())
                    parentDir.mkdirs();

                file.createNewFile();
            }
            config = new YamlConfiguration();
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(){
        if (file == null) return;
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        file.delete();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void reload(){
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}