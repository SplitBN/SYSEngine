package dev.splityosis.sysengine;

import dev.splityosis.sysengine.configlib.ConfigLib;
import org.bukkit.plugin.java.JavaPlugin;

public final class SYSEngine extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigLib.initialize();
    }



    @Override
    public void onDisable() {

    }

}
