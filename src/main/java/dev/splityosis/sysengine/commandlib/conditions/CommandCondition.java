package dev.splityosis.sysengine.commandlib.conditions;

import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.command.CommandContext;
import org.bukkit.command.CommandSender;

/**
 * Represents a condition that must be met before a command is executed.
 * Implementations of this interface define custom logic to check whether
 * a command should be executed based on the provided sender and arguments.
 * <p>
 * Conditions can be used to restrict or enable the execution of commands based
 * on specific criteria (e.g., permissions, player state, or command context).
 */
public interface CommandCondition {

    /**
     * Checks if the condition is met for the given command execution context.
     * This method is called before the command is executed to determine whether
     * the command can proceed. If the condition is not met, the command's
     * execution will be halted.
     *
     * @param sender the sender of the command (typically a player or console).
     * @param command the command being executed.
     * @param context the context of the command execution.
     * @return true if the condition is met, false otherwise.
     */
    boolean isMet(CommandSender sender, Command command, CommandContext context);

    /**
     * Called when the condition is not met. This method provides a way to handle
     * the situation where a command cannot be executed due to unmet conditions.
     * It could be used to send a message to the sender or perform any other logic.
     *
     * @param sender the sender of the command (typically a player or console).
     * @param command the command being executed.
     * @param context the context of the command execution.
     */
    void onNotMet(CommandSender sender, Command command, CommandContext context);
}
