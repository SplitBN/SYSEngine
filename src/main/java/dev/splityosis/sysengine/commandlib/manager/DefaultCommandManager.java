package dev.splityosis.sysengine.commandlib.manager;

import dev.splityosis.sysengine.commandlib.arguments.CommandArgument;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.command.CommandContext;
import dev.splityosis.sysengine.commandlib.command.RawCommandContext;
import dev.splityosis.sysengine.commandlib.conditions.CommandCondition;
import dev.splityosis.sysengine.commandlib.helper.CommandHelpProvider;
import dev.splityosis.sysengine.commandlib.exception.InvalidInputException;
import dev.splityosis.sysengine.commandlib.consumers.CommandConsumer;
import dev.splityosis.sysengine.utils.CommandUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class DefaultCommandManager implements CommandManager {

    private CommandHelpProvider commandHelpProvider;
    private JavaPlugin plugin;

    public DefaultCommandManager(JavaPlugin plugin, CommandHelpProvider commandHelpProvider) {
        this.plugin = plugin;
        this.commandHelpProvider = commandHelpProvider;
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void registerCommand(Command... commands) {
        for (Command command : commands) {
            PluginCommand pluginCommand = CommandUtil.createPluginCommand(command.getName(), getPlugin());
            if (pluginCommand == null)
                throw new RuntimeException("Couldn't create PluginCommand.");

            pluginCommand.setAliases(Arrays.asList(command.getAliases()));
            pluginCommand.setPermission(command.getPermission());

            pluginCommand.setExecutor((sender, command1, label, args) -> {
                process(sender, new ArrayList<>(), command, args, label);
                return true;
            });

            pluginCommand.setTabCompleter((sender, command1, label, args) -> processTabComplete(sender, new ArrayList<>(), command, args, label));

//             pluginCommand.unregister(CommandUtil.getCommandMap());
            pluginCommand.register(CommandUtil.getCommandMap());
            CommandUtil.getCommandMap().register(command.getName(), pluginCommand);
        }
    }

    @Override
    public void unregisterCommand(Command... commands) {

    }

    @Override
    public CommandHelpProvider getCommandHelpProvider() {
        return commandHelpProvider;
    }

    @Override
    public void process(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label) {
        if (parentCommands == null)
            parentCommands = new ArrayList<>();

        // Check permission
        if (command.getPermission() != null && !commandSender.hasPermission(command.getPermission())) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return;
        }

        // Check for sub commands
        if (args.length > 0) {
            Command subcommand = command.getSubCommands().getOrDefault(args[0], new HashMap<>()).get(args.length - 1);
            if (subcommand != null) {
                parentCommands.add(command);
                process(commandSender, parentCommands, subcommand, Arrays.copyOfRange(args, 1, args.length), label);
                return;
            }
        }

        // Check if the command is executable
        if (command.getCommandConsumer() == null && command.getPlayerCommandConsumer() == null) {
            getCommandHelpProvider().sendHelp(commandSender, parentCommands, command, args, label);
            return;
        }

        // Get the right executor
        CommandConsumer commandExecutor = null;
        if (commandSender instanceof Player) {
            commandExecutor = command.getPlayerCommandConsumer();
            if (commandExecutor == null)
                commandExecutor = command.getCommandConsumer();
        } else {
            commandExecutor = command.getCommandConsumer();
            if (commandExecutor == null) {
                commandSender.sendMessage(ChatColor.RED + "Only players can run this command.");
                return;
            }
        }

        // Try to parse context and execute command
        LinkedHashMap<String, String> rawArgs = new LinkedHashMap<>();
        LinkedHashMap<String, Object> parsedArgs = new LinkedHashMap<>();

        int minArgs = command.getArguments().length;
        int maxArgs = minArgs + command.getOptionalArguments().length;

        if (args.length < minArgs || args.length > maxArgs) {
            getCommandHelpProvider().sendHelp(commandSender, parentCommands, command, args, label);
            return;
        }

        List<CommandArgument<?>> commandArgumentList = new ArrayList<>(Arrays.asList(command.getArguments()));
        commandArgumentList.addAll(Arrays.asList(command.getOptionalArguments()));

        CommandContext context = new CommandContext(label, parentCommands, rawArgs, parsedArgs);

        // Put raw args before parsing so more info is passed to argument.parse()
        for (int i = 0; i < args.length; i++) {
            CommandArgument<?> argument = commandArgumentList.get(i);
            rawArgs.put(argument.getName(), args[i]);
            context.update();
        }

        for (int i = 0; i < args.length; i++) {
            CommandArgument<?> argument = commandArgumentList.get(i);
            try {
                parsedArgs.put(argument.getName(), argument.parse(commandSender, args[i], command, i, context));
                context.update();
            } catch (InvalidInputException e) {
                argument.onInvalidInput(commandSender, args[i], command, i, context);
                return;
            }
        }

        // Check conditions
        for (CommandCondition condition : command.getConditions()) {
            if (!condition.isMet(commandSender, command, context)) {
                condition.onNotMet(commandSender, command, context);
                return;
            }
        }

        try {
            commandExecutor.execute(commandSender, context);
        } catch (Exception e) {
            commandSender.sendMessage(ChatColor.RED + "An internal error occurred while processing the command.");
            e.printStackTrace();
        }
    }

    @Override
    public List<String> processTabComplete(CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label) {
        return processTabComplete(new HashSet<>(), commandSender, parentCommands, command, args, label);
    }

    public List<String> processTabComplete(Set<Class<?>> processedArgumentsClasses, CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label) {

        // Check permission
        if (command.getPermission() != null && !commandSender.hasPermission(command.getPermission()))
            return Collections.emptyList();

        // get suggestions from THIS command
        List<String> tabCompletions = new ArrayList<>(getCommandTabCompletions(processedArgumentsClasses, commandSender, parentCommands, command, args, label));

        // process subcommands
        if (args.length > 1) {
            for (Command subCommand : new HashSet<>(command.getSubCommands().getOrDefault(args[0], new HashMap<>()).values())) {
                List<String> lst = processTabComplete(commandSender, parentCommands, subCommand, Arrays.copyOfRange(args, 1, args.length), label);
                if (lst != null)
                    tabCompletions.addAll(lst);
            }
        }
        return tabCompletions;
    }

    public List<String> getCommandTabCompletions(Set<Class<?>> processedArgumentsClasses, CommandSender commandSender, List<Command> parentCommands, Command command, String[] args, String label) {
        LinkedHashMap<String, String> rawArgs = new LinkedHashMap<>();
        int maxArgs = command.getArguments().length + command.getOptionalArguments().length;

        List<String> finalList = new ArrayList<>();

        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> filteredKeys = command.getSubCommands().keySet().stream()
                    .filter(key -> key.toLowerCase().startsWith(prefix))
                    .sorted()
                    .toList();

            finalList.addAll(filteredKeys);
        }

        if (args.length > maxArgs)
            return finalList;

        List<CommandArgument<?>> commandArgumentList = new ArrayList<>(Arrays.asList(command.getArguments()));
        commandArgumentList.addAll(Arrays.asList(command.getOptionalArguments()));

        CommandArgument<?> arg = commandArgumentList.get(args.length - 1);

        // Check if this argument was already processed
        if (!processedArgumentsClasses.add(arg.getClass())) {
            return Collections.emptyList();
        }

        // fill rawArgs
        for (int i = 0; i < args.length; i++) {
            CommandArgument<?> argument = commandArgumentList.get(i);
            rawArgs.put(argument.getName(), args[i]);
        }

        RawCommandContext context = new RawCommandContext(label, parentCommands, rawArgs);

        List<String> completions = arg.tabComplete(commandSender, args[args.length - 1], command, args.length - 1, context);
        if (completions != null)
            finalList.addAll(completions);

        return finalList;
    }
}
