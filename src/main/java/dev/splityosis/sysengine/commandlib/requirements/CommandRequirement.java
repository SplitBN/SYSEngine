package dev.splityosis.sysengine.commandlib.requirements;

import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.command.CommandContext;
import dev.splityosis.sysengine.commandlib.exception.RequirementNotMetException;
import org.bukkit.command.CommandSender;

/**
 * Represents a requirement that must be met before a command is executed.
 * Implementations of this interface define custom logic to check whether
 * a command should be executed based on the provided sender and arguments.
 * <p>
 * Requirements can be used to restrict or enable the execution of commands based
 * on specific criteria (e.g., permissions, player state, or command context).
 * When a requirement is met, it may return a context-specific result or null if no result is needed.
 *
 * @param <T> the type of result returned when the requirement is met, or null if no result is required.
 */
public interface CommandRequirement<T> {

    /**
     * Evaluates the requirement for the given command execution context.
     * If the requirement is met, this method returns a result of type {@code T} or {@code null}
     * if no result is applicable.
     * If the requirement is not met, it throws a {@code ConditionNotMetException}.
     *
     * @param sender the sender of the command (typically a player or console).
     * @param command the command being executed.
     * @param context the context of the command execution.
     * @return a result of type {@code T} if applicable, or {@code null} if no result is needed.
     * @throws RequirementNotMetException if the requirement is not met, preventing command execution.
     */
    T evaluate(CommandSender sender, Command command, CommandContext context) throws RequirementNotMetException;

    /**
     * Called when the requirement is not met. This method provides a way to handle
     * the situation where a command cannot be executed due to unmet requirements.
     * It can be used to send a message to the sender or perform any other necessary logic.
     *
     * @param sender the sender of the command (typically a player or console).
     * @param command the command being executed.
     * @param context the context of the command execution.
     * @param exception the exception detailing why the requirement was not met.
     */
    void onNotMet(CommandSender sender, Command command, CommandContext context, RequirementNotMetException exception);
}
