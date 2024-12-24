package dev.splityosis.sysengine.actions.actiontypes;

import com.cryptomorin.xseries.messages.ActionBar;
import dev.splityosis.sysengine.actions.ActionType;
import dev.splityosis.sysengine.utils.ColorUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class SendActionBarActionType implements ActionType {

    @Override
    public String getName() {
        return "sendActionBar";
    }

    @Override
    public List<String> getAliases() {
        return List.of("actionBar");
    }

    @Override
    public String getDescription() {
        return "Sends an action bar message to the target";
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
        if (!(target instanceof Player player)) return;

        params = applyPlaceholders(player, params, replacements);
        String message = ColorUtil.colorize(params.get(0));
        ActionBar.sendActionBar(player, ColorUtil.colorize(message));    }
}