package dev.splityosis.sysengine.actions.actiontypes;

import com.cryptomorin.xseries.messages.ActionBar;
import dev.splityosis.sysengine.actions.ActionType;
import dev.splityosis.sysengine.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class SendActionBarAllActionType implements ActionType {

    @Override
    public String getName() {
        return "sendActionBarAll";
    }

    @Override
    public List<String> getAliases() {
        return List.of("actionBarAll");
    }

    @Override
    public String getDescription() {
        return "Sends an action bar message to all players";
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

        for (Player player : Bukkit.getOnlinePlayers())
            ActionBar.sendActionBar(player, ColorUtil.colorize(message));
    }
}