package dev.splityosis.sysengine.guilib.builder.requirements;

import dev.splityosis.sysengine.commandlib.exception.RequirementNotMetException;
import dev.splityosis.sysengine.guilib.events.GuiItemClickEvent;

/**
 * A requirement that must be met before a GUI item executes its action.
 * <p>
 * Implements logic to evaluate a condition and optionally return a result.
 * Throws RequirementNotMetException on failure.
 *
 * @param <T> the type of result returned when the requirement is met (or Void if none).
 */
public interface GuiItemRequirement<T> {

    /**
     * Evaluates this requirement against the click event.
     * <p>
     * If the requirement is met, returns a result of type {@code T} (or null if none).
     * If the requirement is not met, throws RequirementNotMetException.
     *
     * @param event the item click event
     * @return a result of type T, or null if no result is required
     * @throws RequirementNotMetException if the requirement is not met
     */
    T evaluate(GuiItemClickEvent event) throws RequirementNotMetException;

    /**
     * Handles the failure case when {@link #evaluate} throws.
     * <p>
     * Can send a message to the player or perform other feedback logic.
     *
     * @param event the item click event
     * @param exception the exception thrown by evaluate()
     */
    void onNotMet(GuiItemClickEvent event, RequirementNotMetException exception);
}
