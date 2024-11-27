package dev.splityosis.sysengine;

import de.tr7zw.changeme.nbtapi.NBT;
import dev.splityosis.sysengine.configlib.ConfigLib;
import org.bukkit.plugin.java.JavaPlugin;

public final class SYSEngine extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin");
            getPluginLoader().disablePlugin(this);
            return;
        }

        ConfigLib.initialize();
    }



    @Override
    public void onDisable() {

    }

}
