package dev.splityosis.sysengine.actions.actiontypes;

import dev.splityosis.sysengine.actions.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ConsoleCommandActionType implements ActionType {

    @Override
    public String getName() {
        return "runConsoleCommand";
    }

    @Override
    public List<String> getAliases() {
        return List.of("consoleCommand");
    }

    @Override
    public String getDescription() {
        return "Executes a command as the console";
    }

    @Override
    public List<String> getParameters() {
        return List.of("command");
    }

    @Override
    public List<String> getOptionalParameters() {
        return List.of();
    }

    @Override
    public void execute(Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements) {
        params = applyPlaceholders(target instanceof Player player ? player : null, params, replacements);
        String command = params.get(0);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}