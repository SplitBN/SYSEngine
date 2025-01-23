package dev.splityosis.sysengine.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for interacting with PlaceholderAPI.
 * <p>
 * All methods parse the provided input for placeholders. If PlaceholderAPI is unavailable,
 * the input is returned unchanged.
 */
public class PapiUtil {

    private PapiUtil() {
    }

    /**
     * Checks if PlaceholderAPI is currently installed and enabled.
     *
     * @return {@code true} if PlaceholderAPI is enabled, {@code false} otherwise.
     */
    public static boolean isPapiAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    /**
     * Parses placeholders in a string for the specified Player.
     * If PlaceholderAPI is unavailable, the input string is returned unchanged.
     *
     * @param player The Player context for parsing.
     * @param text   The string containing placeholders.
     * @return The parsed string, or the original string if PlaceholderAPI is unavailable.
     */
    public static String parsePlaceholders(Player player, String text) {
        if (isPapiAvailable() && text != null) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }

    /**
     * Parses placeholders in a string for the specified OfflinePlayer.
     * If PlaceholderAPI is unavailable, the input string is returned unchanged.
     *
     * @param player The OfflinePlayer context for parsing.
     * @param text   The string containing placeholders.
     * @return The parsed string, or the original string if PlaceholderAPI is unavailable.
     */
    public static String parsePlaceholders(OfflinePlayer player, String text) {
        if (isPapiAvailable() && text != null) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }

    /**
     * Parses placeholders in a list of strings for the specified Player.
     * If PlaceholderAPI is unavailable, the input list is returned unchanged.
     *
     * @param player The Player context for parsing.
     * @param lines  The list of strings containing placeholders.
     * @return The list of parsed strings, or the original list if PlaceholderAPI is unavailable.
     */
    public static List<String> parsePlaceholders(Player player, List<String> lines) {
        if (isPapiAvailable() && lines != null) {
            return lines.stream().map(line -> parsePlaceholders(player, line)).collect(Collectors.toList());
        }
        return lines;
    }

    /**
     * Parses placeholders in a list of strings for the specified OfflinePlayer.
     * If PlaceholderAPI is unavailable, the input list is returned unchanged.
     *
     * @param player The OfflinePlayer context for parsing.
     * @param lines  The list of strings containing placeholders.
     * @return The list of parsed strings, or the original list if PlaceholderAPI is unavailable.
     */
    public static List<String> parsePlaceholders(OfflinePlayer player, List<String> lines) {
        if (isPapiAvailable() && lines != null) {
            return lines.stream().map(line -> parsePlaceholders(player, line)).collect(Collectors.toList());
        }
        return lines;
    }

    /**
     * Parses relational placeholders in a string for two Players.
     * If PlaceholderAPI is unavailable, the input string is returned unchanged.
     *
     * @param one  The first Player in the relationship.
     * @param two  The second Player in the relationship.
     * @param text The string containing relational placeholders.
     * @return The parsed string, or the original string if PlaceholderAPI is unavailable.
     */
    public static String parseRelationalPlaceholders(Player one, Player two, String text) {
        if (isPapiAvailable() && text != null) {
            return PlaceholderAPI.setRelationalPlaceholders(one, two, text);
        }
        return text;
    }

    /**
     * Parses relational placeholders in a list of strings for two Players.
     * If PlaceholderAPI is unavailable, the input list is returned unchanged.
     *
     * @param one   The first Player in the relationship.
     * @param two   The second Player in the relationship.
     * @param lines The list of strings containing relational placeholders.
     * @return The list of parsed strings, or the original list if PlaceholderAPI is unavailable.
     */
    public static List<String> parseRelationalPlaceholders(Player one, Player two, List<String> lines) {
        if (isPapiAvailable() && lines != null) {
            return lines.stream().map(line -> parseRelationalPlaceholders(one, two, line)).collect(Collectors.toList());
        }
        return lines;
    }

    /**
     * Parses bracket placeholders in a string for the specified Player.
     * If PlaceholderAPI is unavailable, the input string is returned unchanged.
     *
     * @param player The Player context for parsing.
     * @param text   The string containing bracket placeholders.
     * @return The parsed string, or the original string if PlaceholderAPI is unavailable.
     */
    public static String parseBracketPlaceholders(Player player, String text) {
        if (isPapiAvailable() && text != null) {
            return PlaceholderAPI.setBracketPlaceholders(player, text);
        }
        return text;
    }

    /**
     * Parses bracket placeholders in a string for the specified OfflinePlayer.
     * If PlaceholderAPI is unavailable, the input string is returned unchanged.
     *
     * @param player The OfflinePlayer context for parsing.
     * @param text   The string containing bracket placeholders.
     * @return The parsed string, or the original string if PlaceholderAPI is unavailable.
     */
    public static String parseBracketPlaceholders(OfflinePlayer player, String text) {
        if (isPapiAvailable() && text != null) {
            return PlaceholderAPI.setBracketPlaceholders(player, text);
        }
        return text;
    }

    /**
     * Parses bracket placeholders in a list of strings for the specified Player.
     * If PlaceholderAPI is unavailable, the input list is returned unchanged.
     *
     * @param player The Player context for parsing.
     * @param lines  The list of strings containing bracket placeholders.
     * @return The list of parsed strings, or the original list if PlaceholderAPI is unavailable.
     */
    public static List<String> parseBracketPlaceholders(Player player, List<String> lines) {
        if (isPapiAvailable() && lines != null) {
            return lines.stream().map(line -> parseBracketPlaceholders(player, line)).collect(Collectors.toList());
        }
        return lines;
    }

    /**
     * Parses bracket placeholders in a list of strings for the specified OfflinePlayer.
     * If PlaceholderAPI is unavailable, the input list is returned unchanged.
     *
     * @param player The OfflinePlayer context for parsing.
     * @param lines  The list of strings containing bracket placeholders.
     * @return The list of parsed strings, or the original list if PlaceholderAPI is unavailable.
     */
    public static List<String> parseBracketPlaceholders(OfflinePlayer player, List<String> lines) {
        if (isPapiAvailable() && lines != null) {
            return lines.stream().map(line -> parseBracketPlaceholders(player, line)).collect(Collectors.toList());
        }
        return lines;
    }
}
