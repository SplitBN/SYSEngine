package dev.splityosis.sysengine.configlib.configuration;

import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface AbstractMapper<T> {

    T getFromConfig(ConfigManager manager, ConfigurationSection section, String path); // Get the object from the config

    void setInConfig(ConfigManager manager, T instance, ConfigurationSection section, String path); // Set the object in the config

    Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})"); // Pattern for matching hex color codes
    /**
     * Translates color codes in the given string and applies color formatting.
     * 
     * This method translates alternate color codes using the '&' character and
     * replaces hex color codes with their corresponding ChatColor values.
     * 
     * @param str The input string containing color codes.
     * @return The colorized string with applied color formatting, or null if the input string is null.
     */
    default String colorize(String str) {
        if (str == null) return null;
        Matcher matcher = HEX_PATTERN.matcher(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', str));
        StringBuffer buffer = new StringBuffer();

        while (matcher.find())
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group(1)).toString());

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    /**
     * Applies colorization to each string in the provided list.
     *
     * @param lst the list of strings to be colorized; if null, the method returns null
     * @return a new list with each string colorized, or null if the input list is null
     */
    default List<String> colorize(List<String> lst){
        if (lst == null) return null;
        List<String> newList = new ArrayList<>();
        lst.forEach(s -> {
            newList.add(colorize(s));
        });
        return newList;
    }

    /**
     * Reverses the colorization of each string in the provided list.
     * If the input list is null, it returns null.
     *
     * @param lst the list of strings to be reverse colorized
     * @return a new list with each string reverse colorized, or null if the input list is null
     */
    default List<String> reverseColorize(List<String> lst) {
        if (lst == null) return null;
        List<String> newLst = new ArrayList<>();
        for (String s : lst)
            newLst.add(reverseColorize(s));
        return newLst;
    }

    Pattern patternAll = Pattern.compile("§x(§[0-9a-fA-F]){6}"); // Pattern for matching hex color codes
    /**
     * Reverses the color codes in the given input string.
     * 
     * This method converts Minecraft color codes (prefixed with '§') to HTML-like color codes (prefixed with '&#').
     * For example, '§a' becomes '&#a'.
     * 
     * @param input the input string containing Minecraft color codes
     * @return the input string with Minecraft color codes replaced by HTML-like color codes
     */
    default String reverseColorize(String input) {
        Matcher matcher = patternAll.matcher(input);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String colorCode = matcher.group().replaceAll("§", "");
            matcher.appendReplacement(sb, "&#" + colorCode.substring(1));
        }
        matcher.appendTail(sb);

        return sb.toString().replaceAll("§([0-9a-fklmnorx])", "&$1");
    }
}
