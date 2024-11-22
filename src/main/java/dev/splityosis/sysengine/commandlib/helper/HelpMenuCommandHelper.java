package dev.splityosis.sysengine.commandlib.helper;

import dev.splityosis.sysengine.commandlib.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class HelpMenuCommandHelper implements CommandHelpProvider {

    public abstract void sendHeader(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label);

    public abstract void sendFooter(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label);

    public abstract void sendHelpLines(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label);

    @Override
    public void sendHelp(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label) {
        sendHeader(commandSender, parentCommands, command, args, label);
        sendHelpLines(commandSender, parentCommands, command, args, label);
        sendFooter(commandSender, parentCommands, command, args, label);
    }
}
