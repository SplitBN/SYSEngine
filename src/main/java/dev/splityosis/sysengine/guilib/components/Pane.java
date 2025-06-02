package dev.splityosis.sysengine.guilib.components;

import dev.splityosis.sysengine.guilib.events.*;

import java.util.Map;

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

    Pane onClick(GuiEvent<PaneClickEvent> onClick);

    Pane onOpen(GuiEvent<PaneOpenEvent> onOpen);

    Pane onClose(GuiEvent<PaneCloseEvent> onClose);

    Pane setOnItemPreClick(GuiEvent<GuiItemPreClickEvent> onItemClick);

    GuiEvent<PaneClickEvent> getOnClick();

    GuiEvent<PaneOpenEvent> getOnOpen();

    GuiEvent<PaneCloseEvent> getOnClose();

    GuiEvent<GuiItemPreClickEvent> getOnItemPreClick();

}
