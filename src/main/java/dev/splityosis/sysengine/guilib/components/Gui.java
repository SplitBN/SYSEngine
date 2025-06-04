package dev.splityosis.sysengine.guilib.components;

import dev.splityosis.sysengine.guilib.events.GuiCloseEvent;
import dev.splityosis.sysengine.guilib.events.GuiEvent;
import dev.splityosis.sysengine.guilib.events.GuiOpenEvent;
import org.bukkit.entity.Player;

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
    Gui setActivePage(int index);

    /**
     * Adds a new page to this GUI.
     *
     * @param page the page to add
     */
    Gui addPage(GuiPage page);

    /**
     * Adds a new page and applies the given configuration.
     *
     * @param page  the page to add
     * @param setup a consumer for configuring the page
     * @param <E>   the type of the page
     */
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
    Gui open(Player player);

    /**
     * Returns all players currently viewing this GUI.
     */
    Collection<Player> getViewers();

    /**
     * Sets the callback to run when this GUI is opened.
     */
    Gui onOpen(GuiEvent<GuiOpenEvent> onOpen);

    /**
     * Sets the callback to run when this GUI is closed.
     */
    Gui onClose(GuiEvent<GuiCloseEvent> onClose);

    /**
     * Returns the current open callback.
     */
    GuiEvent<GuiOpenEvent> getOnOpen();

    /**
     * Returns the current close callback.
     */
    GuiEvent<GuiCloseEvent> getOnClose();
}