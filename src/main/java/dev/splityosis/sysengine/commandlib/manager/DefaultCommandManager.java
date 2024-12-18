package dev.splityosis.sysengine.commandlib.manager;

import dev.splityosis.sysengine.commandlib.arguments.CommandArgument;
import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.command.CommandContext;
import dev.splityosis.sysengine.commandlib.command.RawCommandContext;
import dev.splityosis.sysengine.commandlib.requirements.CommandRequirement;
import dev.splityosis.sysengine.commandlib.exception.RequirementNotMetException;
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
    private Map<Command, PluginCommand> registeredPluginCommands = new HashMap<>();

    public DefaultCommandManager(JavaPlugin plugin, CommandHelpProvider commandHelpProvider) {
        this.plugin = plugin;
        this.commandHelpProvider = commandHelpProvider;
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void registerCommands(Command... commands) {
        for (Command command : commands) {
            PluginCommand pluginCommand = CommandUtil.createPluginCommand(command.getName(), getPlugin());
            if (pluginCommand == null)
                throw new RuntimeException("Couldn't create PluginCommand.");

            pluginCommand.setAliases(Arrays.asList(command.getAliases()));
            pluginCommand.setPermission(command.getPermission());
            if (!command.getDescription().isEmpty())
                pluginCommand.setDescription(command.getDescription());


            pluginCommand.setExecutor((sender, command1, label, args) -> {
                process(sender, new ArrayList<>(), command, args, label);
                return true;
            });

            pluginCommand.setTabCompleter((sender, command1, label, args) -> processTabComplete(sender, new ArrayList<>(), command, args, label));


            pluginCommand.register(CommandUtil.getCommandMap());
            registeredPluginCommands.put(command, pluginCommand);
            CommandUtil.getCommandMap().register(command.getName(), pluginCommand);
        }
    }

    @Override
    public void unregisterCommands(Command... commands) {
        for (Command command : commands) {
            PluginCommand pluginCommand = registeredPluginCommands.remove(command);
            if (pluginCommand != null)
                pluginCommand.unregister(CommandUtil.getCommandMap());
        }
    }

    @Override
    public CommandHelpProvider getCommandHelpProvider() {
        return commandHelpProvider;
    }


    @Override
    public void process(CommandSender commandSender, List<Command> parentCommands, Command root, String[] inputArgs, String label) {

        if (parentCommands == null)
            parentCommands = new ArrayList<>();

        if (root.getPermission() != null && !commandSender.hasPermission(root.getPermission())) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return;
        }

        FindCommandResult findCommandResult = findCommand(commandSender, root, inputArgs);
        if (findCommandResult.getStatus() == FindCommandStatus.MISSING_PERMISSION) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return;
        }

        if (findCommandResult.getStatus() == FindCommandStatus.USAGE_ERROR) {
            List<Command> parents = findCommandResult.getPath();
            if (!parents.isEmpty())
                parents.remove(parents.size() - 1);
            getCommandHelpProvider().sendHelp(commandSender, parents, findCommandResult.getCommand(), findCommandResult.getArgs(), label);
            return;
        }

        Command command = findCommandResult.getCommand();
        String[] args = findCommandResult.getArgs();

            // Get the right executor
            CommandConsumer commandExecutor;
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
            List<Object> requirementValues = new ArrayList<>();


            List<CommandArgument<?>> commandArgumentList = new ArrayList<>(Arrays.asList(command.getArguments()));
            commandArgumentList.addAll(Arrays.asList(command.getOptionalArguments()));

            CommandContext context = new CommandContext(command, label, parentCommands, rawArgs, parsedArgs, requirementValues);

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
                    argument.onInvalidInput(commandSender, args[i], command, i, context, e);
                    return;
                }
            }

            // Check requirements
            for (CommandRequirement<?> requirement : command.getRequirements()) {
                try {
                    Object value = requirement.evaluate(commandSender, command, context);
                    requirementValues.add(value);
                } catch (RequirementNotMetException e) {
                    requirement.onNotMet(commandSender, command, context, e);
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

    @Override
    public CommandManager setCommandHelpProvider(CommandHelpProvider commandHelpProvider) {
        this.commandHelpProvider = commandHelpProvider;
        return this;
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

        RawCommandContext context = new RawCommandContext(command, label, parentCommands, rawArgs);

        List<String> completions = arg.tabComplete(commandSender, args[args.length - 1], command, args.length - 1, context);
        if (completions != null)
            finalList.addAll(completions);

        return finalList;
    }

    /**
     * Find the final command to execute, checking permissions along the way.
     * <p>
     * If successful, returns a SUCCESS result with the path and final args.
     * If failed due to missing permission, returns MISSING_PERMISSION result.
     * If failed due to usage/args error, returns USAGE_ERROR with the relevant command and path.
     *
     * @param sender The CommandSender executing the command.
     * @param rootCommand The starting command (root).
     * @param args The arguments supplied.
     * @return The result of the command resolution.
     */
    public static FindCommandResult findCommand(CommandSender sender, Command rootCommand, String[] args) {
        return findCommand(sender, rootCommand, args, new ArrayList<>());
    }

    private static FindCommandResult findCommand(CommandSender sender, Command currentCommand, String[] args, List<Command> pathSoFar) {
        if (currentCommand.getPermission() != null && !sender.hasPermission(currentCommand.getPermission())) {
            // Missing permission for this node
            return FindCommandResult.missingPermission(currentCommand);
        }

        pathSoFar.add(currentCommand);

        if (args.length > 0) {
            Map<String, Map<Integer, Command>> subs = currentCommand.getSubCommands();
            String nextArg = args[0];

            if (subs.containsKey(nextArg)) {
                Map<Integer, Command> variants = subs.get(nextArg);
                int remainingArgCount = args.length - 1;
                String[] subArgs = new String[remainingArgCount];
                System.arraycopy(args, 1, subArgs, 0, remainingArgCount);

                Set<Command> alrChecked = new HashSet<>();

                for (Command candidate : variants.values()) {
                    if (!alrChecked.add(candidate)) continue;

                    if (candidate.getPermission() != null && !sender.hasPermission(candidate.getPermission())) {
                        return FindCommandResult.missingPermission(candidate);
                    }

                    List<Command> newPath = new ArrayList<>(pathSoFar);
                    FindCommandResult result = findCommand(sender, candidate, subArgs, newPath);
                    if (result != null && result.getStatus() != null) {
                        if (result.getStatus() == FindCommandStatus.SUCCESS
                                || result.getStatus() == FindCommandStatus.MISSING_PERMISSION) {
                            return result;
                        } else if (result.getStatus() == FindCommandStatus.USAGE_ERROR) {
                            return result;
                        }
                    }
                }

                // If we tried all variants and none succeeded, it means:
                // Either missing permissions were handled already (and returned),
                // or no suitable subcommand variant could handle these arguments.
                // This would mean a usage/args error at this command node.
                return FindCommandResult.usageError(pathSoFar, currentCommand, args);
            }
        }

        if (currentCommand.isExecutable() && currentCommand.doesHandle(args.length)) {
            // Success
            return FindCommandResult.success(pathSoFar, args);
        }

        return FindCommandResult.usageError(pathSoFar, currentCommand, args);
    }

    public enum FindCommandStatus {
        SUCCESS,
        MISSING_PERMISSION,
        USAGE_ERROR
    }

    public static class FindCommandResult {
        private final FindCommandStatus status;
        private final List<Command> path;
        private final Command command;
        private final String[] args;

        private FindCommandResult(FindCommandStatus status, List<Command> path, Command command, String[] args) {
            this.status = status;
            this.path = path;
            this.command = command;
            this.args = args;
        }

        public static FindCommandResult success(List<Command> path, String[] finalArgs) {
            return new FindCommandResult(FindCommandStatus.SUCCESS, path, path.get(path.size()-1), finalArgs);
        }

        public static FindCommandResult missingPermission(Command command) {
            return new FindCommandResult(FindCommandStatus.MISSING_PERMISSION, null, command, null);
        }

        public static FindCommandResult usageError(List<Command> path, Command failedAt, String[] args) {
            return new FindCommandResult(FindCommandStatus.USAGE_ERROR, path, failedAt, args);
        }

        public FindCommandStatus getStatus() {
            return status;
        }

        /**
         * The full path of commands leading to the final command or the command that caused a usage error.
         */
        public List<Command> getPath() {
            return path;
        }

        /**
         * In SUCCESS, this is the final command to execute.
         * In USAGE_ERROR, this is the command where it failed.
         * In MISSING_PERMISSION, this is the command that required a permission sender lacked.
         */
        public Command getCommand() {
            return command;
        }

        /**
         * The arguments that apply to the final command (SUCCESS) or the arguments that caused a usage error (USAGE_ERROR).
         * Null if MISSING_PERMISSION.
         */
        public String[] getArgs() {
            return args;
        }
    }
}
