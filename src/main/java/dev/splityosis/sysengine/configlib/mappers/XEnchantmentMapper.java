package dev.splityosis.sysengine.configlib.mappers;

import com.cryptomorin.xseries.XEnchantment;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

public class XEnchantmentMapper implements AbstractMapper<XEnchantment> {

    @Override
    public XEnchantment getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String str = section.getString(path);
        if (str == null || str.isEmpty()) return null;
        return XEnchantment.matchXEnchantment(str.toUpperCase()).orElse(null);
    }

    @Override
    public void setInConfig(ConfigManager manager, XEnchantment instance, ConfigurationSection section, String path) {
        section.set(path, instance == null ? "" : instance.name());
    }
}
