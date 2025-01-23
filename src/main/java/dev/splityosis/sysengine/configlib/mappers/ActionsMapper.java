package dev.splityosis.sysengine.configlib.mappers;

import com.google.common.collect.Maps;
import dev.splityosis.sysengine.actions.ActionDefinition;
import dev.splityosis.sysengine.actions.Actions;
import dev.splityosis.sysengine.actions.ActionsParser;
import dev.splityosis.sysengine.configlib.configuration.AbstractMapper;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.configlib.bukkit.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ActionsMapper implements AbstractMapper<Actions> {

    @Override
    public Actions getFromConfig(ConfigManager manager, ConfigurationSection section, String path) {

        if (section.isList(path)) {
            List<?> list = section.getList(path);

            if (list == null || list.isEmpty())
                return new Actions();

            return ActionsParser.parseActions(list);
        }

        else if (section.isConfigurationSection(path)) {
            List<String> list = new ArrayList<>();

            ConfigurationSection actionsSection = section.getConfigurationSection(path);
            for (String key : actionsSection.getKeys(false))
                list.add(key + " " + actionsSection.getString(key));

            if (list.isEmpty())
                return new Actions();

            return ActionsParser.parseActions(list);
        }

        else
            return new Actions();
    }

    @Override
    public void setInConfig(ConfigManager manager, Actions instance, ConfigurationSection section, String path) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (ActionDefinition actionDefinition : instance.getActionDefinitions())
            list.add(Collections.singletonMap(actionDefinition.getActionType(), paramsToString(actionDefinition.getParameters())));

        section.set(path, list);
    }

    private static String paramsToString(List<String> params) {
        if (params == null || params.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();
        for (String param : params)
            sb.append("{").append(param).append("} ");
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
