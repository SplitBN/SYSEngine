package dev.splityosis.sysengine.utils;

import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for handling color codes in strings and lists of strings.
 * Provides methods for applying color codes, including support for hex colors,
 * and methods for reversing colorization.
 */
public class ColorUtil {

    /**
     * Regex pattern for matching hex color codes prefixed with '&'.
     */
    public static final Pattern HEX_PATTERN = Pattern.compile("&(#([0-9A-Fa-f]{6}))");

    /**
     * Regex pattern for matching hex color codes prefixed with '§x' for reverse conversion.
     */
    public static final Pattern REVERSE_HEX_PATTERN = Pattern.compile("§x(§[0-9a-fA-F]){6}");

    /**
     * Colorizes a string by applying standard and hex color codes.
     * Supports Minecraft 1.16+ hex color codes if the server version allows.
     *
     * @param str The string to colorize.
     * @return The colorized string.
     */
    public static String colorize(String str) {
        if (VersionUtil.isServerAtLeast("1.16"))
            str = colorizeHex(str);
        return colorizeStandard(str);
    }

    /**
     * Colorizes a collection of strings by applying standard and hex color codes.
     * Supports Minecraft 1.16+ hex color codes if the server version allows.
     * <p>
     * Modifies the collection in place and returns the same collection.
     *
     * @param collection The collection of strings to colorize.
     * @param <T> The type of the collection (e.g., List, Set).
     * @return The modified collection of colorized strings.
     */
    public static <T extends Collection<String>> T colorize(T collection) {
        if (VersionUtil.isServerAtLeast("1.16"))
            colorizeHex(collection);
        colorizeStandard(collection);
        return collection;
    }

    /**
     * Reverses colorization in a string, converting color codes back to
     * the format prefixed with '&'.
     *
     * @param str The string to reverse colorize.
     * @return The reverse-colorized string.
     */
    public static String reverseColorize(String str) {
        if (VersionUtil.isServerAtLeast("1.16"))
            str = reverseColorizeHex(str);
        return reverseColorizeStandard(str);
    }

    /**
     * Reverses colorization in a collection of strings, converting color codes
     * back to the format prefixed with '&'.
     * <p>
     * Modifies the collection in place and returns the same collection.
     *
     * @param collection The collection of strings to reverse colorize.
     * @param <T> The type of the collection (e.g., List, Set).
     * @return The modified collection of reverse-colorized strings.
     */
    public static <T extends Collection<String>> T reverseColorize(T collection) {
        if (VersionUtil.isServerAtLeast("1.16"))
            reverseColorizeHex(collection);
        reverseColorizeStandard(collection);
        return collection;
    }

    /**
     * Colorizes a string using standard Minecraft color codes with '&' as a prefix.
     *
     * @param str The string to colorize.
     * @return The colorized string.
     */
    public static String colorizeStandard(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    /**
     * Colorizes a collection of strings using standard Minecraft color codes with '&' as a prefix.
     * <p>
     * Modifies the collection in place and returns the same collection.
     *
     * @param collection The collection of strings to colorize.
     * @param <T> The type of the collection (e.g., List, Set).
     * @return The modified collection of colorized strings.
     */
    public static <T extends Collection<String>> T colorizeStandard(T collection) {
        for (String string : collection) {
            collection.remove(string); // Remove the old string
            collection.add(string == null ? null : colorizeStandard(string)); // Add the modified string
        }
        return collection;
    }

    /**
     * Reverses standard colorization in a string by replacing '§' codes with '&' codes.
     *
     * @param str The string to reverse colorize.
     * @return The reverse-colorized string.
     */
    public static String reverseColorizeStandard(String str) {
        return str.replaceAll("§([0-9a-fklmnorx])", "&$1");
    }

    /**
     * Reverses standard colorization in a collection of strings by replacing '§' codes with '&' codes.
     * <p>
     * Modifies the collection in place and returns the same collection.
     *
     * @param collection The collection of strings to reverse colorize.
     * @param <T> The type of the collection (e.g., List, Set).
     * @return The modified collection of reverse-colorized strings.
     */
    public static <T extends Collection<String>> T reverseColorizeStandard(T collection) {
        for (String string : collection) {
            collection.remove(string);
            collection.add(string == null ? null : reverseColorizeStandard(string));
        }
        return collection;
    }

    /**
     * Colorizes a string by converting hex color codes with '&' prefix
     * to Minecraft-compatible hex codes.
     *
     * @param str The string to colorize.
     * @return The hex-colorized string.
     */
    public static String colorizeHex(String str) {
        Matcher matcher = HEX_PATTERN.matcher(str);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find())
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group(1)).toString());

        return matcher.appendTail(buffer).toString();
    }

    /**
     * Colorizes a collection of strings by converting hex color codes with '&' prefix
     * to Minecraft-compatible hex codes.
     * <p>
     * Modifies the collection in place and returns the same collection.
     *
     * @param collection The collection of strings to colorize.
     * @param <T> The type of the collection (e.g., List, Set).
     * @return The modified collection of hex-colorized strings.
     */
    public static <T extends Collection<String>> T colorizeHex(T collection) {
        for (String string : collection) {
            collection.remove(string);
            collection.add(string == null ? null : colorizeHex(string));
        }
        return collection;
    }

    /**
     * Reverses hex colorization in a string by converting Minecraft hex color codes
     * back to the format with '&' and hex values.
     *
     * @param str The string to reverse colorize.
     * @return The reverse-colorized hex string.
     */
    public static String reverseColorizeHex(String str) {
        Matcher matcher = REVERSE_HEX_PATTERN.matcher(str);

        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String colorCode = matcher.group().replaceAll("§", "");
            matcher.appendReplacement(sb, "&#" + colorCode.substring(1));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Reverses hex colorization in a collection of strings by converting Minecraft hex color codes
     * back to the format with '&' and hex values.
     * <p>
     * Modifies the collection in place and returns the same collection.
     *
     * @param collection The collection of strings to reverse colorize.
     * @param <T> The type of the collection (e.g., List, Set).
     * @return The modified collection of reverse-colorized hex strings.
     */
    public static <T extends Collection<String>> T reverseColorizeHex(T collection) {
        for (String string : collection) {
            collection.remove(string);
            collection.add(string == null ? null : reverseColorizeHex(string));
        }
        return collection;
    }
}
