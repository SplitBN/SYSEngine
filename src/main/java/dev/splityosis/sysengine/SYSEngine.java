package dev.splityosis.sysengine;

import de.tr7zw.changeme.nbtapi.NBT;
import dev.splityosis.sysengine.actions.ActionTypeRegistry;
import dev.splityosis.sysengine.commandlib.CommandLib;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.manager.CommandManager;
import dev.splityosis.sysengine.configlib.ConfigLib;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.guilib.components.GuiInteraction;
import dev.splityosis.sysengine.guilib.GuiLib;
import dev.splityosis.sysengine.guilib.gui.DefaultGui;
import dev.splityosis.sysengine.guilib.item.DefaultGuiItem;
import dev.splityosis.sysengine.guilib.layout.BorderLayout;
import dev.splityosis.sysengine.guilib.layout.BoxLayout;
import dev.splityosis.sysengine.guilib.page.ChestPage;
import dev.splityosis.sysengine.guilib.pane.StaticPane;
import dev.splityosis.sysengine.plugin.commands.SYSEngineCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public final class SYSEngine extends JavaPlugin {

    private static SYSEngine plugin;
    private static boolean isInitialized = false;

    private CommandManager commandManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        plugin = this;
        initialize(this);

        commandManager = CommandLib.createCommandManager(this);
        configManager = ConfigLib.createConfigManager(this);

        commandManager.registerCommands(new SYSEngineCommand());

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

        if (!NBT.preloadApi()) {
            plugin.getLogger().warning("NBT-API wasn't initialized properly, disabling the plugin");
            plugin.getPluginLoader().disablePlugin(plugin);
            return;
        }

        ConfigLib.initialize();
        ActionTypeRegistry.initialize();
        GuiLib.initialize(plugin);
    }

    public static SYSEngine getPlugin() {
        return plugin;
    }
}
