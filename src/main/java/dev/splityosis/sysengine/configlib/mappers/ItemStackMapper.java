package dev.splityosis.sysengine.configlib.mappers;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XItemStack;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.splityosis.sysengine.configlib.configuration.ConfigMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.utils.ColorUtil;
import dev.splityosis.sysengine.utils.NBTUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemStackMapper implements ConfigMapper<ItemStack> {

    @Field public XMaterial material;
    @Field public int amount = 1;
    @Field public String displayName;
    @Field public List<String> lore;
    @Field public Map<String, Integer> enchantments = new HashMap<>();
    @Field public List<String> itemFlags = new ArrayList<>();
    @Field public boolean unbreakable = false;

    @FieldInlineComment("Base64, UUID or Username (only applies to skulls).")
    @Field public String skin;
    @Field public NBTCompound customNbt;
    @Field public Color color;
    @Field public PotionProperties potionData;

    @Override
    public @NotNull ItemStack compile(ConfigManager manager, ConfigurationSection section, String path) {
        if (material == null || !material.isSupported()) {
            material = XMaterial.STONE;
        }
        ItemStack itemStack = material.parseItem();
        if (itemStack == null) {
            itemStack = new ItemStack(Material.STONE);
        }

        if ((material == XMaterial.PLAYER_HEAD || material == XMaterial.PLAYER_WALL_HEAD)
                && skin != null && !skin.isEmpty()) {
            itemStack = XSkull.createItem().profile(Profileable.detect(skin)).apply();
        }

        itemStack.setAmount(amount);

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            if (displayName != null)
                meta.setDisplayName(ColorUtil.colorize(displayName));

            if (lore != null)
                meta.setLore(ColorUtil.colorize(lore));

            if (meta instanceof LeatherArmorMeta && color != null) {
                LeatherArmorMeta lam = (LeatherArmorMeta) meta;
                lam.setColor(color);
            }

            if (meta instanceof PotionMeta) {
                PotionMeta pm = (PotionMeta) meta;

                if (potionData != null) {
                    if (potionData.basePotionData != null)
                        pm.setBasePotionData(potionData.basePotionData);
                    if (potionData.customEffects != null)
                        for (PotionEffect effect : potionData.customEffects)
                            if (effect != null)
                                pm.addCustomEffect(effect, true);
                }
            }

            if (itemFlags != null)
                for (String itemFlag : itemFlags) {
                    try {
                        ItemFlag itemFlag1 = ItemFlag.valueOf(itemFlag.toUpperCase());
                        meta.addItemFlags(itemFlag1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            itemStack.setItemMeta(meta);
        }

        if (itemStack.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(itemStack);
            nbtItem.setBoolean("Unbreakable", unbreakable);
            itemStack = nbtItem.getItem();
        }

        if (enchantments != null) {
            for (Map.Entry<String, Integer> entry : enchantments.entrySet()) {
                String enchName = entry.getKey();
                int level = entry.getValue();

                XEnchantment xEnch = XEnchantment.matchXEnchantment(enchName.toUpperCase()).orElse(null);
                if (xEnch == null || !xEnch.isSupported()) continue;

                Enchantment bukkitEnch = xEnch.getEnchant();
                itemStack.addUnsafeEnchantment(bukkitEnch, level);
            }
        }

        // Custom NBT
        if (customNbt != null && itemStack.getType() != Material.AIR) {
            NBTItem nbtItem = new NBTItem(itemStack);
            nbtItem.mergeCompound(customNbt);
            itemStack = nbtItem.getItem();
        }

        return itemStack;
    }

    @Override
    public void decompile(ConfigManager manager, ItemStack instance, ConfigurationSection section, String path) {
        if (instance == null) {
            section.createSection(path);
            return;
        }
        material = XMaterial.matchXMaterial(instance);
        amount = instance.getAmount();

        color = null;
        potionData = null;
        itemFlags = new ArrayList<>();
        ItemMeta meta = instance.getItemMeta();
        if (meta != null) {
            displayName = (meta.getDisplayName() != null)
                    ? ColorUtil.reverseColorize(meta.getDisplayName())
                    : null;

            lore = (meta.getLore() != null)
                    ? ColorUtil.reverseColorize(meta.getLore())
                    : null;

            if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta lam = (LeatherArmorMeta) meta;
                color = lam.getColor();
            }

            if (meta instanceof PotionMeta) {
                potionData = new PotionProperties();
                PotionMeta pm = (PotionMeta) meta;
                potionData.basePotionData = pm.getBasePotionData();
                potionData.customEffects = pm.getCustomEffects();
                if (!potionData.customEffects.isEmpty())
                    potionData.customEffects = new ArrayList<>(potionData.customEffects);
            }

            for (ItemFlag itemFlag : meta.getItemFlags())
                itemFlags.add(itemFlag.name());
        }

        enchantments = new HashMap<>();
        instance.getEnchantments().forEach((bukkitEnch, level) -> {
            String xName = XEnchantment.matchXEnchantment(bukkitEnch).name();
            enchantments.put(xName, level);
        });

        skin = null;
        if (material == XMaterial.PLAYER_HEAD || material == XMaterial.PLAYER_WALL_HEAD) {
            skin = XSkull.of(instance).getDelegateProfile().getProfileValue();
            if (skin == null) {
                skin = "";
            }
        }

        unbreakable = false;
        if (instance.getType() != Material.AIR)
            unbreakable = new NBTItem(instance).getBoolean("Unbreakable");

        // Custom NBT
        if (instance.getType() != Material.AIR) {
            customNbt = NBTUtil.getCustomNbtCompound(instance);
        } else {
            customNbt = new NBTContainer();
        }
    }

    @Override
    public void setInConfig(ConfigManager manager, ItemStack instance, ConfigurationSection section, String path) {
        if (instance != null)
            ConfigMapper.super.setInConfig(manager, instance, section, path);
        else
            section.createSection(path);
    }

    @Override
    public ItemStack getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        ConfigurationSection configSection = section.getConfigurationSection(path);
        if (configSection == null || configSection.getKeys(false).isEmpty())
            return null;
        return ConfigMapper.super.getFromConfig(manager, section, path);
    }

    public static class PotionProperties {
        public PotionData basePotionData;
        public List<PotionEffect> customEffects;
    }
}
