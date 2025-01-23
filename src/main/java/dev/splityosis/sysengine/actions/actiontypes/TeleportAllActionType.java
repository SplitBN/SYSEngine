package dev.splityosis.sysengine.actions.actiontypes;

import dev.splityosis.sysengine.actions.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TeleportAllActionType implements ActionType {

    @Override
    public String getName() {
        return "teleportAll";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("tpAll");
    }

    @Override
    public String getDescription() {
        return "Teleports all players to a specified location";
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("x", "y", "z");
    }

    @Override
    public List<String> getOptionalParameters() {
        return Arrays.asList("world");
    }

    @Override
    public void execute(Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements) {

        params = applyPlaceholders(null, params, replacements);

        double x = Double.parseDouble(params.get(0));
        double y = Double.parseDouble(params.get(1));
        double z = Double.parseDouble(params.get(2));
        World world = params.size() > 3 ? Bukkit.getWorld(params.get(3)) : null;


        for (Player player : Bukkit.getOnlinePlayers()) {
            if (world == null)
                world = player.getWorld();
            Location location = new Location(world, x, y, z);
            player.teleport(location);
        }
    }
}
