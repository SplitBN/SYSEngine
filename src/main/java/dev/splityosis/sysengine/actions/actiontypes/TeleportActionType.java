package dev.splityosis.sysengine.actions.actiontypes;

import dev.splityosis.sysengine.actions.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class TeleportActionType implements ActionType {

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public List<String> getAliases() {
        return List.of("tp");
    }

    @Override
    public String getDescription() {
        return "Teleports the target to a specified location";
    }

    @Override
    public List<String> getParameters() {
        return List.of("x", "y", "z");
    }

    @Override
    public List<String> getOptionalParameters() {
        return List.of("world");
    }

    @Override
    public void execute(Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements) {
        if (!(target instanceof Player player)) return;

        params = applyPlaceholders(player, params, replacements);
        double x = Double.parseDouble(params.get(0));
        double y = Double.parseDouble(params.get(1));
        double z = Double.parseDouble(params.get(2));
        String worldName = params.size() > 3 ? params.get(3) : player.getWorld().getName();

        Location location = new Location(Bukkit.getWorld(worldName), x, y, z);
        player.teleport(location);
    }
}