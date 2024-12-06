package dev.splityosis.sysengine.configlib.mappers;

import com.cryptomorin.xseries.XMaterial;
import dev.splityosis.sysengine.configlib.configuration.ConfigMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class MaterialMapper implements ConfigMapper<Material> {
    @Field("") String str;

    @Override
    public Material compile(ConfigManager manager, ConfigurationSection section, String path) {
        if (str == null || str.isEmpty()) return null;
        XMaterial material = XMaterial.matchXMaterial(str.toUpperCase()).orElse(null);
        if (material == null) return null;
        return material.parseMaterial();
    }

    @Override
    public void decompile(ConfigManager manager, Material instance, ConfigurationSection section, String path) {
        str = instance.name();
    }
}
