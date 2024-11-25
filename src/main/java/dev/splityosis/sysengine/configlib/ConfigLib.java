package dev.splityosis.sysengine.configlib;

import dev.splityosis.sysengine.configlib.manager.DefaultConfigManager;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.MapperRegistry;
import dev.splityosis.sysengine.configlib.mappers.*;

/**
 * The ConfigLib class provides a centralized interface for managing configuration
 * and mappers within the application. It allows for the creation of configuration
 * managers and the registration of mappers.
 */
public class ConfigLib {

    private static boolean initialized = false;

    // A registry to hold all the mappers.
    private static final MapperRegistry mapperRegistry = new MapperRegistry();

    private ConfigLib() {}

    /**
     * Creates a new instance of ConfigManager.
     *
     * @return A new instance of ConfigManager.
     */
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
     * Initializes whatever needs to be initialized for this library.
     * You only need to call this if you are shading in the engine, else this gets called for you.
     */
    public static void initialize(){
        if (initialized) return;
        initialized = true;

        getMapperRegistry().registerMapper(new AWTColorMapper());
        getMapperRegistry().registerMapper(new ColorMapper());
        getMapperRegistry().registerMapper(new InstantMapper());
        getMapperRegistry().registerMapper(new LocalDateMapper());
        getMapperRegistry().registerMapper(new LocalDateTimeMapper());
        getMapperRegistry().registerMapper(new LocalTimeMapper());
        getMapperRegistry().registerMapper(new LocationMapper());
        getMapperRegistry().registerMapper(new PotionEffectMapper());
        getMapperRegistry().registerMapper(new UUIDMapper());
        getMapperRegistry().registerMapper(new VectorMapper());
    }
}