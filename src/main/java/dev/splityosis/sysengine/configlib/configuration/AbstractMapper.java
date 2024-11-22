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

    T getFromConfig(ConfigManager manager, ConfigurationSection section, String path);

    void setInConfig(ConfigManager manager, T instance, ConfigurationSection section, String path);

    @SuppressWarnings("unchecked")
    default Class<?> getGenericClass(){
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) superClass.getActualTypeArguments()[0];
    }

    Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");
    default String colorize(String str) {
        if (str == null) return null;
        Matcher matcher = HEX_PATTERN.matcher(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', str));
        StringBuffer buffer = new StringBuffer();

        while (matcher.find())
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group(1)).toString());

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    default List<String> colorize(List<String> lst){
        if (lst == null) return null;
        List<String> newList = new ArrayList<>();
        lst.forEach(s -> {
            newList.add(colorize(s));
        });
        return newList;
    }

    default List<String> reverseColorize(List<String> lst) {
        if (lst == null) return null;
        List<String> newLst = new ArrayList<>();
        for (String s : lst)
            newLst.add(reverseColorize(s));
        return newLst;
    }

    Pattern patternAll = Pattern.compile("§x(§[0-9a-fA-F]){6}");
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
