package dev.splityosis.sysengine.actions;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class makes it easy for configurators to define actions in configuration files.
 * It saves developers time by allowing dynamic behavior to be configured without code changes.
 *
 * <h3>Example Usage:</h3>
 * <pre>
 * {@code
 * // Define actions programmatically
 * Actions actions = new ActionsBuilder()
 *     .sendMessage("&aWelcome, player!")
 *     .playSound("LEVELUP")
 *     .teleport(100, 65, 200, "world")
 *     .wait(200)
 *     .sendMessage("Don't look back")
 *     .build();
 * actions.execute(targetPlayer);
 * }
 * </pre>
 *
 * <h3>Configuration Example (YAML):</h3>
 * <pre>
 * {@code
 * on-game-start:
 * - sendMessage: '&aWelcome, player!'
 * - playSound: 'LEVELUP'
 * - teleport: '{100} {65} {200} {world}'
 * - wait: 200
 * - message: '&cDon\'t look back'
 * }
 * </pre>
 */

public class Actions {

    private final List<ActionDefinition> actionDefinitions;

    /**
     * Creates an empty Actions object.
     */
    public Actions() {
        this(new ArrayList<>());
    }

    /**
     * Creates an Actions object with the specified action definitions.
     * @param actionDefinitions the action definitions to include.
     */
    public Actions(ActionDefinition... actionDefinitions) {
        this(Arrays.asList(actionDefinitions));
    }

    /**
     * Creates an Actions object with the specified action definitions.
     * @param actionDefinitions the list of action definitions to include.
     */
    public Actions(List<ActionDefinition> actionDefinitions) {
        if (actionDefinitions == null)
            actionDefinitions = Collections.emptyList();
        this.actionDefinitions = actionDefinitions;
    }

    /**
     * Executes all actions with no target or replacements.
     */
    public void execute() {
        execute(null, (Map<String, String>) null);
    }

    /**
     * Executes all actions with a specific target.
     * @param target the target object for the actions.
     */
    public void execute(@Nullable Object target) {
        execute(target, (Map<String, String>) null);
    }

    /**
     * Executes all actions with a map of replacements.
     * @param replacements a map of placeholders and their replacements.
     */
    public void execute(@Nullable Map<String, String> replacements) {
        execute(null, replacements);
    }

    /**
     * Executes all actions with an array of replacements.
     * @param replacements an array where even indices are keys and odd indices are values.
     */
    public void execute(@Nullable String... replacements) {
        execute(null, replacements);
    }

    /**
     * Executes all actions with a specific target and an array of replacements.
     * @param target the target object for the actions.
     * @param replacements an array where even indices are keys and odd indices are values.
     */
    public void execute(@Nullable Object target, String... replacements) {
        Map<String, String> replacementsMap = new HashMap<>();
        for (int i = 0; i < replacements.length; i+=2)
            replacementsMap.put(replacements[i], replacements[i+1]);
        execute(target, replacementsMap);
    }

    /**
     * Executes all actions with a specific target and a map of replacements.
     * @param target the target object for the actions.
     * @param replacements a map of placeholders and their replacements.
     */
    public void execute(@Nullable Object target, @Nullable Map<String, String> replacements) {
        new ActionsExecution(this, target, replacements).execute();
    }

    /**
     * Returns the list of action definitions in this Actions object.
     * @return the list of action definitions.
     */
    public List<ActionDefinition> getActionDefinitions() {
        return actionDefinitions;
    }

    /**
     * Creates a new ActionsBuilder instance.
     * @return a new ActionsBuilder instance.
     */
    public static ActionsBuilder builder() {
        return new ActionsBuilder();
    }
}
