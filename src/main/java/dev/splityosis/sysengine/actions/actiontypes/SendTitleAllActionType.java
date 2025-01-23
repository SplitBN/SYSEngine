package dev.splityosis.sysengine.actions.actiontypes;

import com.cryptomorin.xseries.messages.Titles;
import dev.splityosis.sysengine.actions.ActionType;
import dev.splityosis.sysengine.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SendTitleAllActionType implements ActionType {

    @Override
    public String getName() {
        return "sendTitleAll";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("titleAll");
    }

    @Override
    public String getDescription() {
        return "Sends a title and subtitle to all players";
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("title", "subtitle");
    }

    @Override
    public List<String> getOptionalParameters() {
        return Arrays.asList("fadeIn", "stay", "fadeOut");
    }

    @Override
    public void execute(Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements) {
        params = applyPlaceholders(null, params, replacements);
        String title = ColorUtil.colorize(params.get(0));
        String subtitle = ColorUtil.colorize(params.get(1));
        int fadeIn = params.size() > 2 ? Integer.parseInt(params.get(2)) : 10;
        int stay = params.size() > 3 ? Integer.parseInt(params.get(3)) : 70;
        int fadeOut = params.size() > 4 ? Integer.parseInt(params.get(4)) : 20;

        for (Player player : Bukkit.getOnlinePlayers())
            Titles.sendTitle(player, fadeIn, stay, fadeOut, ColorUtil.colorize(title), ColorUtil.colorize(subtitle));
    }
}