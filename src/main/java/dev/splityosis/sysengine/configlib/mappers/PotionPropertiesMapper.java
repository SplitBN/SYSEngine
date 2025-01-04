package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

public class PotionPropertiesMapper implements AbstractMapper<ItemStackMapper.PotionProperties> {

    @Override
    public ItemStackMapper.PotionProperties getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        ItemStackMapper.PotionProperties potionProperties = new ItemStackMapper.PotionProperties();
        potionProperties.basePotionData = new PotionDataMapper().getFromConfig(manager, section, path);

        ConfigurationSection configurationSection = section.getConfigurationSection(path + ".custom-effects");
        if (configurationSection != null) {
            PotionEffectMapper potionEffectMapper = new PotionEffectMapper();

            potionProperties.customEffects = new ArrayList<>();
            for (String effect : configurationSection.getKeys(false))
                potionProperties.customEffects.add(potionEffectMapper.getFromConfig(manager, configurationSection, effect));
        }
        return potionProperties;
    }

    @Override
    public void setInConfig(ConfigManager manager, ItemStackMapper.PotionProperties instance, ConfigurationSection section, String path) {
        section.createSection(path);
        if (instance == null) return;
        if (instance.basePotionData != null)
            new PotionDataMapper().setInConfig(manager, instance.basePotionData, section, path);

        if (instance.customEffects != null) {
            PotionEffectMapper potionEffectMapper = new PotionEffectMapper();
            int i = 0;
            for (PotionEffect customEffect : instance.customEffects) {
                potionEffectMapper.setInConfig(manager, customEffect, section, path + ".custom-effect." + i);
            }
        }
    }
}
