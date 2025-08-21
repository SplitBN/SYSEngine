package dev.splityosis.sysengine.configlib.mappers;

import com.cryptomorin.xseries.XSound;
import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;

public class XSoundMapper implements AbstractMapper<XSound> {


    @Override
    public XSound getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String sound = section.getString(path);
        if (sound == null)
            return null;
        return XSound.of(sound).orElseThrow(() -> new IllegalArgumentException("Invalid XSound path: " + section.getCurrentPath() + "." + path));
    }

    @Override
    public void setInConfig(ConfigManager manager, XSound instance, ConfigurationSection section, String path) {
        section.set(path, instance.name());
    }

}
