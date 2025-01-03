package dev.splityosis.sysengine.configlib.mappers;

import de.tr7zw.changeme.nbtapi.*;

import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.*;
import java.util.Base64;

/**
 * A fully refined mapper that groups each NBT type in the YAML under "byte:", "int:", etc.
 * Lists go under "list:" with "type: <elementType>" and "data: [items...]"
 */
public final class NBTCompoundMapper implements AbstractMapper<NBTCompound> {

    @Override
    public NBTCompound getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        // 1) Check if the target section exists
        ConfigurationSection subSec = section.getConfigurationSection(path);
        if (subSec == null) {
            // No data => return null or an empty container
            return null;
        }

        // 2) Create a new container to hold the data
        NBTContainer container = new NBTContainer();

        // 3) Convert the grouped YAML back into NBT
        groupedConfigToCompound(subSec, container);

        // 4) Return the resulting NBT
        return container;
    }

    @Override
    public void setInConfig(ConfigManager manager, NBTCompound instance, ConfigurationSection section, String path) {
        // If no data, clear the path
        if (instance == null) {
            section.createSection(path);
            return;
        }

        // 1) Create or clear the sub-section
        ConfigurationSection subSec = section.createSection(path);

        // 2) Write the NBT into that sub-section in grouped format
        compoundToGroupedConfig(instance, subSec);
    }

    // -------------------------------------------------------------------
    //  WRITING: (NBTCompound -> YAML)
    // -------------------------------------------------------------------

    /**
     * Converts an NBTCompound into a grouped YAML format inside 'section'.
     */
    public static void compoundToGroupedConfig(NBTCompound compound, ConfigurationSection section) {
        if (compound == null) return;

        for (String key : compound.getKeys()) {
            NBTType type = compound.getType(key);
            if (type == null) continue;  // skip invalid

            switch (type) {
                case NBTTagByte:
                    setPrimitive(section, "byte", key, compound.getByte(key));
                    break;

                case NBTTagShort:
                    setPrimitive(section, "short", key, compound.getShort(key));
                    break;

                case NBTTagInt:
                    setPrimitive(section, "int", key, compound.getInteger(key));
                    break;

                case NBTTagLong:
                    setPrimitive(section, "long", key, compound.getLong(key));
                    break;

                case NBTTagFloat:
                    setPrimitive(section, "float", key, compound.getFloat(key));
                    break;

                case NBTTagDouble:
                    setPrimitive(section, "double", key, compound.getDouble(key));
                    break;

                case NBTTagString:
                    setPrimitive(section, "string", key, compound.getString(key));
                    break;

                case NBTTagByteArray:
                    // Store a byte[] as a single Base64 string
                    byte[] byteArr = compound.getByteArray(key);
                    if (byteArr != null) {
                        String base64 = Base64.getEncoder().encodeToString(byteArr);
                        setPrimitive(section, "byteArray", key, base64);
                    }
                    break;

                case NBTTagIntArray:
                    // Store as a YAML list of ints
                    int[] iArr = compound.getIntArray(key);
                    if (iArr != null) {
                        setIntList(section, "intArray", key, iArr);
                    }
                    break;

                case NBTTagLongArray:
                    // Store as a YAML list of longs
                    long[] lArr = compound.getLongArray(key);
                    if (lArr != null) {
                        setLongList(section, "longArray", key, lArr);
                    }
                    break;

                case NBTTagCompound:
                    // Nested compound => store under "compounds" => key => sub-sections
                    NBTCompound subComp = compound.getCompound(key);
                    if (subComp != null) {
                        ConfigurationSection compSec = getOrCreate(section, "compounds");
                        ConfigurationSection nested = compSec.createSection(key);
                        compoundToGroupedConfig(subComp, nested);
                    }
                    break;

                case NBTTagList:
                    // It's a typed list. Let's store under "list" => key => { type: X, data: [...] }
                    storeListByType(compound, key, section);
                    break;

                default:
                    // NBTTagEnd or unknown
                    break;
            }
        }
    }

    private static void setPrimitive(ConfigurationSection root, String groupName, String key, Object value) {
        ConfigurationSection groupSec = getOrCreate(root, groupName);
        groupSec.set(key, value);
    }

    private static void setIntList(ConfigurationSection root, String groupName, String key, int[] array) {
        ConfigurationSection groupSec = getOrCreate(root, groupName);
        List<Integer> list = new ArrayList<>();
        for (int i : array) {
            list.add(i);
        }
        groupSec.set(key, list);
    }

    private static void setLongList(ConfigurationSection root, String groupName, String key, long[] array) {
        ConfigurationSection groupSec = getOrCreate(root, groupName);
        List<Long> list = new ArrayList<>();
        for (long l : array) {
            list.add(l);
        }
        groupSec.set(key, list);
    }

    private static ConfigurationSection getOrCreate(ConfigurationSection parent, String childName) {
        ConfigurationSection sub = parent.getConfigurationSection(childName);
        if (sub == null) {
            sub = parent.createSection(childName);
        }
        return sub;
    }

    private static void storeListByType(NBTCompound compound, String key, ConfigurationSection root) {
        ConfigurationSection listsSec = getOrCreate(root, "list");
        ConfigurationSection thisListSec = listsSec.createSection(key);

        NBTType elementType = compound.getListType(key);
        thisListSec.set("type", nbtTypeToName(elementType));

        switch (elementType) {
            case NBTTagString: {
                NBTList<String> sList = compound.getStringList(key);
                thisListSec.set("data", sList);
                break;
            }
            case NBTTagInt: {
                NBTList<Integer> iList = compound.getIntegerList(key);
                thisListSec.set("data", iList);
                break;
            }
            case NBTTagFloat: {
                NBTList<Float> fList = compound.getFloatList(key);
                List<Float> floats = new ArrayList<>(fList);
                thisListSec.set("data", floats);
                break;
            }
            case NBTTagDouble: {
                NBTList<Double> dList = compound.getDoubleList(key);
                List<Double> doubles = new ArrayList<>(dList);
                thisListSec.set("data", doubles);
                break;
            }
            case NBTTagLong: {
                NBTList<Long> lList = compound.getLongList(key);
                List<Long> longs = new ArrayList<>(lList);
                thisListSec.set("data", longs);
                break;
            }
            case NBTTagCompound: {
                NBTCompoundList cList = compound.getCompoundList(key);
                List<Map<String, Object>> compoundItems = new ArrayList<>();
                for (int i = 0; i < cList.size(); i++) {
                    NBTCompound elem = cList.get(i);

                    // Convert sub-compound to a grouped config in a mini-map
                    MemoryConfiguration dummy = new MemorySectionImpl();
                    compoundToGroupedConfig(elem, dummy);

                    compoundItems.add(dummy.getValues(false));
                }
                thisListSec.set("data", compoundItems);
                break;
            }
            default:
                thisListSec.set("data", Collections.singletonList("Unsupported list element: " + elementType));
                break;
        }
    }

    private static String nbtTypeToName(NBTType type) {
        switch (type) {
            case NBTTagString:   return "string";
            case NBTTagInt:      return "int";
            case NBTTagFloat:    return "float";
            case NBTTagDouble:   return "double";
            case NBTTagLong:     return "long";
            case NBTTagCompound: return "compound";
            default:             return "unknown";
        }
    }

    // -------------------------------------------------------------------
    //  READING: (YAML -> NBTCompound)
    // -------------------------------------------------------------------

    public static void groupedConfigToCompound(ConfigurationSection section, NBTCompound compound) {
        if (section == null || compound == null) return;

        loadByteGroup(section.getConfigurationSection("byte"), compound);
        loadShortGroup(section.getConfigurationSection("short"), compound);
        loadIntGroup(section.getConfigurationSection("int"), compound);
        loadLongGroup(section.getConfigurationSection("long"), compound);
        loadFloatGroup(section.getConfigurationSection("float"), compound);
        loadDoubleGroup(section.getConfigurationSection("double"), compound);
        loadStringGroup(section.getConfigurationSection("string"), compound);

        loadByteArrayGroup(section.getConfigurationSection("byteArray"), compound);
        loadIntArrayGroup(section.getConfigurationSection("intArray"), compound);
        loadLongArrayGroup(section.getConfigurationSection("longArray"), compound);

        ConfigurationSection compsSec = section.getConfigurationSection("compounds");
        if (compsSec != null) {
            for (String subKey : compsSec.getKeys(false)) {
                ConfigurationSection nestedSec = compsSec.getConfigurationSection(subKey);
                if (nestedSec == null) continue;
                NBTCompound subComp = compound.addCompound(subKey);
                groupedConfigToCompound(nestedSec, subComp);
            }
        }

        ConfigurationSection listsSec = section.getConfigurationSection("list");
        if (listsSec != null) {
            for (String listKey : listsSec.getKeys(false)) {
                ConfigurationSection oneListSec = listsSec.getConfigurationSection(listKey);
                if (oneListSec == null) continue;
                loadList(oneListSec, listKey, compound);
            }
        }
    }

    private static void loadByteGroup(ConfigurationSection sec, NBTCompound compound) {
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            byte val = (byte) sec.getInt(key);
            compound.setByte(key, val);
        }
    }

    private static void loadShortGroup(ConfigurationSection sec, NBTCompound compound) {
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            short val = (short) sec.getInt(key);
            compound.setShort(key, val);
        }
    }

    private static void loadIntGroup(ConfigurationSection sec, NBTCompound compound) {
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            int val = sec.getInt(key);
            compound.setInteger(key, val);
        }
    }

    private static void loadLongGroup(ConfigurationSection sec, NBTCompound compound) {
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            long val = sec.getLong(key);
            compound.setLong(key, val);
        }
    }

    private static void loadFloatGroup(ConfigurationSection sec, NBTCompound compound) {
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            float val = (float) sec.getDouble(key);
            compound.setFloat(key, val);
        }
    }

    private static void loadDoubleGroup(ConfigurationSection sec, NBTCompound compound) {
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            double val = sec.getDouble(key);
            compound.setDouble(key, val);
        }
    }

    private static void loadStringGroup(ConfigurationSection sec, NBTCompound compound) {
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            String val = sec.getString(key, "");
            compound.setString(key, val);
        }
    }

    private static void loadByteArrayGroup(ConfigurationSection sec, NBTCompound compound) {
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            String base64 = sec.getString(key, "");
            byte[] decoded = Base64.getDecoder().decode(base64);
            compound.setByteArray(key, decoded);
        }
    }

    private static void loadIntArrayGroup(ConfigurationSection sec, NBTCompound compound) {
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            List<Integer> intList = sec.getIntegerList(key);
            int[] arr = intList.stream().mapToInt(Integer::intValue).toArray();
            compound.setIntArray(key, arr);
        }
    }

    private static void loadLongArrayGroup(ConfigurationSection sec, NBTCompound compound) {
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            List<Long> longList = new ArrayList<>();
            for (Object o : sec.getList(key, Collections.emptyList())) {
                if (o instanceof Number) {
                    longList.add(((Number) o).longValue());
                }
            }
            long[] arr = longList.stream().mapToLong(Long::longValue).toArray();
            compound.setLongArray(key, arr);
        }
    }

    private static void loadList(ConfigurationSection listSec, String listKey, NBTCompound compound) {
        String type = listSec.getString("type", "unknown");
        List<?> data = listSec.getList("data", null);
        if (data == null) return;

        switch (type.toLowerCase()) {
            case "string":
                loadStringList(listKey, data, compound);
                break;
            case "int":
                loadIntList(listKey, data, compound);
                break;
            case "float":
                loadFloatList(listKey, data, compound);
                break;
            case "double":
                loadDoubleList(listKey, data, compound);
                break;
            case "long":
                loadLongList(listKey, data, compound);
                break;
            case "compound":
                loadCompoundList(listKey, data, compound);
                break;
            default:
                // unknown, skip
                break;
        }
    }

    private static void loadStringList(String key, List<?> data, NBTCompound compound) {
        NBTList<String> strList = compound.getStringList(key);
        strList.clear();
        for (Object o : data) {
            strList.add(String.valueOf(o));
        }
    }

    private static void loadIntList(String key, List<?> data, NBTCompound compound) {
        NBTList<Integer> iList = compound.getIntegerList(key);
        iList.clear();
        for (Object o : data) {
            if (o instanceof Number) {
                iList.add(((Number) o).intValue());
            } else {
                try {
                    iList.add(Integer.parseInt(o.toString()));
                } catch (Exception ignored) {}
            }
        }
    }

    private static void loadFloatList(String key, List<?> data, NBTCompound compound) {
        NBTList<Float> fList = compound.getFloatList(key);
        fList.clear();
        for (Object o : data) {
            if (o instanceof Number) {
                fList.add(((Number) o).floatValue());
            } else {
                try {
                    fList.add(Float.parseFloat(o.toString()));
                } catch (Exception ignored) {}
            }
        }
    }

    private static void loadDoubleList(String key, List<?> data, NBTCompound compound) {
        NBTList<Double> dList = compound.getDoubleList(key);
        dList.clear();
        for (Object o : data) {
            if (o instanceof Number) {
                dList.add(((Number) o).doubleValue());
            } else {
                try {
                    dList.add(Double.parseDouble(o.toString()));
                } catch (Exception ignored) {}
            }
        }
    }

    private static void loadLongList(String key, List<?> data, NBTCompound compound) {
        NBTList<Long> lList = compound.getLongList(key);
        lList.clear();
        for (Object o : data) {
            if (o instanceof Number) {
                lList.add(((Number) o).longValue());
            } else {
                try {
                    lList.add(Long.parseLong(o.toString()));
                } catch (Exception ignored) {}
            }
        }
    }

    private static void loadCompoundList(String key, List<?> data, NBTCompound compound) {
        NBTCompoundList cList = compound.getCompoundList(key);
        cList.clear();

        for (Object elem : data) {
            // Possibly a Map from the 'dummy.getValues(false)' approach
            if (elem instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> mapElem = (Map<String, Object>) elem;

                NBTCompound sub = cList.addCompound();
                MemoryConfiguration dummy = new MemorySectionImpl(mapElem);
                groupedConfigToCompound(dummy, sub); // All good until this point
            }
            // Possibly a nested MemorySection
            else if (elem instanceof ConfigurationSection) {
                ConfigurationSection cs = (ConfigurationSection) elem;
                NBTCompound sub = cList.addCompound();
                groupedConfigToCompound(cs, sub);
            }
            // else skip or handle differently
        }
    }


    // -------------------------------------------------------------------
    //  A minimal in-memory root Configuration for sub-maps
    // -------------------------------------------------------------------
    private static class MemorySectionImpl extends MemoryConfiguration {

        public MemorySectionImpl() {
            super(); // valid as a root
        }

        public MemorySectionImpl(Map<String, Object> values) {
            super();
            // Manually recurse each entry
            for (Map.Entry<String, Object> e : values.entrySet()) {
                setRecursively(this, e.getKey(), e.getValue());
            }
        }

        /**
         * Recursively sets a key/value pair into the given ConfigurationSection.
         * If 'value' is a Map, we create a child section and populate it.
         * If 'value' is a List, we recursively handle each element in that list.
         */
        private static void setRecursively(ConfigurationSection section, String key, Object value) {
            if (value instanceof Map) {
                // Convert the map into a sub-section
                ConfigurationSection child = section.createSection(key);

                @SuppressWarnings("unchecked")
                Map<String, Object> mapValue = (Map<String, Object>) value;

                for (Map.Entry<String, Object> entry : mapValue.entrySet()) {
                    setRecursively(child, entry.getKey(), entry.getValue());
                }
            }
            else if (value instanceof List) {
                // We might have a list of primitives or a list of more maps/lists
                @SuppressWarnings("unchecked")
                List<Object> listValue = (List<Object>) value;

                List<Object> newList = new ArrayList<>();
                for (Object elem : listValue) {
                    // If the element is itself a map or a list,
                    // we can store it via a mini-subsection approach:
                    if (elem instanceof Map || elem instanceof List) {
                        // Temporarily create a sub MemoryConfiguration
                        MemoryConfiguration temp = new MemoryConfiguration();
                        // We'll store that nested structure at an arbitrary key, e.g. "root"
                        setRecursively(temp, "root", elem);
                        // Now temp contains the nested data. Let's store it as a map:
                        newList.add(temp.getValues(false).get("root"));
                    } else {
                        // It's a primitive => store it directly
                        newList.add(elem);
                    }
                }
                section.set(key, newList);
            }
            else {
                // It's a primitive (String, Number, Boolean, etc.) => store directly
                section.set(key, value);
            }
        }
    }

}
