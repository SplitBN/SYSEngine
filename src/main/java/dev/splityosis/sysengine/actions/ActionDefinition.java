package dev.splityosis.sysengine.actions;

import java.util.Arrays;
import java.util.List;

public class ActionDefinition {
    private final String actionType;
    private final List<String> parameters;

    public ActionDefinition(String actionType, List<String> parameters) {
        this.actionType = actionType;
        this.parameters = parameters;
    }

    public ActionDefinition(String actionType, String... parameters) {
        this.actionType = actionType;
        this.parameters = Arrays.asList(parameters);
    }

    public String getActionType() {
        return actionType;
    }

    public List<String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "ActionDefinition{" +
                "actionType='" + actionType + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}