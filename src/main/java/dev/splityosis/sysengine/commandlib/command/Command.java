package dev.splityosis.sysengine.commandlib.command;

import dev.splityosis.sysengine.commandlib.arguments.CommandArgument;
import dev.splityosis.sysengine.commandlib.requirements.CommandRequirement;
import dev.splityosis.sysengine.commandlib.consumers.CommandConsumer;
import dev.splityosis.sysengine.commandlib.consumers.PlayerCommandConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a command in the command library.
 * Each command has a name, optional aliases, permission requirements, description, arguments, and execution logic.
 * Commands can be customized with requirements, required and optional arguments, and different executors for various contexts.
 */
public class Command implements Cloneable{

    private String name;
    private String[] aliases;
    private String description;
    private String permission;
    private CommandArgument<?>[] arguments;
    private CommandArgument<?>[] optionalArguments;
    private CommandRequirement<?>[] requirements;
    private CommandConsumer commandConsumer;
    private PlayerCommandConsumer playerCommandConsumer;
    private Map<String, Map<Integer, Command>> subCommands;

    /**
     * Creates a new command with the specified name and optional aliases.
     *
     * @param name     the primary name of the command.
     * @param aliases  optional alternative names (aliases) for the command.
     */
    public Command(String name, String... aliases) {
        this.name = name;
        this.aliases = (aliases != null) ? aliases : new String[0];
        this.arguments = new CommandArgument[0];
        this.optionalArguments = new CommandArgument[0];
        this.requirements = new CommandRequirement[0];
        this.description = "";
        this.subCommands = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Sets a description for the command.
     *
     * @param description the command description.
     * @return the command instance, for chaining.
     */
    public Command description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the permission required to execute this command.
     *
     * @param permission the permission string.
     * @return the command instance, for chaining.
     */
    public Command permission(String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Sets the requirements that must be met to execute the command.
     *
     * @param requirements the requirements to be met before command execution.
     * @return the command instance, for chaining.
     */
    public Command requirements(CommandRequirement<?>... requirements) {
        this.requirements = requirements;
        return this;
    }

    /**
     * Sets the required arguments for the command.
     *
     * @param arguments the required arguments.
     * @return the command instance, for chaining.
     */
    public Command arguments(CommandArgument<?>... arguments) {
        this.arguments = arguments;
        return this;
    }

    /**
     * Sets optional arguments for the command. Optional arguments follow the required arguments in the command syntax.
     *
     * @param optionalArguments the optional arguments, coming after required arguments in the command.
     * @return the command instance, for chaining.
     */
    public Command optionalArguments(CommandArgument<?>... optionalArguments) {
        this.optionalArguments = optionalArguments;
        return this;
    }

    /**
     * Sets the main executor for the command, handling its logic.
     *
     * @param commandConsumer the command consumer.
     * @return the command instance, for chaining.
     */
    public Command executes(CommandConsumer commandConsumer) {
        this.commandConsumer = commandConsumer;
        return this;
    }

    /**
     * Sets the player-specific executor for the command, providing logic if executed by a player.
     *
     * @param playerCommandConsumer the player command consumer.
     * @return the command instance, for chaining.
     */
    public Command playerExecutes(PlayerCommandConsumer playerCommandConsumer) {
        this.playerCommandConsumer = playerCommandConsumer;
        return this;
    }

    /**
     * Adds subcommands to this command.
     * <p>
     * For example, for a base command `/base`, subcommands could be added like:
     *   `/base <args>`
     *   `/base create <args>`
     *   `/base delete <args>`
     * <p>
     * Example usage:
     * <pre>{@code
     * Command baseCommand = new Command("base")
     *         .setDescription("Base command for managing bases")
     *         .executes((sender, context) -> sender.sendMessage("Use /base <subcommand> for specific actions."));
     *
     * Command createCommand = new Command("create")
     *         .setDescription("Creates a new base")
     *         .setArguments(new CommandArgument<>("baseName"))
     *         .executes((sender, context) -> sender.sendMessage("Base created!"));
     *
     * Command deleteCommand = new Command("delete")
     *         .setDescription("Deletes an existing base")
     *         .setArguments(new CommandArgument<>("baseName"))
     *         .executes((sender, context) -> sender.sendMessage("Base deleted!"));
     *
     * baseCommand.addSubCommand(createCommand, deleteCommand);
     * }</pre>
     *
     * @param commands One or more Command instances to add as subcommands.
     * @return The current Command instance, for chaining.
     */
    public Command addSubCommands(Command... commands) {
        for (Command command : commands) {
            int minArgs = command.getArguments().length;
            int maxArgs = minArgs + command.getOptionalArguments().length;

            addSubCommandForAlias(command.getName(), command, minArgs, maxArgs);

            for (String alias : command.getAliases())
                addSubCommandForAlias(alias, command, minArgs, maxArgs);
        }

        return this;
    }

    /**
     * Creates and returns a copy of this Command instance.
     * <p>
     * The clone includes all properties of the command, such as the name, aliases,
     * description, permission, arguments, optional arguments, requirements, and executors.
     * </p>
     *
     * <p><b>Note:</b> This method performs a shallow copy of the command's data, which is
     * sufficient for immutable and independent fields such as Strings and arrays of command arguments
     * and requirements. Modifications to cloned arrays (like aliases, arguments, or requirements) will
     * not affect the original arrays in this instance, as they are cloned separately.</p>
     *
     * @return A new Command instance that is a copy of this command.
     */
    @Override
    public Object clone() {
        Command clonedCommand = new Command(this.name, this.aliases.clone());

        clonedCommand.description = this.description;
        clonedCommand.permission = this.permission;
        clonedCommand.commandConsumer = this.commandConsumer;
        clonedCommand.playerCommandConsumer = this.playerCommandConsumer;

        if (this.arguments != null) {
            clonedCommand.arguments = this.arguments.clone();
        }
        if (this.optionalArguments != null) {
            clonedCommand.optionalArguments = this.optionalArguments.clone();
        }

        if (this.requirements != null) {
            clonedCommand.requirements = this.requirements.clone();
        }

        return clonedCommand;
    }

    // Getter methods for command properties

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getPermission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }

    public CommandArgument<?>[] getArguments() {
        return arguments;
    }

    public CommandArgument<?>[] getOptionalArguments() {
        return optionalArguments;
    }

    public CommandRequirement<?>[] getRequirements() {
        return requirements;
    }

    public CommandConsumer getCommandConsumer() {
        return commandConsumer;
    }

    public PlayerCommandConsumer getPlayerCommandConsumer() {
        return playerCommandConsumer;
    }

    public Map<String, Map<Integer, Command>> getSubCommands() {
        return subCommands;
    }

    private void addSubCommandForAlias(String alias, Command command, int minArgs, int maxArgs) {
        Map<Integer, Command> commandsByArgCount = subCommands.computeIfAbsent(alias, k -> new HashMap<>());

        for (int argCount = minArgs; argCount <= maxArgs; argCount++)
            commandsByArgCount.put(argCount, command);
    }
}
