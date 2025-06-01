package dev.splityosis.sysengine.guilib;

import dev.splityosis.sysengine.guilib.events.GuiItemClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public interface GuiItem {

    ItemStack getItemStack();

    Pane getParentPane();

    GuiItem setItemStack(ItemStack itemStack);

    GuiItem onClick(Consumer<GuiItemClickEvent> onClick);

    GuiItem update();
}