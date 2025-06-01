package dev.splityosis.sysengine.guilib;

import dev.splityosis.sysengine.guilib.events.*;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.function.Consumer;

public interface Pane {

    Map<Integer, GuiItem> getLocalItems();

    Pane setWeight(int weight);

    int getWeight();

    boolean isVisible();

    default boolean isAttached() {
        return getParentPage() != null;
    }

    Pane setVisible(boolean visible);

    Pane render();

    Pane render(int slot);

    PaneLayout getLayout();

    GuiPage getParentPage();

    int getSlot(GuiItem guiItem);

    Pane handleClick(InventoryClickEvent event);

    Pane setOnClick(Consumer<PaneClickEvent> onClick);

    Pane setOnOpen(Consumer<PaneOpenEvent> onOpen);

    Pane setOnClose(Consumer<PaneCloseEvent> onClose);

}
