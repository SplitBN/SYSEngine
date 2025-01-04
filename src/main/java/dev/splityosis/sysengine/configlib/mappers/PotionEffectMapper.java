package dev.splityosis.sysengine.configlib.mappers;

import com.cryptomorin.xseries.XPotion;
import dev.splityosis.sysengine.configlib.configuration.ConfigMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.utils.VersionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectMapper implements ConfigMapper<PotionEffect> {

    @Field public XPotion type;
    @Field public int duration;
    @Field public int amplifier;
    @Field public boolean ambient = false;
    @Field public boolean particles = true;
    @Field public boolean icon = true;

    public PotionEffectMapper() {}

    @Override
    public PotionEffect compile(ConfigManager manager, ConfigurationSection section, String path) {
        PotionEffectType pet = type.getPotionEffectType();
        if (pet == null) return null;

        if (VersionUtil.isServerAtLeast("1.9")) {
            return new PotionEffect(pet, duration, amplifier, ambient, particles, icon);
        } else if (VersionUtil.isServerAtLeast("1.8")) {
            return new PotionEffect(pet, duration, amplifier, ambient, particles);
        } else {
            return new PotionEffect(pet, duration, amplifier);
        }
    }

    @Override
    public void decompile(ConfigManager manager, PotionEffect instance, ConfigurationSection section, String path) {
        this.type = XPotion.matchXPotion(instance.getType());
        this.duration = instance.getDuration();
        this.amplifier = instance.getAmplifier();
        this.ambient = instance.isAmbient();

        if (VersionUtil.isServerAtLeast("1.8")) {
            this.particles = instance.hasParticles();
        }

        if (VersionUtil.isServerAtLeast("1.9")) {
            this.icon = instance.hasIcon();
        }
    }
}
