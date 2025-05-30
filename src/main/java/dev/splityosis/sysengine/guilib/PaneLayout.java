package dev.splityosis.sysengine.guilib;

import org.bukkit.event.inventory.InventoryType;

import java.util.OptionalInt;

/**
 * Maps between a pane's local slot indexes and raw inventory slots.
 */
public interface PaneLayout {

    /**
     * Converts a local index (from the pane) to a raw inventory slot.
     *
     * @param localIndex index provided by the pane
     * @return raw inventory slot, or empty if not part of the layout
     */
    OptionalInt toRawSlot(InventoryType inventoryType, int inventorySize, int localIndex);

    /**
     * Converts a raw inventory slot to a local index, if this layout handles it.
     *
     * @param rawSlot raw inventory slot clicked or referenced
     * @return local index, or empty if the slot doesn't belong to this layout
     */
    OptionalInt toLocalSlot(InventoryType inventoryType, int inventorySize, int rawSlot);

    /**
     * Returns how many local indexes this layout supports.
     */
    int getSlotCapacity();
}
