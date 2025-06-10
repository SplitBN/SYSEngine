package dev.splityosis.sysengine.guilib.components;

import dev.splityosis.sysengine.guilib.events.GuiEvent;
import dev.splityosis.sysengine.guilib.events.GuiPageClickEvent;
import dev.splityosis.sysengine.guilib.events.GuiPageCloseEvent;
import dev.splityosis.sysengine.guilib.events.GuiPageOpenEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a single page within a GUI.
 * Holds panes, manages layout, rendering, and event handling for that page.
 */
public interface GuiPage {

    /**
     * Returns the inventory type of this page.
     */
    InventoryType getInventoryType();

    /**
     * Returns the size of the inventory.
     */
    int getInventorySize();

    /**
     * Returns the parent GUI this page belongs to, or null if it's not part of any GUI.
     */
    Gui getParentGui();

    /**
     * Returns the title of this page.
     */
    String getTitle();

    /**
     * Sets the title of this page.
     */
    @Contract("_ -> this")
    GuiPage setTitle(String title);

    /**
     * Returns the number of panes added to this page.
     */
    int getPaneCount();

    /**
     * Adds a pane to the page.
     */
    @Contract("_ -> this")
    GuiPage addPane(Pane pane);

    /**
     * Adds a pane and allows further configuration using the provided consumer.
     */
    @Contract("_, _ -> this")
    <T extends Pane> GuiPage addPane(T pane, Consumer<T> setup);

    /**
     * Removes a pane from the page.
     */
    @Contract("_ -> this")
    GuiPage removePane(Pane pane);

    /**
     * Returns all panes currently in the page, ordered by weights.
     */
    List<Pane> getPanes();

    /**
     * Returns all pane layers, ordered by pane weights.
     */
    List<PaneLayer> getPaneLayers();

    /**
     * Refreshes the entire page.
     */
    @Contract("-> this")
    GuiPage refresh();

    /**
     * Refreshes only the given pane.
     */
    @Contract("_ -> this")
    GuiPage refresh(Pane pane);

    /**
     * Refreshes the slot at the given index.
     */
    @Contract("_ -> this")
    GuiPage refresh(int slot);

    /**
     * Returns the slot index where the given GuiItem is currently rendered.
     * Returns -1 if not found.
     */
    int getSlot(GuiItem guiItem);

    /**
     * Returns the GuiItem currently at the given slot, or null if empty.
     */
    GuiItem getItem(int slot);

    /**
     * Returns a map of all currently rendered items in this page, by raw slot index.
     */
    Map<Integer, GuiItem> getCurrentItems();

    /**
     * Returns a list of all players currently viewing this page.
     */
    List<Player> getViewers();

    /**
     * Handles an inventory click event targeted at this page.
     */
    @Contract("_ -> this")
    GuiPage handleClick(InventoryClickEvent event);

    /**
     * Handles an inventory drag event targeted at this page.
     */
    @Contract("_ -> this")
    GuiPage handleDrag(InventoryDragEvent event);

    /**
     * Sets a callback to be triggered when this page is opened.
     */
    @Contract("_ -> this")
    GuiPage onOpen(GuiEvent<GuiPageOpenEvent> onOpen);

    /**
     * Sets a callback to be triggered when this page is closed.
     */
    @Contract("_ -> this")
    GuiPage onClose(GuiEvent<GuiPageCloseEvent> onClose);

    /**
     * Sets a callback to be triggered when this page is clicked.
     */
    @Contract("_ -> this")
    GuiPage onClick(GuiEvent<GuiPageClickEvent> onClick);

    /**
     * Returns the current on open callback.
     */
    GuiEvent<GuiPageOpenEvent> getOnOpen();

    /**
     * Returns the current on-close callback.
     */
    GuiEvent<GuiPageCloseEvent> getOnClose();

    /**
     * Returns the current on-click callback.
     */
    GuiEvent<GuiPageClickEvent> getOnClick();
}
