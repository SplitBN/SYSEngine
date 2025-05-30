package dev.splityosis.sysengine.guilib;

import dev.splityosis.sysengine.guilib.events.GuiPageCloseEvent;
import dev.splityosis.sysengine.guilib.events.GuiPageOpenEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;
import java.util.function.Consumer;

public interface GuiPage {

    InventoryType getInventoryType();

    int getInventorySize();

    Gui getParentGui();

    String getTitle();

    GuiPage setTitle(String title);

    int getPanesAmount();

    GuiPage addPane(Pane pane);

    default GuiPage addPane(Pane pane, Consumer<Pane> setup) {
        setup.accept(pane);
        return addPane(pane);
    }

    GuiPage removePane(Pane pane);

    List<Pane> getPanes();

    GuiPage render();

    GuiItem getItem(int slot);

    GuiPage handleClick(InventoryClickEvent event);

    GuiPage setOnOpen(Consumer<GuiPageOpenEvent> onOpen);

    GuiPage setOnClose(Consumer<GuiPageCloseEvent> onClose);

}