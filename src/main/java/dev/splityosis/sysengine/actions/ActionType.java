package dev.splityosis.sysengine.actions;

import dev.splityosis.sysengine.utils.PapiUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a type of action that can be performed.
 * Each ActionType can:
 *  - Have a unique primary name and possible aliases
 *  - Provide a description and parameter definitions for documentation
 *  - Execute the action given a target and parameters
 */
public interface ActionType {

    /**
     * @return the unique primary name of this action type
     *         (e.g. "playSound")
     */
    String getName();

    /**
     * @return a list of aliases for this action type
     *         (e.g. ["sound", "sendSound"])
     *         Returns an empty list if there are no aliases.
     */
    List<String> getAliases();

    /**
     * @return a brief description of what this action does
     *         (e.g. "Plays a sound to the target.")
     */
    String getDescription();

    /**
     * @return a list of required parameter names (in order)
     *         for documentation/guidance
     *         (e.g. ["soundName", "volume", "pitch"])
     */
    List<String> getParameters();

    /**
     * @return a list of optional parameter names (in order)
     *         for documentation/guidance
     *         (e.g. ["message", "command"])
     */
    List<String> getOptionalParameters();

    /**
     * Executes this action.
     * This assumes the parameter count is right according to{@link #getParameters()} and{@link #getOptionalParameters()}.
     *
     * @param target        an object that the action can use or cast
     *                      (e.g. a Player, a CommandSender, null, etc.)
     * @param params        the list of parameter values (in the same order
     *                      as required + optional parameters)
     * @param replacements  a map of replacements (e.g. {"%faction%" -> "TheBobs"})
     *                      that can be substituted if needed
     *
     * @throws IllegalArgumentException if required params are missing
     *         or if there's an issue with the target type, etc.
     */
    void execute(@Nullable Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements)
            throws IllegalArgumentException;

    /* -------------------------------------------------
     * Helper Methods
     * ------------------------------------------------- */

    /**
     * Applies custom replacements to the given string and parses placeholders using PlaceholderAPI if available.
     *
     * @param player        The player for whom the placeholders are being applied.
     * @param str           The input string containing placeholders and custom keys to be replaced.
     * @param replacements  A map of custom placeholder keys and their corresponding replacement values.
     * @return              The resulting string after applying replacements and parsing placeholders.
     */
    default String applyPlaceholders(@Nullable Player player, @NotNull String str, @NotNull Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet())
            str = str.replace(entry.getKey(), entry.getValue());

        return PapiUtil.parsePlaceholders(player, str);
    }

    /**
     * Applies custom replacements to each string in the list and parses placeholders using PlaceholderAPI if available.
     *
     * @param player        The player for whom the placeholders are being applied.
     * @param lst           The list of strings containing placeholders and custom keys to be replaced.
     * @param replacements  A map of custom placeholder keys and their corresponding replacement values.
     * @return              A new list of strings after applying replacements and parsing placeholders.
     */
    default List<String> applyPlaceholders(@Nullable Player player, @NotNull List<String> lst, @NotNull Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet())
            lst = lst.stream().map(string -> string.replace(entry.getKey(), entry.getValue())).collect(Collectors.toList());

        return PapiUtil.parsePlaceholders(player, lst);
    }
}