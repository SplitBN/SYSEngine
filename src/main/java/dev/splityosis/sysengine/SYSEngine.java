package dev.splityosis.sysengine;

import de.tr7zw.changeme.nbtapi.NBT;
import dev.splityosis.sysengine.actions.ActionTypeRegistry;
import dev.splityosis.sysengine.commandlib.CommandLib;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.manager.CommandManager;
import dev.splityosis.sysengine.configlib.ConfigLib;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.guilib.components.Gui;
import dev.splityosis.sysengine.guilib.components.GuiItem;
import dev.splityosis.sysengine.guilib.GuiLib;
import dev.splityosis.sysengine.guilib.gui.DefaultGui;
import dev.splityosis.sysengine.guilib.item.DefaultGuiItem;
import dev.splityosis.sysengine.guilib.layout.RectangularLayout;
import dev.splityosis.sysengine.guilib.page.ChestPage;
import dev.splityosis.sysengine.guilib.pane.StaticPane;
import dev.splityosis.sysengine.plugin.commands.SYSEngineCommand;
import org.bukkit.Bukkit;
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

        commandManager.registerCommands(
                new Command("dvie")
                        .playerExecutes((sender, context) -> {

                            Gui gui = new DefaultGui()
                                    // GUI‐level open/close logging
                                    .onOpen(evt -> {
                                        Player p = evt.getPlayer();
                                        p.sendMessage("[GuiOpen] Gui opened for " + p.getName());
                                        getLogger().info("[GuiOpen] Gui opened by " + p.getName());
                                    })
                                    .onClose(evt -> {
                                        Player p = evt.getPlayer();
                                        p.sendMessage("[GuiClose] Gui closed for " + p.getName());
                                        getLogger().info("[GuiClose] Gui closed by " + p.getName());
                                    })

                                    // Add a single page: 6×9 chest
                                    .addPage(new ChestPage(6 * 9), page -> {
                                        // Page‐level open/close logging
                                        page.onOpen(evt -> {
                                            Player p = evt.getPlayer();
                                            p.sendMessage("[PageOpen] “" + page.getTitle() + "” opened");
                                            getLogger().info("[PageOpen] Page opened: " + page.getTitle());
                                        });
                                        page.onClose(evt -> {
                                            Player p = evt.getPlayer();
                                            p.sendMessage("[PageClose] “" + page.getTitle() + "” closed");
                                            getLogger().info("[PageClose] Page closed: " + page.getTitle());
                                        });

                                        page.onClick(evt -> {
                                            Player p = evt.getPlayer();
                                            p.sendMessage("[PageClick] Page clicked at raw =" + evt.getSlot());
                                            getLogger().info("[PageClick] Page click on slot " + evt.getSlot());
                                        });

                                        page.setTitle("All‐Events Test Page");

                                        // Pane #1: Static 3×3 at raw slot 27
                                        page.addPane(new StaticPane(new RectangularLayout(27, 3, 3)), pane -> {
                                            // Pane‐level open/close/click logging
                                            pane.onOpen(evt -> {
                                                Player p = evt.getPlayer();
                                                p.sendMessage("[PaneOpen] Pane at layout(27,3×3) opened");
                                                getLogger().info("[PaneOpen] Pane opened: " + pane);
                                            });
                                            pane.onClose(evt -> {
                                                Player p = evt.getPlayer();
                                                p.sendMessage("[PaneClose] Pane at layout(27,3×3) closed");
                                                getLogger().info("[PaneClose] Pane closed: " + pane);
                                            });
                                            pane.onClick(evt -> {
                                                Player p = evt.getPlayer();
                                                p.sendMessage("[PaneClick] Pane clicked at local=" + evt.getLocalSlot()
                                                        + ", raw=" + evt.getRawSlot());
                                                getLogger().info("[PaneClick] Pane clicked: local=" +
                                                        evt.getLocalSlot() + ", raw=" + evt.getRawSlot());
                                            });

                                            // Put an item at local=0 (raw = 27)
                                            pane.setItem(0,
                                                    new DefaultGuiItem(new ItemStack(Material.STONE))
                                                            .onClick(evt -> {
                                                                Player p = evt.getPlayer();
                                                                p.sendMessage("[ItemClick] STONE clicked at raw="
                                                                        + evt.getRawSlot() + ", local=" + evt.getLocalSlot());
                                                                getLogger().info("[ItemClick] GuiItem STONE clicked by " + p.getName());
                                                            })
                                            );

                                            // Put an item at local=8 (raw = 35)
                                            pane.setItem(8,
                                                    new DefaultGuiItem(new ItemStack(Material.GOLD_INGOT))
                                                            .onClick(evt -> {
                                                                Player p = evt.getPlayer();
                                                                p.sendMessage("[ItemClick] GOLD clicked at raw="
                                                                        + evt.getRawSlot() + ", local=" + evt.getLocalSlot());
                                                                getLogger().info("[ItemClick] GuiItem GOLD clicked by " + p.getName());
                                                            })
                                            );
                                        });

                                        // Pane #2: Static 3×3 at raw slot 6
                                        page.addPane(new StaticPane(new RectangularLayout(6, 3, 3)), pane -> {
                                            pane.onOpen(evt -> {
                                                Player p = evt.getPlayer();
                                                p.sendMessage("[PaneOpen] Second pane at layout(6,3×3) opened");
                                                getLogger().info("[PaneOpen] Pane2 opened");
                                            });
                                            pane.onClose(evt -> {
                                                Player p = evt.getPlayer();
                                                p.sendMessage("[PaneClose] Second pane at layout(6,3×3) closed");
                                                getLogger().info("[PaneClose] Pane2 closed");
                                            });
                                            pane.onClick(evt -> {
                                                Player p = evt.getPlayer();
                                                p.sendMessage("[PaneClick] Second pane clicked at local=" + evt.getLocalSlot()
                                                        + ", raw=" + evt.getRawSlot());
                                                getLogger().info("[PaneClick] Pane2 clicked: local=" + evt.getLocalSlot());
                                            });

                                            // One diamond chestplate that changes to bedrock 5s later
                                            GuiItem changingItem = new DefaultGuiItem(new ItemStack(Material.DIAMOND_CHESTPLATE))
                                                    .onClick(evt -> {
                                                        Player p = evt.getPlayer();
                                                        p.sendMessage("[ItemClick] DIAMOND CHESTPLATE clicked at raw="
                                                                + evt.getRawSlot() + ", local=" + evt.getLocalSlot());
                                                        getLogger().info("[ItemClick] GuiItem CHESTPLATE clicked");
                                                    });
                                            pane.setItem(0, changingItem);

                                            // Schedule an update
                                            Bukkit.getScheduler().runTaskLater(this, () -> {
                                                changingItem.setItemStack(new ItemStack(Material.BEDROCK));
                                                changingItem.update();  // redraw that one slot
                                                // Log the update
                                                getLogger().info("[ItemUpdate] GuiItem changed to BEDROCK at raw="
                                                        + pane.getLayout().toRawSlot(0).orElse(-1));
                                            }, 5 * 20);
                                        });
                                    });

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
        GuiLib.initialize(plugin);
    }

    public static SYSEngine getPlugin() {
        return plugin;
    }
}
