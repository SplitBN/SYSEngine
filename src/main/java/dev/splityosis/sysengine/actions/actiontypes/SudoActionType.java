package dev.splityosis.sysengine.actions.actiontypes;

import dev.splityosis.sysengine.actions.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class SudoActionType implements ActionType {

    @Override
    public String getName() {
        return "sudo";
    }

    @Override
    public List<String> getAliases() {
        return List.of("runCommand", "forceCommand");
    }

    @Override
    public String getDescription() {
        return "Makes the target run a command";
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
        if (!(target instanceof CommandSender commandSender)) return;

        params = applyPlaceholders(commandSender instanceof Player player ? player : null, params, replacements);
        String command = params.get(0);
        Bukkit.dispatchCommand(commandSender, command);
    }
}