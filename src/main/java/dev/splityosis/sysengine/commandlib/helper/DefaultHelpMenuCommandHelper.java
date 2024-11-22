package dev.splityosis.sysengine.commandlib.helper;

import dev.splityosis.sysengine.commandlib.arguments.CommandArgument;
import dev.splityosis.sysengine.commandlib.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public class DefaultHelpMenuCommandHelper extends HelpMenuCommandHelper{

    @Override
    public void sendHeader(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label) {
        commandSender.sendMessage("=== Help Menu ===");
        commandSender.sendMessage(" ");
    }

    @Override
    public void sendFooter(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label) {
        commandSender.sendMessage(" ");
        commandSender.sendMessage("=== End of Help Menu ===");
    }

    @Override
    public void sendHelpLines(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label) {
        Set<Command> subcommands = new HashSet<>();
        command.getSubCommands().values().forEach(map -> subcommands.addAll(map.values()));

        if (args.length > 0)
            out: for (Command subCommand : new ArrayList<>(subcommands)) {
                if (args[0].equalsIgnoreCase(subCommand.getName())) continue;
                for (String alias : subCommand.getAliases())
                    if (args[0].equalsIgnoreCase(alias))
                        continue out;
                subcommands.remove(subCommand);
            }

        if (subcommands.isEmpty())
            command.getSubCommands().values().forEach(map -> subcommands.addAll(map.values()));

        String path = getCommandPath(parentCommands, command);
        for (Command subcommand : subcommands) {
            StringBuilder lineBuilder = new StringBuilder("* " + path + " " + subcommand.getName() + " ");

            // Do args
            for (CommandArgument<?> argument : subcommand.getArguments())
                lineBuilder.append("<").append(argument.getName()).append("> ");

            for (CommandArgument<?> argument : subcommand.getOptionalArguments())
                lineBuilder.append("[<").append(argument.getName()).append(">] ");

            String desc = subcommand.getDescription().endsWith(".") ? subcommand.getDescription() : subcommand.getDescription() + ".";

            lineBuilder.append("- ").append(desc);
            commandSender.sendMessage(lineBuilder.toString());
        }
    }

    private String getCommandPath(List<Command> parents, Command command) {
        StringBuilder builder = new StringBuilder("/");
        parents.forEach(parent -> builder.append(parent.getName()).append(" "));
        builder.append(command.getName());
        return builder.toString();
    }
}
