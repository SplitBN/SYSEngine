package dev.splityosis.sysengine.guilib;

import dev.splityosis.sysengine.guilib.events.GuiCloseEvent;
import dev.splityosis.sysengine.guilib.events.GuiOpenEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface Gui {

    int getCurrentPageIndex();

    int getPagesAmount();

    GuiPage getPage(int index);

    GuiPage getCurrentPage();

    Gui setCurrentPage(int index);

    Gui addPage(GuiPage page);

    default Gui addPage(GuiPage page, Consumer<GuiPage> setup) {
        setup.accept(page);
        return addPage(page);
    }

    List<GuiPage> getPages();

    Gui open(Player player);

    Collection<Player> getViewers();

    Gui handleClick(InventoryClickEvent event);

    Gui setOnOpen(Consumer<GuiOpenEvent> onOpen);

    Gui setOnClose(Consumer<GuiCloseEvent> onClose);

}