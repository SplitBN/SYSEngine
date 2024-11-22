package dev.splityosis.sysengine.commandlib;

import dev.splityosis.sysengine.commandlib.helper.CommandHelpProvider;
import dev.splityosis.sysengine.commandlib.helper.DefaultHelpMenuCommandHelper;
import dev.splityosis.sysengine.commandlib.manager.CommandManager;
import dev.splityosis.sysengine.commandlib.manager.DefaultCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandLib {

    public static CommandManager createCommandManager(JavaPlugin plugin) {
        return new DefaultCommandManager(plugin, new DefaultHelpMenuCommandHelper());
    }

    public static CommandManager createCommandManager(JavaPlugin plugin, CommandHelpProvider commandHelpProvider) {
        return new DefaultCommandManager(plugin, commandHelpProvider);
    }
}
