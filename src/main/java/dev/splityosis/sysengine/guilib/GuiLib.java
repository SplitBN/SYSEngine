package dev.splityosis.sysengine.guilib;

import dev.splityosis.sysengine.SYSEngine;
import org.bukkit.plugin.java.JavaPlugin;

public class GuiLib {

    // TODO make VerticalScrollPane, HorizontalScrollPane, PaginatedPane
    // TODO make guiitem builder and gui builders and look into fillers
    // TODO redo interactions

    private static boolean isInitialized = false;

    /**
     * Initializes whatever needs to be initialized for GuiLib.
     * You should never call this, look at {@link SYSEngine#initialize(JavaPlugin)}.
     */
    public static void initialize(JavaPlugin plugin) {
        if (isInitialized) return;
        isInitialized = true;

        plugin.getServer().getPluginManager().registerEvents(new GuiLibListener(), plugin);
    }

    public static boolean isIsInitialized() {
        return isInitialized;
    }
}
