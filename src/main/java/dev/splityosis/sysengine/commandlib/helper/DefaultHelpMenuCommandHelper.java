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
        Set<Command> tempSubcommands = new HashSet<>();
        command.getSubCommands().values().forEach(map -> tempSubcommands.addAll(map.values()));

        Set<Command> subcommands = new HashSet<>(tempSubcommands);

        if (args.length > 0) {
            String firstArg = args[0];
            List<Command> filteredSubcommands = new ArrayList<>();
            for (Command subCommand : subcommands) {
                if (firstArg.equalsIgnoreCase(subCommand.getName())) {
                    filteredSubcommands.add(subCommand);
                } else {
                    for (String alias : subCommand.getAliases()) {
                        if (firstArg.equalsIgnoreCase(alias)) {
                            filteredSubcommands.add(subCommand);
                            break;
                        }
                    }
                }
            }
            if (!filteredSubcommands.isEmpty()) {
                subcommands = new HashSet<>(filteredSubcommands);
            }
        }

        if (subcommands.isEmpty() && command.getSubCommands().size() > 0) {
            Set<Command> additionalSubcommands = new HashSet<>();
            command.getSubCommands().values().forEach(map -> additionalSubcommands.addAll(map.values()));
            subcommands.addAll(additionalSubcommands);
        }

        Map<String, List<Command>> groupedSubcommands = new HashMap<>();
        for (Command subCommand : subcommands) {
            String baseName = subCommand.getName().split(" ")[0];
            groupedSubcommands.computeIfAbsent(baseName, k -> new ArrayList<>()).add(subCommand);
        }

        for (List<Command> group : groupedSubcommands.values()) {
            group.sort(Comparator.comparingInt(command1 -> command1.getArguments().length + command1.getOptionalArguments().length));
        }

        String path = getCommandPath(parentCommands, command);

        String parentDesc = command.getDescription().endsWith(".") ? command.getDescription() : command.getDescription() + ".";
        String parentLine = "&c* " + path + " - " + parentDesc;
        commandSender.sendMessage(ColorUtil.colorize(parentLine));

        for (List<Command> group : groupedSubcommands.values()) {
            for (Command subCommand : group) {
                StringBuilder lineBuilder = new StringBuilder("&c* ").append(path).append(" ").append(subCommand.getName()).append(" ");

                for (CommandArgument<?> argument : subCommand.getArguments()) {
                    lineBuilder.append("<").append(argument.getName()).append("> ");
                }

                for (CommandArgument<?> argument : subCommand.getOptionalArguments()) {
                    lineBuilder.append("[<").append(argument.getName()).append(">] ");
                }

                String desc = subCommand.getDescription().endsWith(".") ? subCommand.getDescription() : subCommand.getDescription() + ".";
                if (!desc.equals(".")) {
                    lineBuilder.append("- ").append(desc);
                }

                commandSender.sendMessage(ColorUtil.colorize(lineBuilder.toString()));
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
