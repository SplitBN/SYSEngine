package dev.splityosis.sysengine;

import de.tr7zw.changeme.nbtapi.NBT;
import dev.splityosis.sysengine.actions.ActionTypeRegistry;
import dev.splityosis.sysengine.commandlib.CommandLib;
import dev.splityosis.sysengine.commandlib.arguments.EnumArgument;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.manager.CommandManager;
import dev.splityosis.sysengine.configlib.ConfigLib;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.guilib.Gui;
import dev.splityosis.sysengine.guilib.GuiItem;
import dev.splityosis.sysengine.guilib.gui.DefaultGui;
import dev.splityosis.sysengine.guilib.item.DefaultGuiItem;
import dev.splityosis.sysengine.guilib.layout.RectangularLayout;
import dev.splityosis.sysengine.guilib.page.ChestPage;
import dev.splityosis.sysengine.guilib.pane.StaticPane;
import dev.splityosis.sysengine.plugin.commands.SYSEngineCommand;
import dev.splityosis.sysengine.scheduling.MissedScheduleStrategy;
import dev.splityosis.sysengine.scheduling.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


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

        commandManager.registerCommands(
                new Command("dvie")
                        .playerExecutes((sender, context) -> {

                            Gui gui = new DefaultGui()

                                    .addPage(new ChestPage(6*9), guiPage -> {
                                        guiPage.setTitle("dddddddd");

                                        guiPage.addPane(new StaticPane(new RectangularLayout(27, 3, 3)), staticPane -> {

                                            staticPane.setItem(0, new DefaultGuiItem(new ItemStack(Material.STONE)));
                                            staticPane.setItem(8, new DefaultGuiItem(new ItemStack(Material.GLASS)));
                                        });

                                        guiPage.addPane(new StaticPane(new RectangularLayout(6, 3, 3)), staticPane -> {
                                            staticPane.setItem(0, new DefaultGuiItem(new ItemStack(Material.RED_BED)));

                                            GuiItem guiItem = new DefaultGuiItem(new ItemStack(Material.DIAMOND_CHESTPLATE));
                                            staticPane.setItem(8, guiItem);

                                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                                guiItem.setItemStack(new ItemStack(Material.BEDROCK));
                                                guiItem.update();
                                            }, 5*20);
                                        });

                                    });

                            gui.getCurrentPage().render();
                            gui.open(sender);

                        })
        );
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
    }

    public static SYSEngine getPlugin() {
        return plugin;
    }
}
