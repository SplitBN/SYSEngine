package dev.splityosis.sysengine.actions.actiontypes;

import dev.splityosis.sysengine.actions.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TeleportActionType implements ActionType {

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("tp");
    }

    @Override
    public String getDescription() {
        return "Teleports the target to a specified location";
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("world", "x", "y", "z");
    }

    @Override
    public List<String> getOptionalParameters() {
        return Arrays.asList("yaw", "pitch");
    }

    @Override
    public void execute(Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements) {
        if (!(target instanceof Player)) return;
        Player player = (Player) target;

        params = applyPlaceholders(player, params, replacements);
        double x = Double.parseDouble(params.get(1));
        double y = Double.parseDouble(params.get(2));
        double z = Double.parseDouble(params.get(3));
        String worldName = params.get(0);

        float yaw = params.size() > 4 ? Float.parseFloat(params.get(4)) : 0;
        float pitch = params.size() > 5 ? Float.parseFloat(params.get(5)) : 0;

        Location location = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
        player.teleport(location);
    }
}