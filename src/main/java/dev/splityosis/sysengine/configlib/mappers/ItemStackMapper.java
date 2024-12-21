package dev.splityosis.sysengine.configlib.mappers;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import dev.splityosis.sysengine.configlib.configuration.ConfigMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.utils.ColorUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemStackMapper implements ConfigMapper<ItemStack> {

    @Field public XMaterial material;
    @Field public int amount;
    @Field public String displayName;
    @Field public List<String> lore;
    @Field public Map<String, Integer> enchantments = new HashMap<>();

    @FieldInlineComment("Base64, UUID or Username (Only applies for skulls)")
    @Field public String skin;


    @Override
    public ItemStack compile(ConfigManager manager, ConfigurationSection section, String path) {
        if (!material.isSupported()) return null; // TODO let them know
        ItemStack itemStack = material.parseItem();

        if (material == XMaterial.PLAYER_HEAD || material == XMaterial.PLAYER_WALL_HEAD) {
            if (skin != null && !skin.isEmpty())
                itemStack = XSkull.createItem().profile(Profileable.detect(skin)).apply();
        }

        itemStack.setAmount(amount);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            itemMeta.setDisplayName(displayName != null ? ColorUtil.colorize(displayName) : null);
            itemMeta.setLore(lore != null ? ColorUtil.colorize(lore) : null);
            itemStack.setItemMeta(itemMeta);
        }

        ItemStack finalItemStack = itemStack;
        if (enchantments != null)
            enchantments.forEach((string, integer) -> {
                XEnchantment xEnchantment = XEnchantment.matchXEnchantment(string.toUpperCase()).orElse(null);
                if (xEnchantment == null) return; // TODO let them know
                if (!xEnchantment.isSupported()) return; // TODO let them know
                finalItemStack.addUnsafeEnchantment(xEnchantment.getEnchant(), integer);
            });

        return finalItemStack;
    }

    @Override
    public void decompile(ConfigManager manager, ItemStack instance, ConfigurationSection section, String path) {
        material = XMaterial.matchXMaterial(instance);
        amount = instance.getAmount();
        ItemMeta itemMeta = instance.getItemMeta();

        if (itemMeta != null) {
            displayName = itemMeta.getDisplayName() != null ? ColorUtil.reverseColorize(itemMeta.getDisplayName()) : null;
            lore = itemMeta.getLore() != null ? ColorUtil.reverseColorize(itemMeta.getLore()) : null;
        }

        enchantments = null;
        instance.getEnchantments().forEach((enchantment, integer) -> {
            enchantments.put(XEnchantment.matchXEnchantment(enchantment).name(), integer);
        });

        skin = null;
        if (material == XMaterial.PLAYER_HEAD || material == XMaterial.PLAYER_WALL_HEAD) {
            skin = XSkull.of(instance).getDelegateProfile().getProfileValue();
            if (skin == null)
                skin = "";
        }
    }
}
