package dev.splityosis.sysengine.actions.actiontypes;

import dev.splityosis.sysengine.actions.ActionType;
import dev.splityosis.sysengine.utils.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MessageActionType implements ActionType {

    @Override
    public String getName() {
        return "sendMessage";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("message", "msg");
    }

    @Override
    public String getDescription() {
        return "Sends a message to the target";
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
    public void execute(Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements) throws IllegalArgumentException {
        if (target == null) return;
        if (! (target instanceof CommandSender)) return;
        CommandSender commandSender = (CommandSender) target;
        params = applyPlaceholders(target instanceof Player ? (Player) target : null, params, replacements);
        commandSender.sendMessage(ColorUtil.colorize(params.get(0)));
    }
}
