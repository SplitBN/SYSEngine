package dev.splityosis.sysengine.utils;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class CommandUtil {

    private static final CommandMap commandMap;

    static {
        Server server = Bukkit.getServer();
        Class<?> craftServerClass = server.getClass();

        try {
            Field commandMapField = craftServerClass.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(server);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to get CommandMap", e);
        }
    }

    /**
     * Gets command map
     */
    public static CommandMap getCommandMap() {
        return commandMap;
    }

    /**
     * Creates a new instance of a {@link PluginCommand} with the specified name and plugin.
     * This method uses reflection to access the private constructor of {@link PluginCommand}.
     *
     * @param name The name of the command to be created.
     * @param plugin The JavaPlugin instance that owns this command.
     * @return A new instance of {@link PluginCommand} if successful, or {@code null} if an error occurs.
     */
    public static PluginCommand createPluginCommand(String name, JavaPlugin plugin) {
        try {
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            return constructor.newInstance(name, plugin);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
