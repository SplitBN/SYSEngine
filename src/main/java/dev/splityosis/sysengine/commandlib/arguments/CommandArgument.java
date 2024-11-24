package dev.splityosis.sysengine.commandlib.arguments;

import dev.splityosis.sysengine.commandlib.command.Command;
import dev.splityosis.sysengine.commandlib.command.CommandContext;
import dev.splityosis.sysengine.commandlib.command.RawCommandContext;
import dev.splityosis.sysengine.commandlib.exception.InvalidInputException;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Represents an argument in a command. Implementations of this interface define
 * how specific types of arguments should be processed, validated, and tab-completed.
 *
 * @param <T> The type of the argument value.
 */
public interface CommandArgument<T> {

    /**
     * Gets the name of this argument. This name represents the purpose or role
     * of the argument within the command (e.g., "receiver" for a player argument
     * in a give command). This name is used when displaying command usage, help
     * messages, or providing feedback to the user.
     *
     * @return the descriptive name of the argument.
     */
    String getName();

    /**
     * Parses the input provided by the user into the expected argument type.
     * This method is responsible for converting the input string into the desired
     * data type, throwing an exception if the input is invalid.
     *
     * @param sender the sender of the command (typically a player or console).
     * @param input the raw input string for this argument.
     * @param command the command being executed.
     * @param index the index of this argument in the command's argument list.
     * @param context the command context containing any pre-processed data and parsed previous arguments.
     * @return the parsed argument of type T.
     * @throws InvalidInputException if the input cannot be parsed or is invalid.
     */
    T parse(CommandSender sender, String input, Command command, int index, CommandContext context) throws InvalidInputException;

    /**
     * Handles invalid input for this argument. This method is called when the input
     * provided by the user fails validation or cannot be parsed. It provides feedback
     * to the user to inform them of the correct format or available options.
     *
     * @param sender the sender of the command (typically a player or console).
     * @param input the raw input string that failed validation.
     * @param command the command being executed.
     * @param index the index of this argument in the command's argument list.
     * @param context the command context containing any pre-processed data and parsed previous arguments.
     * @param inputException the exception thrown by the failed parse method.
     */
    void onInvalidInput(CommandSender sender, String input, Command command, int index, CommandContext context, InvalidInputException inputException);

    /**
     * Provides a list of tab-completion options based on the current input.
     * This method allows dynamic suggestions to be displayed to the user as they
     * type the command, helping them complete the argument with valid options.
     *
     * @param sender the sender of the command (typically a player or console).
     * @param input the current input string for this argument.
     * @param command the command being executed.
     * @param index the index of this argument in the command's argument list.
     * @param context the raw command context containing any pre-processed data.
     * @return a list of suggested completions for the argument.
     */
    List<String> tabComplete(CommandSender sender, String input, Command command, int index, RawCommandContext context);
}
