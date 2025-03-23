package dev.splityosis.sysengine.actions.actiontypes;

import dev.splityosis.sysengine.actions.ActionType;
import dev.splityosis.sysengine.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MessageAllActionType implements ActionType {

    @Override
    public String getName() {
        return "sendMessageAll";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("messageAll", "broadcast");
    }

    @Override
    public String getDescription() {
        return "Sends a message to all players";
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("message");
    }

    @Override
    public List<String> getOptionalParameters() {
        return Arrays.asList();
    }

    @Override
    public void execute(Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements) {
        if (target instanceof Player)
            params = applyPlaceholders((Player) target, params, replacements);
        else
            params = applyPlaceholders(null, params, replacements);

        String message = ColorUtil.colorize(params.get(0));
        Bukkit.broadcastMessage(message);
    }
}