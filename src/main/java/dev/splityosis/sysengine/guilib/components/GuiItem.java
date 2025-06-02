package dev.splityosis.sysengine.guilib.components;

import dev.splityosis.sysengine.guilib.events.GuiEvent;
import dev.splityosis.sysengine.guilib.events.GuiItemClickEvent;
import org.bukkit.inventory.ItemStack;

public interface GuiItem {

    ItemStack getItemStack();

    Pane getParentPane();

    GuiItem setItemStack(ItemStack itemStack);

    GuiItem onClick(GuiEvent<GuiItemClickEvent> onClick);

    GuiEvent<GuiItemClickEvent> getOnClick();

    GuiItem update();

    GuiItem clone();
}