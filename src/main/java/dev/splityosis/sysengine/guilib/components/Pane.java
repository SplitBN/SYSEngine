package dev.splityosis.sysengine.guilib.components;

import dev.splityosis.sysengine.guilib.events.*;

import org.jetbrains.annotations.Contract;

import java.util.Map;
import java.util.Set;

/**
 * Represents a container of GUI items that uses a {@link PaneLayout} to position elements within a GUI page.
 * Panes are rendered onto pages and may support visibility toggles, weighted ordering and
 * event listeners.
 */
public interface Pane {

    /**
     * Returns the map of local GUIItems within this pane,
     * indexed by their local slot (layout index).
     * <p>
     *     Note: This isn't necessarily what is shown on the gui.
     * </p>
     */
    Map<Integer, GuiItem> getLocalItems();

    /**
     * Sets the rendering weight of this pane. Higher weights render later (on top).
     */
    @Contract("_ -> this")
    Pane setWeight(int weight);

    /**
     * Returns the rendering weight of this pane.
     */
    int getWeight();

    /**
     * Returns whether this pane is currently visible.
     * Invisible panes are not rendered or interactable.
     */
    boolean isVisible();

    /**
     * Returns whether this pane is currently attached to a {@link GuiPage}.
     */
    boolean isAttached();

    /**
     * Sets the visibility of this pane.
     */
    @Contract("_ -> this")
    Pane setVisible(boolean visible);

    /**
     * Triggers a full refresh of the pane’s content.
     */
    @Contract("-> this")
    Pane refresh();

    /**
     * Triggers a refresh of a single local slot in this pane.
     *
     * @param slot the local layout slot to refresh
     */
    @Contract("_ -> this")
    Pane refresh(int slot);

    /**
     * Returns the layout this pane uses to map local slot to raw inventory slots.
     */
    PaneLayout getLayout();

    /**
     * Returns the GUI page this pane is attached to, or {@code null} if not attached.
     */
    GuiPage getParentPage();

    /**
     * Returns if the specified interaction type is currently allowed.
     */
    boolean isInteractionAllowed(GuiInteraction interaction);

    /**
     * Returns a set of the allowed interaction types.
     */
    Set<GuiInteraction> getAllowedInteractions();

    /**
     * Sets a callback to be triggered when this pane is clicked.
     */
    @Contract("_ -> this")
    Pane onClick(GuiEvent<PaneClickEvent> onClick);

    /**
     * Sets a callback to be triggered when this pane is opened.
     */
    @Contract("_ -> this")
    Pane onOpen(GuiEvent<PaneOpenEvent> onOpen);

    /**
     * Sets a callback to be triggered when this pane is closed.
     */
    @Contract("_ -> this")
    Pane onClose(GuiEvent<PaneCloseEvent> onClose);

    /**
     * Sets a callback that fires before a GUI item within this pane is clicked.
     * Can be used for conditional behavior or early cancellation.
     */
    @Contract("_ -> this")
    Pane onItemPreClick(GuiEvent<GuiItemPreClickEvent> onItemClick);

    /**
     * Returns the current on-click callback.
     */
    GuiEvent<PaneClickEvent> getOnClick();

    /**
     * Returns the current on-open callback.
     */
    GuiEvent<PaneOpenEvent> getOnOpen();

    /**
     * Returns the current on-close callback.
     */
    GuiEvent<PaneCloseEvent> getOnClose();

    /**
     * Returns the current pre-item-click callback.
     */
    GuiEvent<GuiItemPreClickEvent> getOnItemPreClick();
}
