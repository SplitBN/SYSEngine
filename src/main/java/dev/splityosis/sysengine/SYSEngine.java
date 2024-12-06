package dev.splityosis.sysengine;

import de.tr7zw.changeme.nbtapi.NBT;
import dev.splityosis.sysengine.configlib.ConfigLib;
import org.bukkit.plugin.java.JavaPlugin;

public final class SYSEngine extends JavaPlugin {

    private static boolean isInitialized = false;

    @Override
    public void onEnable() {
        initialize(this);
    }



    @Override
    public void onDisable() {

    }

    /**
     * Initializes the libraries and whatever needs to be initialized for this engine.
     * You only need to call this if you are shading in the engine, else this gets called for you.
     *
     * @param plugin The plugin initializing the libraries
     * @Note This disables the plugin if it fails
     */
    public static void initialize(JavaPlugin plugin) {
        if (isInitialized) return;
        isInitialized = true;

        // Removed since reverting to older version.
//        if (!NBT.preloadApi()) {
//            plugin.getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin");
//            plugin.getPluginLoader().disablePlugin(plugin);
//            return;
//        }

        ConfigLib.initialize();
    }

}
