package dev.splityosis.sysengine;

import de.tr7zw.changeme.nbtapi.NBT;
import dev.splityosis.sysengine.actions.ActionTypeRegistry;
import dev.splityosis.sysengine.commandlib.CommandLib;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.manager.CommandManager;
import dev.splityosis.sysengine.configlib.ConfigLib;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.plugin.commands.SYSEngineCommand;
import dev.splityosis.sysengine.scheduling.MissedScheduleStrategy;
import dev.splityosis.sysengine.scheduling.scheduler.Scheduler;
import org.bukkit.Bukkit;
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

        Test test = new Test();
        try {
            configManager.registerConfig(test, new File(getDataFolder(), "test.yml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Scheduler scheduler =
                Scheduler.create(this)
                .executes(scheduledContext -> {
                    // This block runs when the scheduler is triggered

                    String identifier = scheduledContext.getTaskIdentifier(); // This is null if the schedule doesn't use identifiers
                });

        File dataFile = new File(getDataFolder(), "scheduler-data.yml");
        scheduler.enableMissedSchedules(dataFile, MissedScheduleStrategy.CALL_ALL);

        // On load and on reload to set the new schedule
        scheduler.setSchedule(test.dataSchedule);

        scheduler.enable();

        commandManager.registerCommands(
                new Command("reloadschedule")
                        .executes((sender, context) -> {
                            try {
                                configManager.reload(test);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            scheduler.setSchedule(test.dataSchedule);
                            noDataScheduler.setSchedule(test.noDataSchedule);
                        }),

                new Command("logschedules")
                        .executes((sender, context) -> {
                            Bukkit.broadcastMessage("with data: " + test.dataSchedule);
                            Bukkit.broadcastMessage("without data: " + test.noDataSchedule);
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
