package dev.splityosis.sysengine.actions.actiontypes;

import dev.splityosis.sysengine.actions.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConsoleCommandActionType implements ActionType {

    @Override
    public String getName() {
        return "runConsoleCommand";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("consoleCommand");
    }

    @Override
    public String getDescription() {
        return "Executes a command as the console";
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("command");
    }

    @Override
    public List<String> getOptionalParameters() {
        return Arrays.asList();
    }

    @Override
    public void execute(Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements) {
        params = applyPlaceholders(target instanceof Player ? (Player) target: null, params, replacements);
        String command = params.get(0);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}