package dev.splityosis.sysengine.commandlib.helper;

import dev.splityosis.sysengine.commandlib.arguments.CommandArgument;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.utils.ColorUtil;
import org.bukkit.command.CommandSender;

import java.util.*;

public class DefaultHelpMenuCommandHelper extends HelpMenuCommandHelper{

    @Override
    public void sendHeader(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label) {
        commandSender.sendMessage(ColorUtil.colorize("&cInvalid Command Usage."));
        commandSender.sendMessage(ColorUtil.colorize("&c&m       &c Options &c&m       "));
        commandSender.sendMessage(" ");
    }

    @Override
    public void sendFooter(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label) {
        commandSender.sendMessage(" ");
        commandSender.sendMessage(ColorUtil.colorize("&c&m-------------------------".replace("-", " ")));
    }

    @Override
    public void sendHelpLines(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label) {
        Set<Command> allSubCommands = new HashSet<>();
        Set<Command> finalAllSubCommands = allSubCommands;
        command.getSubCommands().values().forEach(map -> finalAllSubCommands.addAll(map.values()));

        if (args.length > 0) {
            String firstArg = args[0];
            Set<Command> filteredSubCommands = new HashSet<>();

            for (Command subCommand : allSubCommands) {
                if (firstArg.equalsIgnoreCase(subCommand.getName())) {
                    filteredSubCommands.add(subCommand);
                }
                else {
                    for (String alias : subCommand.getAliases()) {
                        if (firstArg.equalsIgnoreCase(alias)) {
                            filteredSubCommands.add(subCommand);
                            break;
                        }
                    }
                }
            }

            if (!filteredSubCommands.isEmpty()) {
                allSubCommands = filteredSubCommands;
            }
        }

        Map<String, List<Command>> groupedSubCommands = new HashMap<>();
        for (Command subCommand : allSubCommands) {
            String baseName = subCommand.getName().split(" ")[0];
            groupedSubCommands.computeIfAbsent(baseName, k -> new ArrayList<>()).add(subCommand);
        }

        for (List<Command> group : groupedSubCommands.values()) {
            group.sort(Comparator.comparingInt(cmd -> cmd.getArguments().length + cmd.getOptionalArguments().length));
        }

        String commandPath = getCommandPath(parentCommands, command);
        StringBuilder parentLineBuilder = new StringBuilder("&c* ").append(commandPath).append(" ");

        for (CommandArgument<?> argument : command.getArguments()) {
            parentLineBuilder.append("<").append(argument.getName()).append("> ");
        }

        for (CommandArgument<?> argument : command.getOptionalArguments()) {
            parentLineBuilder.append("[<").append(argument.getName()).append(">] ");
        }

        String parentDescription = command.getDescription().endsWith(".") ? command.getDescription() : command.getDescription() + ".";
        parentLineBuilder.append("- ").append(parentDescription);

        commandSender.sendMessage(ColorUtil.colorize(parentLineBuilder.toString()));

        for (List<Command> group : groupedSubCommands.values()) {
            for (Command subCommand : group) {
                StringBuilder subCommandLine = new StringBuilder("&c* ").append(commandPath).append(" ").append(subCommand.getName()).append(" ");

                for (CommandArgument<?> argument : subCommand.getArguments()) {
                    subCommandLine.append("<").append(argument.getName()).append("> ");
                }

                for (CommandArgument<?> argument : subCommand.getOptionalArguments()) {
                    subCommandLine.append("[<").append(argument.getName()).append(">] ");
                }

                String subDescription = subCommand.getDescription().endsWith(".") ? subCommand.getDescription() : subCommand.getDescription() + ".";
                if (!subDescription.equals(".")) {
                    subCommandLine.append("- ").append(subDescription);
                }

                commandSender.sendMessage(ColorUtil.colorize(subCommandLine.toString()));
            }
        }
    }


    private String getCommandPath(List<Command> parents, Command command) {
        StringBuilder builder = new StringBuilder("/");
        parents.forEach(parent -> builder.append(parent.getName()).append(" "));
        builder.append(command.getName());
        return builder.toString();
    }
}
