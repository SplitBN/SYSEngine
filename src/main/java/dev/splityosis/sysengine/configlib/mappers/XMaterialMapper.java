package dev.splityosis.sysengine.configlib.mappers;

import com.cryptomorin.xseries.XMaterial;
import dev.splityosis.sysengine.configlib.configuration.ConfigMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

public class XMaterialMapper implements ConfigMapper<XMaterial> {

    @Field("") String str;

    @Override
    public XMaterial compile(ConfigManager manager, ConfigurationSection section, String path) {
        if (str == null || str.isEmpty()) return null;
        return XMaterial.matchXMaterial(str.toUpperCase()).orElse(null);
    }

    @Override
    public void decompile(ConfigManager manager, XMaterial instance, ConfigurationSection section, String path) {
        str = instance.name();
    }
}
