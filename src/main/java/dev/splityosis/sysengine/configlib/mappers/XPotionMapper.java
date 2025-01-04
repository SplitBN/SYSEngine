package dev.splityosis.sysengine.configlib.mappers;

import com.cryptomorin.xseries.XPotion;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

public class XPotionMapper implements AbstractMapper<XPotion> {

    @Override
    public XPotion getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String str = section.getString(path);
        if (str == null || str.isEmpty()) return null;
        return XPotion.matchXPotion(str.toUpperCase()).orElse(null);
    }

    @Override
    public void setInConfig(ConfigManager manager, XPotion instance, ConfigurationSection section, String path) {
        section.set(path, instance == null ? "" : instance.name());
    }
}