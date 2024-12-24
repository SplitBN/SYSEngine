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
    private final Map<String, ActionType> actionTypes = new HashMap<>();
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

        String primaryName = actionType.getName().toLowerCase();
        actionTypes.put(primaryName, actionType);
        actionTypeSet.add(actionType);

        for (String alias : actionType.getAliases()) {
            String lowerAlias = alias.toLowerCase();
            actionTypes.put(lowerAlias, actionType);
        }
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
     * Retrieves an ActionType by its name or alias.
     *
     * @param name The name or alias of the ActionType.
     * @return The corresponding ActionType instance, or {@code null} if not found.
     */
    public ActionType getActionType(String name) {
        if (name == null) {
            return null;
        }
        return actionTypes.get(name.toLowerCase());
    }

    /**
     * Checks if an ActionType with the given name or alias is registered.
     *
     * @param name The name or alias to check.
     * @return {@code true} if registered, {@code false} otherwise.
     */
    public boolean isRegistered(String name) {
        if (name == null) {
            return false;
        }
        return actionTypes.containsKey(name.toLowerCase());
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
    private static void initialize() {
        instance = new ActionTypeRegistry();
        instance.registerActionTypes(
                new MessageActionType(),
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
}
