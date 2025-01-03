package dev.splityosis.sysengine.configlib.mappers;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import de.tr7zw.changeme.nbtapi.*;
import dev.splityosis.sysengine.configlib.configuration.ConfigMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.utils.ColorUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemStackMapper implements ConfigMapper<ItemStack> {

    @Field public XMaterial material;
    @Field public int amount;
    @Field public String displayName;
    @Field public List<String> lore;
    @Field public Map<String, Integer> enchantments = new HashMap<>();

    @FieldInlineComment("Base64, UUID or Username (Only applies for skulls)")
    @Field public String skin;

    @Field public NBTCompound customNbt;


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

        if (customNbt != null) {
            new NBTItem(finalItemStack, true).mergeCompound(customNbt);
        }

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

        enchantments = new HashMap<>();
        instance.getEnchantments().forEach((enchantment, integer) -> {
            enchantments.put(XEnchantment.matchXEnchantment(enchantment).name(), integer);
        });

        skin = null;
        if (material == XMaterial.PLAYER_HEAD || material == XMaterial.PLAYER_WALL_HEAD) {
            skin = XSkull.of(instance).getDelegateProfile().getProfileValue();
            if (skin == null)
                skin = "";
        }

        customNbt = getCustomNbtCompound(instance);
    }

    // A set of known top-level vanilla keys in most Minecraft versions
    private static final Set<String> VANILLA_KEYS = new HashSet<>(Arrays.asList(
            "Damage",
            "Unbreakable",
            "HideFlags",
            "RepairCost",
            "display",
            "ench",
            "AttributeModifiers",
            "CustomModelData",
            "BlockEntityTag",
            "BlockStateTag",
            "LocName",
            "Potion",
            "SkullOwner",
            "StoredEnchantments",
            "author",
            "generation",
            "pages",
            "title",
            "Explosion",
            "Fireworks",
            "map",
            "Resolved",
            "color"
            // Add more if needed
    ));

    /**
     * Returns a new NBTCompound that contains only the "custom" (non-vanilla) top-level NBT
     * from the given ItemStack. Sub-compounds of these custom keys are copied in full.
     *
     * @param item The ItemStack to examine
     * @return A new NBTCompound with custom NBT data, or empty if none
     */
    public static NBTCompound getCustomNbtCompound(ItemStack item) {
        if (item == null) return null;

        // Wrap the item in an NBTItem
        NBTItem nbtItem = new NBTItem(item);
        // In many versions of the API, "nbtItem" itself is an NBTCompound,
        // or you can call nbtItem.getCompound() for the root
        NBTCompound root = nbtItem;

        // We'll store custom keys in a new NBTContainer (which is-a NBTCompound)
        NBTContainer customRoot = new NBTContainer();

        // Loop over top-level keys
        for (String key : root.getKeys()) {
            // Skip if it's a known vanilla key
            if (VANILLA_KEYS.contains(key)) {
                continue;
            }
            // Otherwise copy it (including sub-compounds) into our 'customRoot'
            copyKey(root, customRoot, key);
        }

        return customRoot;
    }

    /**
     * Copies a single key/value (including nested compounds/lists) from 'source' into 'target'.
     * Only handles the types that exist in NBTCompound (see your source code).
     */
    private static void copyKey(NBTCompound source, NBTCompound target, String key) {
        NBTType type = source.getType(key);
        if (type == null) return;

        switch (type) {
            case NBTTagByte:
                target.setByte(key, source.getByte(key));
                break;
            case NBTTagShort:
                target.setShort(key, source.getShort(key));
                break;
            case NBTTagInt:
                target.setInteger(key, source.getInteger(key));
                break;
            case NBTTagLong:
                target.setLong(key, source.getLong(key));
                break;
            case NBTTagFloat:
                target.setFloat(key, source.getFloat(key));
                break;
            case NBTTagDouble:
                target.setDouble(key, source.getDouble(key));
                break;
            case NBTTagByteArray:
                target.setByteArray(key, source.getByteArray(key));
                break;
            case NBTTagIntArray:
                target.setIntArray(key, source.getIntArray(key));
                break;
            case NBTTagLongArray:
                target.setLongArray(key, source.getLongArray(key));
                break;
            case NBTTagString:
                target.setString(key, source.getString(key));
                break;
            case NBTTagCompound:
                // Recursively copy sub-compound
                NBTCompound sourceSub = source.getCompound(key);
                if (sourceSub == null) break;

                NBTCompound targetSub = target.addCompound(key);
                for (String subKey : sourceSub.getKeys()) {
                    copyKey(sourceSub, targetSub, subKey);
                }
                break;
            case NBTTagList:
                // The old line caused infinite recursion. Use a dedicated method:
                copyList(source, target, key);
                break;
            default:
                // NBTTagEnd or unknown
                break;
        }
    }

    /**
     * Copies a list from 'source' to 'target' for the given key,
     * including sub-compounds (compound lists) and int[] arrays.
     */
    private static void copyList(NBTCompound source, NBTCompound target, String key) {
        // Determine the type of elements stored in this NBTTagList
        NBTType elementType = source.getListType(key);
        if (elementType == null) return;

        switch (elementType) {
            case NBTTagString: {
                // source list
                NBTList<String> srcList = source.getStringList(key);
                // target list
                NBTList<String> tgtList = target.getStringList(key);
                tgtList.clear();
                // copy
                for (String val : srcList) {
                    tgtList.add(val);
                }
                break;
            }

            case NBTTagInt: {
                NBTList<Integer> srcList = source.getIntegerList(key);
                NBTList<Integer> tgtList = target.getIntegerList(key);
                tgtList.clear();
                for (Integer val : srcList) {
                    tgtList.add(val);
                }
                break;
            }

            case NBTTagFloat: {
                NBTList<Float> srcList = source.getFloatList(key);
                NBTList<Float> tgtList = target.getFloatList(key);
                tgtList.clear();
                for (Float val : srcList) {
                    tgtList.add(val);
                }
                break;
            }

            case NBTTagDouble: {
                NBTList<Double> srcList = source.getDoubleList(key);
                NBTList<Double> tgtList = target.getDoubleList(key);
                tgtList.clear();
                for (Double val : srcList) {
                    tgtList.add(val);
                }
                break;
            }

            case NBTTagLong: {
                NBTList<Long> srcList = source.getLongList(key);
                NBTList<Long> tgtList = target.getLongList(key);
                tgtList.clear();
                for (Long val : srcList) {
                    tgtList.add(val);
                }
                break;
            }

            case NBTTagCompound: {
                // List of sub-compounds
                NBTCompoundList srcList = source.getCompoundList(key);
                NBTCompoundList tgtList = target.getCompoundList(key);
                tgtList.clear();

                // For each sub-compound in the source list, create a new sub-compound in the target
                // and recursively copy *its* keys with copyKey(...)
                for (int i = 0; i < srcList.size(); i++) {
                    NBTCompound srcElem = srcList.get(i);
                    NBTCompound tgtElem = tgtList.addCompound();

                    // Copy each key from srcElem into tgtElem
                    for (String subKey : srcElem.getKeys()) {
                        copyKey(srcElem, tgtElem, subKey);
                    }
                }
                break;
            }

            case NBTTagIntArray: {
                // NBT-API supports a list of int[] (e.g. getIntArrayList)
                NBTList<int[]> srcList = source.getIntArrayList(key);
                NBTList<int[]> tgtList = target.getIntArrayList(key);
                tgtList.clear();

                for (int[] val : srcList) {
                    // to be safe, copy the array
                    int[] copied = java.util.Arrays.copyOf(val, val.length);
                    tgtList.add(copied);
                }
                break;
            }

            /*
             * If you want to handle a list of UUID (NBT stores them internally as int arrays),
             * you could do something similar for 'getUUIDList(key)', if your version supports it.
             */

            default:
                // Possibly NBTTagByte or NBTTagShort in a list (not typically used in vanilla or NBT-API typed lists),
                // or NBTTagEnd. If you want to handle them, you'd do something special here.
                break;
        }
    }


}
