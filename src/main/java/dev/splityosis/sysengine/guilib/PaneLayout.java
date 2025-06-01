package dev.splityosis.sysengine.guilib;

import org.bukkit.event.inventory.InventoryType;

import java.util.OptionalInt;

/**
 * Maps between a pane's local slot indexes and raw inventory slots.
 */
public interface PaneLayout {

    /**
     * Called once when this layout is added to a page, before any rendering happens.
     * @param type the InventoryType of the parent gui
     * @param size the size (slot count) of the parent gui
     */
    PaneLayout initialize(InventoryType type, int size);

    boolean isInitialized();

    /**
     * Converts a local index (from the pane) to a raw inventory slot.
     *
     * @param localIndex index provided by the pane
     * @return raw inventory slot, or empty if not part of the layout
     */
    OptionalInt toRawSlot(int localIndex);

    /**
     * Converts a raw inventory slot to a local index, if this layout handles it.
     *
     * @param rawSlot raw inventory slot clicked or referenced
     * @return local index, or empty if the slot doesn't belong to this layout
     */
    OptionalInt toLocalSlot(int rawSlot);

    /**
     * Returns how many local indexes this layout supports.
     */
    int getSlotCapacity();
}
