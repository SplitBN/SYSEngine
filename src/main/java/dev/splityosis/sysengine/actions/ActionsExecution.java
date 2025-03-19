package dev.splityosis.sysengine.actions;

import dev.splityosis.sysengine.SYSEngine;
import org.bukkit.Bukkit;

import java.util.*;

public class ActionsExecution {

    private final ActionTypeRegistry actionTypeRegistry = ActionTypeRegistry.get();
    private final Actions actions;
    private final Object target;
    private Map<String, String> replacements;
    private final Iterator<ActionDefinition> actionDefinitionsIterator;

    public ActionsExecution(Actions actions, Object target, Map<String, String> replacements) {
        this.actions = actions;
        this.target = target;
        this.replacements = replacements;
        if (this.replacements == null)
            this.replacements = new HashMap<>();
        this.actionDefinitionsIterator = actions.getActionDefinitions().iterator();
    }
    public void execute() {
        processLine();
    }

    private void processLine() {
        if (!actionDefinitionsIterator.hasNext())
            return;

        ActionDefinition actionDefinition = actionDefinitionsIterator.next();
        List<String> params = actionDefinition.getParameters();
        if (params == null)
            params = new ArrayList<>();

        // Handle wait keyword
        if (waitAliases.contains(actionDefinition.getActionType())) {
            if (params.size() != 1)
                Bukkit.getLogger().warning("Invalid number of parameters for " + actionDefinition.getActionType());
            else {
                try {
                    int wait = Integer.parseInt(params.get(0));
                    if (wait <= 0)
                        throw new RuntimeException();

                    Bukkit.getScheduler().runTaskLater(SYSEngine.plugin, this::processLine, wait);
                    return;
                } catch (Exception e) {
                    Bukkit.getLogger().warning("wait keyword expects {<gameticks>}, a positive integer.");
                }
            }
            processLine();
            return;
        }

        // Handle rest
        List<ActionType> actionTypes = actionTypeRegistry.getActionTypes(actionDefinition.getActionType().toLowerCase());
        if (actionTypes == null || actionTypes.isEmpty())
            Bukkit.getLogger().warning("Action type with identifier '" + actionDefinition.getActionType() + "' does exist");
        else {
            ActionType actionType = actionTypeRegistry.getActionType(actionDefinition.getActionType().toLowerCase(), params.size());
            if (actionType == null) {
                Bukkit.getLogger().warning("Invalid number of parameters for ActionType '" + actionDefinition.getActionType() + "', Expected: " + generateUsages(actionTypes));
            }
            else {
                try {
                    actionType.execute(target, params, replacements);
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Exception caught while executing actionType '" + actionDefinition.getActionType() + "' with parameters: " + params);
                    e.printStackTrace();
                }
            }
        }

        processLine();
    }

    private String generateUsages(List<ActionType> actionTypes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ActionType actionType : actionTypes)
            stringBuilder.append(generateUsage(actionType)).append("OR ");

        return stringBuilder.substring(0, stringBuilder.length() - 3);
    }

    private String generateUsage(ActionType actionType) {
        List<String> parameters = actionType.getParameters();
        List<String> optionalParameters = actionType.getOptionalParameters();
        StringBuilder stringBuilder = new StringBuilder(actionType.getName() + ": \"");
        if (parameters != null)
            parameters.forEach(string -> stringBuilder.append("{<").append(string).append(">} "));
        if (optionalParameters != null)
            optionalParameters.forEach(string -> stringBuilder.append("{[<").append(string).append(">]} "));
        return stringBuilder.substring(0, stringBuilder.length() - 1) + "\"";
    }

    public final static Set<String> waitAliases = new HashSet<>(Arrays.asList("wait", "sleep", "pause", "delay"));

    public Actions getActions() {
        return actions;
    }

    public Iterator<ActionDefinition> getActionDefinitionsIterator() {
        return actionDefinitionsIterator;
    }

    public Map<String, String> getReplacements() {
        return replacements;
    }

    public Object getTarget() {
        return target;
    }

    public ActionTypeRegistry getActionTypeRegistry() {
        return actionTypeRegistry;
    }
}
