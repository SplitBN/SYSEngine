package dev.splityosis.sysengine.configlib.mappers;

import com.cryptomorin.xseries.XPotion;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectMapper implements AbstractMapper<PotionEffect> {

    @Override
    public PotionEffect getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String effectString = section.getString(path);
        if (effectString == null || effectString.isEmpty()) {
            return null;
        }

        String[] parts = effectString.split(" ");
        if (parts.length < 1) {
            return null;
        }

        PotionEffectType type = PotionEffectType.getByName(parts[0]);
        if (type == null) {
            return null;
        }

        int duration = (parts.length >= 2) ? Integer.parseInt(parts[1]) : 600;

        int amplifier = (parts.length >= 3) ? Integer.parseInt(parts[2]) : 0;

        return new PotionEffect(type, duration, amplifier);
    }

    @Override
    public void setInConfig(ConfigManager manager, PotionEffect instance, ConfigurationSection section, String path) {
        if (instance == null) {
            section.set(path, "");
            return;
        }

        String effectString = String.format(
                "%s %d %d",
                instance.getType().getName(),
                instance.getDuration(),
                instance.getAmplifier()
        );
        section.set(path, effectString);
    }
}
