package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import org.bukkit.util.Vector;
import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;

import java.text.DecimalFormat;

public class VectorMapper implements AbstractMapper<Vector> {

    @Override
    public Vector getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String vecString = section.getString(path);
        if (vecString == null || vecString.isEmpty()) {
            return null;
        }

        String[] parts = vecString.split(" ");
        if (parts.length != 3) {
            return null;
        }

        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double z = Double.parseDouble(parts[2]);

        return new Vector(x, y, z);
    }

    @Override
    public void setInConfig(ConfigManager manager, Vector instance, ConfigurationSection section, String path) {
        if (instance == null) {
            section.set(path, ""); // Set to an empty string if vector is null
            return;
        }

        String vecString = String.format(formatNumber(instance.getX()), formatNumber(instance.getY()), formatNumber(instance.getZ()));
        section.set(path, vecString);
    }

    private static String formatNumber(double d) {
        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        if (d == Math.floor(d))
            return String.valueOf((int) d);
        else
            return decimalFormat.format(d);
    }
}
