package dev.splityosis.sysengine.configlib.mappers;

import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;
import dev.splityosis.sysengine.configlib.configuration.ConfigMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.function.Function;

import java.util.ArrayList;
import java.util.List;

public class FunctionMapper implements ConfigMapper<Function> {

    @Field("variables-DONT-TOUCH") String variables;
    @Field("domains") List<String> domains;

    @Override
    public Function compile(ConfigManager manager, ConfigurationSection section, String path) {
        String[] split = variables.split(",");

        for (int i = 0; i < split.length; i++)
            split[i] = split[i].trim();

        return Function.builder(domains).variables(split).build();
    }

    @Override
    public void decompile(ConfigManager manager, Function instance, ConfigurationSection section, String path) {
        variables = String.join(", ", instance.getVariableNames());
        domains = new ArrayList<>(instance.getLines());
    }
}
