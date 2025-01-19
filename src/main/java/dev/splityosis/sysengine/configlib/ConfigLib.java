package dev.splityosis.sysengine.configlib;

import dev.splityosis.sysengine.SYSEngine;
import dev.splityosis.sysengine.configlib.manager.DefaultConfigManager;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.MapperRegistry;
import dev.splityosis.sysengine.configlib.mappers.*;
import dev.splityosis.sysengine.utils.VersionUtil;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The ConfigLib class provides a centralized interface for managing configuration
 * and mappers within the application. It allows for the creation of configuration
 * managers and the registration of mappers.
 */
public class ConfigLib {

    private static boolean isInitialized = false;

    // A registry to hold all the mappers.
    private static final MapperRegistry mapperRegistry = new MapperRegistry();

    private ConfigLib() {}

    /**
     * Creates a new instance of ConfigManager.
     *
     * @return A new instance of ConfigManager.
     */
    public static ConfigManager createConfigManager(JavaPlugin plugin) {
        return new DefaultConfigManager();
    }

    /**
     * Creates a new instance of ConfigManager.
     *
     * @return A new instance of ConfigManager.
     */
    @Deprecated(forRemoval = true)
    public static ConfigManager createConfigManager() {
        return new DefaultConfigManager();
    }

    /**
     * Retrieves the MapperRegistry instance, which holds all registered mappers.
     *
     * @return The MapperRegistry instance.
     */
    public static MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    /**
     * Registers a mapper with the MapperRegistry.
     *
     * <p>This method delegates to {@link MapperRegistry#registerMapper(AbstractMapper)}.</p>
     *
     * @param mapper The mapper to register.
     */
    public static void registerMapper(AbstractMapper<?> mapper) {
        mapperRegistry.registerMapper(mapper);
    }

    /**
     * Registers a mapper with the MapperRegistry using a specific identifier.
     *
     * <p>This method delegates to {@link MapperRegistry#registerMapper(AbstractMapper, String)}.</p>
     *
     * @param mapper     The mapper to register.
     * @param identifier The identifier to associate with the mapper.
     */
    public static void registerMapper(AbstractMapper<?> mapper, String identifier) {
        mapperRegistry.registerMapper(mapper, identifier);
    }

    /**
     * Initializes whatever needs to be initialized for ConfigLib.
     * You should never call this, look at {@link SYSEngine#initialize(JavaPlugin)}.
     */
    public static void initialize(){
        if (isInitialized) return;
        isInitialized = true;

        getMapperRegistry().registerMappers(
                new AWTColorMapper(),
                new ColorMapper(),
                new InstantMapper(),
                new LocalDateMapper(),
                new LocalDateTimeMapper(),
                new LocalTimeMapper(),
                new LocationMapper(),
                new PotionEffectMapper(),
                new UUIDMapper(),
                new VectorMapper(),
                new XMaterialMapper(),
                new XEnchantmentMapper(),
                new ItemStackMapper(),
                new ActionsMapper(),
                new NBTCompoundMapper(),
                new XPotionMapper(),
                new PotionPropertiesMapper()
        );

        if (VersionUtil.isServerAtLeast("1.11"))
            getMapperRegistry().registerMappers(
                    new PotionDataMapper()
            );
    }
}