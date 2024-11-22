package dev.splityosis.sysengine.commandlib;

import dev.splityosis.sysengine.commandlib.helper.CommandHelpProvider;
import dev.splityosis.sysengine.commandlib.helper.DefaultHelpMenuCommandHelper;
import dev.splityosis.sysengine.commandlib.manager.CommandManager;
import dev.splityosis.sysengine.commandlib.manager.DefaultCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main API class for the command library.
 * This class provides utility methods for creating and managing command-related functionality
 * within a Bukkit/Spigot plugin. Additional methods may be added in the future to enhance
 * the capabilities of this library.
 */
public class CommandLib {

    /**
     * Creates a new instance of {@link CommandManager} using the default help menu command helper.
     *
     * @param plugin the {@link JavaPlugin} instance to associate with the command manager
     * @return a new instance of {@link CommandManager}
     */
    public static CommandManager createCommandManager(JavaPlugin plugin) {
        return new DefaultCommandManager(plugin, new DefaultHelpMenuCommandHelper());
    }

    /**
     * Creates a new instance of {@link CommandManager} using a custom command help provider.
     *
     * @param plugin the {@link JavaPlugin} instance to associate with the command manager
     * @param commandHelpProvider the custom command help provider to use
     * @return a new instance of {@link CommandManager}
     */
    public static CommandManager createCommandManager(JavaPlugin plugin, CommandHelpProvider commandHelpProvider) {
        return new DefaultCommandManager(plugin, commandHelpProvider);
    }
}
