package dev.splityosis.sysengine.guilib.components;

import dev.splityosis.sysengine.guilib.events.GuiEvent;
import dev.splityosis.sysengine.guilib.events.GuiPageClickEvent;
import dev.splityosis.sysengine.guilib.events.GuiPageCloseEvent;
import dev.splityosis.sysengine.guilib.events.GuiPageOpenEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface GuiPage {

    InventoryType getInventoryType();

    int getInventorySize();

    Gui getParentGui();

    String getTitle();

    GuiPage setTitle(String title);

    int getPanesAmount();

    GuiPage addPane(Pane pane);

    default <T extends Pane> GuiPage addPane(T pane, Consumer<T> setup) {
        setup.accept(pane);
        return addPane(pane);
    }

    GuiPage removePane(Pane pane);

    List<Pane> getPanes();

    List<PaneLayer> getPaneLayers();

    GuiPage render();

    GuiPage render(Pane pane);

    GuiPage render(int slot);

    int getSlot(GuiItem guiItem);

    GuiItem getItem(int slot);

    Map<Integer, GuiItem> getCurrentItems();

    List<Player> getViewers();

    GuiPage handleClick(InventoryClickEvent event);

    GuiPage onOpen(GuiEvent<GuiPageOpenEvent> onOpen);

    GuiPage onClose(GuiEvent<GuiPageCloseEvent> onClose);

    GuiPage onClick(GuiEvent<GuiPageClickEvent> onClick);

    GuiEvent<GuiPageOpenEvent> getOnOpen();

    GuiEvent<GuiPageCloseEvent> getOnClose();

    GuiEvent<GuiPageClickEvent> getOnClick();

}