package dev.splityosis.sysengine.commandlib.helper;

import dev.splityosis.sysengine.commandlib.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface CommandHelpProvider {

    /**
     * Sends help to the specified CommandSender based on the command context.
     *
     * @param commandSender     the entity requesting help.
     * @param parentCommands    the chain of commands used to get to this command.
     * @param command           the command for which help is being requested.
     * @param args              the raw arguments input by the command sender.
     * @param label             the entire raw form of the command entered by the sender.
     */
    void sendHelp(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label);
}