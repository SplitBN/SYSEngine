package dev.splityosis.sysengine.actions.actiontypes;

import dev.splityosis.sysengine.actions.ActionType;
import dev.splityosis.sysengine.utils.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MessageActionType implements ActionType {

    @Override
    public String getName() {
        return "sendMessage";
    }

    @Override
    public List<String> getAliases() {
        return List.of("message", "msg");
    }

    @Override
    public String getDescription() {
        return "Sends a message to the target";
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
    public void execute(Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements) throws IllegalArgumentException {
        if (target == null) return;
        if (! (target instanceof CommandSender commandSender)) return;
        params = applyPlaceholders(target instanceof Player player ? player : null, params, replacements);
        commandSender.sendMessage(ColorUtil.colorize(params.get(0)));
    }
}
