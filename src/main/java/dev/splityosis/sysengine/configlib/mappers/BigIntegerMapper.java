package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;

import java.math.BigInteger;

public class BigIntegerMapper implements AbstractMapper<BigInteger> {

    @Override
    public BigInteger getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {
        String str = section.getString(path);
        if (str == null) {
            return new BigInteger("0");
        }
        return new BigInteger(str);
    }

    @Override
    public void setInConfig(ConfigManager manager, BigInteger instance, ConfigurationSection section, String path) {
        section.set(path, instance.toString());
    }
}
