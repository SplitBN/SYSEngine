package dev.splityosis.sysengine.actions;

import dev.splityosis.sysengine.SYSEngine;
import dev.splityosis.sysengine.actions.actiontypes.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Singleton class for managing ActionType instances.
 */
public class ActionTypeRegistry {

    private static ActionTypeRegistry instance;

    private final Map<String, Map<Integer, ActionType>> actionTypesByNameAndParamCount = new HashMap<>();

    private final Set<ActionType> actionTypeSet = new HashSet<>();

    // Private constructor to prevent instantiation
    private ActionTypeRegistry() {}

    /**
     * Retrieves the singleton instance of ActionTypeRegistry.
     *
     * @return The singleton instance.
     */
    public static synchronized ActionTypeRegistry get() {
        if (instance == null)
            initialize();
        return instance;
    }

    /**
     * Registers an ActionType with the registry.
     * Overrides existing registrations if any.
     *
     * @param actionType The ActionType instance to register.
     */
    public void registerActionType(ActionType actionType) {
        Objects.requireNonNull(actionType, "ActionType cannot be null.");

        int requiredCount = actionType.getParameters().size();
        int maxCount = requiredCount + actionType.getOptionalParameters().size();

        String primaryName = actionType.getName().toLowerCase();
        registerByParamCounts(primaryName, actionType, requiredCount, maxCount);

        for (String alias : actionType.getAliases()) {
            String lowerAlias = alias.toLowerCase();
            registerByParamCounts(lowerAlias, actionType, requiredCount, maxCount);
        }

        actionTypeSet.add(actionType);
    }

    /**
     * Registers the ActionTypes with the registry.
     * Overrides existing registrations if any.
     *
     * @param actionTypes An array of ActionTypes to register.
     */
    public void registerActionTypes(ActionType... actionTypes) {
        Objects.requireNonNull(actionTypes, "ActionTypes cannot be null.");
        for (ActionType actionType : actionTypes)
            registerActionType(actionType);
    }

    /**
     * Retrieves an ActionType by its name or alias and the specified parameter count.
     *
     * @param name       The name or alias of the ActionType.
     * @param paramCount The number of parameters to match.
     * @return The corresponding ActionType instance, or {@code null} if not found.
     */
    public ActionType getActionType(String name, int paramCount) {
        if (name == null)
            return null;

        Map<Integer, ActionType> byParamCount = actionTypesByNameAndParamCount.get(name.toLowerCase());
        if (byParamCount == null) {
            return null;
        }
        return byParamCount.get(paramCount);
    }

    /**
     * Retrieves a list of all ActionType instances that are registered under a given name or alias,
     * ignoring parameter count.
     *
     * @param name The name or alias of the ActionType.
     * @return A list of matching ActionTypes, or an empty list if none are found.
     */
    public List<ActionType> getActionTypes(String name) {
        if (name == null)
            return Collections.emptyList();

        Map<Integer, ActionType> byParamCount = actionTypesByNameAndParamCount.get(name.toLowerCase());
        if (byParamCount == null) {
            return Collections.emptyList();
        }

        return new ArrayList<>(byParamCount.values());
    }

    /**
     * Checks if an ActionType with the given name or alias is registered
     * for any parameter count.
     *
     * @param name The name or alias to check.
     * @return {@code true} if registered, {@code false} otherwise.
     */
    public boolean isRegistered(String name) {
        if (name == null) {
            return false;
        }
        return actionTypesByNameAndParamCount.containsKey(name.toLowerCase());
    }

    /**
     * Retrieves all registered ActionType instances.
     *
     * @return An unmodifiable collection of all registered ActionTypes.
     */
    public Collection<ActionType> getAllActionTypes() {
        return Collections.unmodifiableCollection(actionTypeSet);
    }

    /**
     * Initializes whatever needs to be initialized for actions.
     * You should never call this, look at {@link SYSEngine#initialize(JavaPlugin)}.
     */
    public static void initialize() {
        if (instance != null) return;
        instance = new ActionTypeRegistry();
        instance.registerActionTypes(
                new MessageActionType(),
                new MessagePlayerActionType(),
                new ConsoleCommandActionType(),
                new SudoActionType(),
                new SendActionBarActionType(),
                new TeleportActionType(),
                new MessageAllActionType(),
                new PlaySoundActionType(),
                new PlaySoundAllActionType(),
                new SendTitleActionType(),
                new SendTitleAllActionType(),
                new SendActionBarAllActionType()
        );
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    /**
     * Registers an ActionType under the given identifier for all applicable parameter counts.
     */
    private void registerByParamCounts(String identifier, ActionType actionType, int minCount, int maxCount) {
        Map<Integer, ActionType> map = actionTypesByNameAndParamCount
                .computeIfAbsent(identifier, k -> new HashMap<>());

        // For all possible param counts the action can handle (min -> max)
        for (int count = minCount; count <= maxCount; count++) {
            map.put(count, actionType);
        }
    }
}
