package dev.splityosis.sysengine.actions.actiontypes;

import dev.splityosis.sysengine.actions.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SudoActionType implements ActionType {

    @Override
    public String getName() {
        return "sudo";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("runCommand", "forceCommand");
    }

    @Override
    public String getDescription() {
        return "Makes the target run a command";
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
        if (!(target instanceof CommandSender)) return;
        CommandSender commandSender = (CommandSender) target;

        params = applyPlaceholders(commandSender instanceof Player ? (Player) commandSender: null, params, replacements);
        String command = params.get(0);
        Bukkit.dispatchCommand(commandSender, command);
    }
}