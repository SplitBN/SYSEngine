package dev.splityosis.sysengine;

import de.tr7zw.changeme.nbtapi.NBT;
import dev.splityosis.sysengine.actions.ActionTypeRegistry;
import dev.splityosis.sysengine.commandlib.CommandLib;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.manager.CommandManager;
import dev.splityosis.sysengine.configlib.ConfigLib;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.guilib.components.Gui;
import dev.splityosis.sysengine.guilib.GuiLib;
import dev.splityosis.sysengine.guilib.gui.DefaultGui;
import dev.splityosis.sysengine.guilib.item.DefaultGuiItem;
import dev.splityosis.sysengine.guilib.layout.BoxLayout;
import dev.splityosis.sysengine.guilib.layout.FullLayout;
import dev.splityosis.sysengine.guilib.pane.HorizontalScrollPane;
import dev.splityosis.sysengine.guilib.pane.PaginatedPane;
import dev.splityosis.sysengine.guilib.pane.StaticPane;
import dev.splityosis.sysengine.guilib.pane.VerticalScrollPane;
import dev.splityosis.sysengine.plugin.commands.SYSEngineCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;


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

                            int ROWS    = 5;
                            int COLS    = 9;
                            int THICK   = 3;
                            int MID_ROW = ROWS / 2;  // 2
                            int MID_COL = COLS / 2;  // 4

// Horizontal bar: spans all 9 columns, 3 rows thick, centered at row 2 → rows 1,2,3
                            HorizontalScrollPane hPane = new HorizontalScrollPane(
                                    7, 3,
                                    new BoxLayout(10, 7, 3)
                            );

// Vertical bar: spans all 5 rows, 3 columns thick, centered at col 4 → cols 3,4,5
                            VerticalScrollPane vPane = new VerticalScrollPane(
                                    3, 3,
                                    new BoxLayout(12, 3, 3)
                            );

// Two distinct pools of materials
                            Material[] hMats = {
                                    Material.WHITE_CONCRETE, Material.QUARTZ, Material.QUARTZ_BLOCK, Material.DIAMOND
                            };
                            Material[] vMats = {
                                    Material.OBSIDIAN, Material.CRYING_OBSIDIAN, Material.COAL, Material.BLACK_CONCRETE
                            };

                            new DefaultGui()
                                    .addPage("Thick Scroll + Test", ROWS, guiPage -> {
                                        Random rnd = new Random();

                                        // Populate horizontal pane (e.g. 20 random columns × 3 rows)
                                        for (int col = 0; col < 20; col++) {
                                            for (int row = 0; row < THICK; row++) {
                                                Material mat = hMats[rnd.nextInt(hMats.length)];
                                                ItemStack item = new ItemStack(mat);
                                                ItemMeta meta = item.getItemMeta();
                                                meta.setDisplayName("H–" + col + "," + row);
                                                item.setItemMeta(meta);
                                                hPane.setItem(col, row, new DefaultGuiItem(item));
                                            }
                                        }

                                        // Populate vertical pane (e.g. 15 random rows × 3 cols)
                                        for (int row = 0; row < 15; row++) {
                                            for (int col = 0; col < THICK; col++) {
                                                Material mat = vMats[rnd.nextInt(vMats.length)];
                                                ItemStack item = new ItemStack(mat);
                                                ItemMeta meta = item.getItemMeta();
                                                meta.setDisplayName("V–" + row + "," + col);
                                                item.setItemMeta(meta);
                                                vPane.setItem(row, col, new DefaultGuiItem(item));
                                            }
                                        }

                                        // Add both scroll panes
                                        guiPage.addPane(hPane, pane -> { /* no extra items here */ });
                                        guiPage.addPane(vPane, pane -> { /* no extra items here */ });

                                        // Overlay a StaticPane to place the control arrows on top
                                        guiPage.addPane(new StaticPane(new FullLayout()), pane -> {
                                            // ↑ at top-center of vertical bar: slot = 0*9 + MID_COL
                                            pane.setItem(MID_COL, new DefaultGuiItem(new ItemStack(Material.ARROW))
                                                    .onClick(evt -> {
                                                        if (vPane.getScrollOffset() == vPane.getMinRow()) {
                                                            evt.getPlayer().sendMessage("Already at top!");
                                                            return;
                                                        }
                                                        vPane.scrollUp(1).refresh();
                                                    })
                                            );

                                            // ↓ at bottom-center of vertical bar: slot = (ROWS-1)*9 + MID_COL
                                            pane.setItem((ROWS - 1) * COLS + MID_COL, new DefaultGuiItem(new ItemStack(Material.ARROW))
                                                    .onClick(evt -> {
                                                        if (vPane.getScrollOffset() == vPane.getMaxRow()) {
                                                            evt.getPlayer().sendMessage("Already at bottom!");
                                                            return;
                                                        }
                                                        vPane.scrollDown(1).refresh();
                                                    })
                                            );

                                            // ← at middle-left of horizontal bar: slot = MID_ROW*9 + 0
                                            pane.setItem(MID_ROW * COLS, new DefaultGuiItem(new ItemStack(Material.ARROW))
                                                    .onClick(evt -> {
                                                        if (hPane.getScrollOffset() == hPane.getMinColumn()) {
                                                            evt.getPlayer().sendMessage("Already at leftmost!");
                                                            return;
                                                        }
                                                        hPane.scrollLeft(1).refresh();
                                                    })
                                            );

                                            // → at middle-right of horizontal bar: slot = MID_ROW*9 + (COLS-1)
                                            pane.setItem(MID_ROW * COLS + (COLS - 1), new DefaultGuiItem(new ItemStack(Material.ARROW))
                                                    .onClick(evt -> {
                                                        if (hPane.getScrollOffset() == hPane.getMaxColumn()) {
                                                            evt.getPlayer().sendMessage("Already at rightmost!");
                                                            return;
                                                        }
                                                        hPane.scrollRight(1).refresh();
                                                    })
                                            );
                                        });
                                    })
                                    .open(sender);



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
