package dev.splityosis.sysengine.actions.actiontypes;

import dev.splityosis.sysengine.actions.ActionType;
import dev.splityosis.sysengine.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MessageAllActionType implements ActionType {

    @Override
    public String getName() {
        return "sendMessageAll";
    }

    @Override
    public List<String> getAliases() {
        return List.of("messageAll", "broadcast");
    }

    @Override
    public String getDescription() {
        return "Sends a message to all players";
    }

    @Override
    public List<String> getParameters() {
        return List.of("message");
    }

    @Override
    public List<String> getOptionalParameters() {
        return List.of();
    }

    @Override
    public void execute(Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements) {
        params = applyPlaceholders(null, params, replacements);
        String message = ColorUtil.colorize(params.get(0));
        Bukkit.broadcastMessage(message);
    }
}