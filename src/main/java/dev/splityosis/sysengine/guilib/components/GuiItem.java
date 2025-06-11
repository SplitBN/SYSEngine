package dev.splityosis.sysengine.guilib.components;

import dev.splityosis.sysengine.guilib.builder.item.GuiItemBuilder;
import dev.splityosis.sysengine.guilib.events.GuiEvent;
import dev.splityosis.sysengine.guilib.events.GuiItemClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;

/**
 * Represents an item displayed within a pane in the GUI.
 */
public interface GuiItem {

    /**
     * Returns the display ItemStack of this GUI item.
     *
     * @return the item stack
     */
    ItemStack getItemStack();

    /**
     * Returns the parent pane this item belongs to.
     *
     * @return the parent pane, or null if not attached
     */
    Pane getParentPane();

    /**
     * Sets the ItemStack this GuiItem displays
     *
     * @param itemStack the item to display
     * @return this GuiItem instance
     */
    @Contract("_ -> this")
    GuiItem setItemStack(ItemStack itemStack);

    /**
     * Sets a callback to be triggered when this GuiItem is clicked.
     *
     * @param onClick the click event consumer
     * @return this GuiItem instance
     */
    @Contract("_ -> this")
    GuiItem onClick(GuiEvent<GuiItemClickEvent> onClick);

    /**
     * Returns the current on-click callback.
     *
     * @return the click handler
     */
    GuiEvent<GuiItemClickEvent> getOnClick();

    /**
     * Triggers a refresh on the item. updating its ItemStack if needed.
     *
     * @return this GuiItem instance
     */
    @Contract("-> this")
    GuiItem refresh();

    /**
     * Creates a deep clone of this GUI item.
     *
     * @return the cloned GuiItem
     */
    @Contract("-> new")
    GuiItem clone();

    /**
     * Sets the visibility of this item.
     * Invisible items will not be rendered or handle interactions.
     *
     * @param visible true to make visible, false to hide
     * @return this GuiItem instance
     */
    @Contract("_ -> this")
    GuiItem setVisible(boolean visible);

    /**
     * Returns whether this item is currently visible.
     *
     * @return true if visible, false otherwise
     */
    boolean isVisible();

    /**
     * Start a builder from a fixed item.
     */
    static GuiItemBuilder of(ItemStack stack) {
        return GuiItemBuilder.of(stack);
    }

    /**
     * Start a builder from a supplier.
     */
    static GuiItemBuilder of(Supplier<ItemStack> supplier) {
        return GuiItemBuilder.of(supplier);
    }
}
