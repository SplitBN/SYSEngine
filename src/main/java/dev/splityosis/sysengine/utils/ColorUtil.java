package dev.splityosis.sysengine.utils;

import org.bukkit.ChatColor;
import java.util.List;
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
     * Colorizes a list of strings by applying standard and hex color codes.
     * Supports Minecraft 1.16+ hex color codes if the server version allows.
     *
     * @param lst The list of strings to colorize.
     * @return A list of colorized strings.
     */
    public static List<String> colorize(List<String> lst) {
        if (VersionUtil.isServerAtLeast("1.16"))
            lst = colorizeHex(lst);
        return colorizeStandard(lst);
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
     * Reverses colorization in a list of strings, converting color codes
     * back to the format prefixed with '&'.
     *
     * @param lst The list of strings to reverse colorize.
     * @return A list of reverse-colorized strings.
     */
    public static List<String> reverseColorize(List<String> lst) {
        if (VersionUtil.isServerAtLeast("1.16"))
            lst = reverseColorizeHex(lst);
        return reverseColorizeStandard(lst);
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
     * Colorizes a list of strings using standard Minecraft color codes with '&' as a prefix.
     *
     * @param lst The list of strings to colorize.
     * @return A list of colorized strings.
     */
    public static List<String> colorizeStandard(List<String> lst) {
        return lst.stream().map(string -> string == null ? null : colorizeStandard(string)).toList();
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
     * Reverses standard colorization in a list of strings by replacing '§' codes with '&' codes.
     *
     * @param lst The list of strings to reverse colorize.
     * @return A list of reverse-colorized strings.
     */
    public static List<String> reverseColorizeStandard(List<String> lst) {
        return lst.stream().map(string -> string == null ? null : reverseColorizeStandard(string)).toList();
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
     * Colorizes a list of strings by converting hex color codes with '&' prefix
     * to Minecraft-compatible hex codes.
     *
     * @param lst The list of strings to colorize.
     * @return A list of hex-colorized strings.
     */
    public static List<String> colorizeHex(List<String> lst) {
        return lst.stream().map(string -> string == null ? null : colorizeHex(string)).toList();
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
     * Reverses hex colorization in a list of strings by converting Minecraft hex color codes
     * back to the format with '&' and hex values.
     *
     * @param lst The list of strings to reverse colorize.
     * @return A list of reverse-colorized hex strings.
     */
    public static List<String> reverseColorizeHex(List<String> lst) {
        return lst.stream().map(string -> string == null ? null : reverseColorizeHex(string)).toList();
    }
}