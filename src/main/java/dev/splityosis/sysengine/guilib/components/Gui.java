package dev.splityosis.sysengine.guilib.components;

import dev.splityosis.sysengine.guilib.events.GuiCloseEvent;
import dev.splityosis.sysengine.guilib.events.GuiEvent;
import dev.splityosis.sysengine.guilib.events.GuiOpenEvent;
import dev.splityosis.sysengine.guilib.gui.DefaultGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a GUI that supports multiple pages, each containing panes and items.
 * Allows managing pages, opening the GUI for players, and listening to open/close events.
 */
public interface Gui {

    /**
     * Returns the index of the currently active page.
     */
    int getActivePageIndex();

    /**
     * Returns the total number of pages in this GUI.
     */
    int getPageCount();

    /**
     * Returns the page at the specified index.
     *
     * @param index the page index
     */
    GuiPage getPage(int index);

    /**
     * Returns the currently active page.
     */
    GuiPage getActivePage();

    /**
     * Sets the active page by index.
     *
     * @param index the page index to activate
     */
    @Contract("_ -> this")
    Gui setActivePage(int index);

    /**
     * Adds a new DefaultPage to this GUI.
     *
     * @param rows The amount of rows from 1-6.
     * @param setup a consumer for configuring the page
     */
    @Contract("_, _, _ -> this")
    Gui addPage(String title, int rows, Consumer<GuiPage> setup);

    /**
     * Adds a new DefaultPage to this GUI.
     *
     * @param rows The amount of rows from 1-6.
     * @param setup a consumer for configuring the page
     */
    @Contract("_, _ -> this")
    Gui addPage(int rows, Consumer<GuiPage> setup);

    /**
     * Adds a new DefaultPage to this GUI.
     *
     * @param setup a consumer for configuring the page
     */
    @Contract("_, _, _ -> this")
    Gui addPage(String title, InventoryType type, Consumer<GuiPage> setup);

    /**
     * Adds a new DefaultPage to this GUI.
     *
     * @param setup a consumer for configuring the page
     */
    @Contract("_, _ -> this")
    Gui addPage(InventoryType type, Consumer<GuiPage> setup);

    /**
     * Adds a new page to this GUI.
     *
     * @param page the page to add
     */
    @Contract("_ -> this")
    Gui addPage(GuiPage page);

    /**
     * Adds a new page and applies the given configuration.
     *
     * @param page the page to add
     * @param setup a consumer for configuring the page
     * @param <E> the type of the page
     */
    @Contract("_, _ -> this")
    <E extends GuiPage> Gui addPage(E page, Consumer<E> setup);

    /**
     * Returns all pages added to this GUI.
     */
    List<GuiPage> getPages();

    /**
     * Opens the GUI to the specified player.
     *
     * @param player the player to open the GUI for
     */
    @Contract("_ -> this")
    Gui open(Player player);

    /**
     * Returns all players currently viewing this GUI.
     */
    Collection<Player> getViewers();

    /**
     * Sets the callback to run when this GUI is opened.
     */
    @Contract("_ -> this")
    Gui onOpen(GuiEvent<GuiOpenEvent> onOpen);

    /**
     * Sets the callback to run when this GUI is closed.
     */
    @Contract("_ -> this")
    Gui onClose(GuiEvent<GuiCloseEvent> onClose);

    /**
     * Returns the current open callback.
     */
    GuiEvent<GuiOpenEvent> getOnOpen();

    /**
     * Returns the current close callback.
     */
    GuiEvent<GuiCloseEvent> getOnClose();

    static Gui create() {
        return new DefaultGui();
    }
}
