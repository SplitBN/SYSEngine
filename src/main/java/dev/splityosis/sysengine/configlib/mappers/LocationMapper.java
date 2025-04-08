package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;

import java.text.DecimalFormat;

public class LocationMapper implements AbstractMapper<Location> {

    @Override
    public Location getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String locString = section.getString(path);
        if (locString == null || locString.isEmpty()) {
            return null;
        }

        String[] parts = locString.split(" ");
        if (parts.length < 4) {
            return null;
        }

        // Parse (world, x, y, z)
        String worldName = parts[0];
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);

        float yaw = (parts.length >= 5) ? Float.parseFloat(parts[4]) : 0.0f;
        float pitch = (parts.length >= 6) ? Float.parseFloat(parts[5]) : 0.0f;

        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    @Override
    public void setInConfig(ConfigManager manager, Location instance, ConfigurationSection section, String path) {
        if (instance == null) {
            section.set(path, "");
            return;
        }

        String worldName = instance.getWorld().getName();
        String x = formatNumber(instance.getX());
        String y = formatNumber(instance.getY());
        String z = formatNumber(instance.getZ());
        String yaw = formatNumber(instance.getYaw());
        String pitch = formatNumber(instance.getPitch());

        String locString = String.join(" ", worldName, x, y, z, yaw, pitch);
        section.set(path, locString);
    }


    private static String formatNumber(double d) {
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        if (d == Math.floor(d))
            return String.valueOf((int) d);
        else
            return decimalFormat.format(d);
    }

}
