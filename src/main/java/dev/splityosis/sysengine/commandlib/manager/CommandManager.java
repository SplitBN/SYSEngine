package dev.splityosis.sysengine.commandlib.manager;

import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.helper.CommandHelpProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public interface CommandManager {

    JavaPlugin getPlugin();

    void registerCommand(Command... commands);

    void unregisterCommand(Command... commands);

    void process(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label);

    List<String> processTabComplete(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label);

    CommandHelpProvider getCommandHelpProvider();
}
