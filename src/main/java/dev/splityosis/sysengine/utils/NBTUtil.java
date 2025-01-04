package dev.splityosis.sysengine.utils;

import de.tr7zw.changeme.nbtapi.*;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NBTUtil {

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

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound root = nbtItem;

        NBTContainer customRoot = new NBTContainer();

        for (String key : root.getKeys()) {
            if (VANILLA_KEYS.contains(key)) {
                continue;
            }
            copyKey(root, customRoot, key);
        }

        return customRoot;
    }

    /**
     * Copies a single key/value (including nested compounds/lists) from 'source' into 'target'.
     * Only handles the types that exist in NBTCompound.
     */
    public static void copyKey(NBTCompound source, NBTCompound target, String key) {
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
                copyList(source, target, key);
                break;
            default:
                break;
        }
    }

    /**
     * Copies a list from 'source' to 'target' for the given key,
     * including sub-compounds (compound lists) and int[] arrays.
     */
    private static void copyList(NBTCompound source, NBTCompound target, String key) {
        NBTType elementType = source.getListType(key);
        if (elementType == null) return;

        switch (elementType) {
            case NBTTagString: {
                NBTList<String> srcList = source.getStringList(key);
                NBTList<String> tgtList = target.getStringList(key);
                tgtList.clear();
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
                NBTCompoundList srcList = source.getCompoundList(key);
                NBTCompoundList tgtList = target.getCompoundList(key);
                tgtList.clear();

                for (int i = 0; i < srcList.size(); i++) {
                    NBTCompound srcElem = srcList.get(i);
                    NBTCompound tgtElem = tgtList.addCompound();

                    for (String subKey : srcElem.getKeys()) {
                        copyKey(srcElem, tgtElem, subKey);
                    }
                }
                break;
            }

            case NBTTagIntArray: {
                NBTList<int[]> srcList = source.getIntArrayList(key);
                NBTList<int[]> tgtList = target.getIntArrayList(key);
                tgtList.clear();

                for (int[] val : srcList) {
                    int[] copied = java.util.Arrays.copyOf(val, val.length);
                    tgtList.add(copied);
                }
                break;
            }

            // Possibly lists of UUIDs

            default:
                break;
        }
    }


}
