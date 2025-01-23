package dev.splityosis.sysengine.configlib.mappers;

import com.cryptomorin.xseries.XMaterial;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;

public class XMaterialMapper implements AbstractMapper<XMaterial> {

    @Override
    public XMaterial getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String str = section.getString(path);
        if (str == null || str.isEmpty()) return null;
        return XMaterial.matchXMaterial(str.toUpperCase()).orElse(null);
    }

    @Override
    public void setInConfig(ConfigManager manager, XMaterial instance, ConfigurationSection section, String path) {
        section.set(path, instance == null ? "" : instance.name());
    }
}
