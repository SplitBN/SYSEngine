package dev.splityosis.sysengine.configlib.mappers;

import com.cryptomorin.xseries.XPotion;
import dev.splityosis.sysengine.configlib.configuration.ConfigMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class PotionDataMapper implements ConfigMapper<PotionData> {

    @Field public XPotion type;
    @Field public boolean extended = false;
    @Field public boolean upgraded = false;

    @Override
    public PotionData compile(ConfigManager manager, ConfigurationSection section, String path) {
        if (type == null)
             return null;
        PotionType potionType;
        try {
            potionType = type.getPotionType();
        } catch (IllegalArgumentException e) {
            potionType = PotionType.WATER;
        }
        return new PotionData(potionType, extended, upgraded);
    }

    @Override
    public void decompile(ConfigManager manager, PotionData instance, ConfigurationSection section, String path) {
        if (instance == null) {
            type = null;
            extended = false;
            upgraded = false;
            return;
        }
        this.type = XPotion.matchXPotion(instance.getType());
        this.extended = instance.isExtended();
        this.upgraded = instance.isUpgraded();
    }
}
