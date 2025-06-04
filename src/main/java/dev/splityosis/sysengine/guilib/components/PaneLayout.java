package dev.splityosis.sysengine.guilib.components;

import org.bukkit.event.inventory.InventoryType;

import java.util.OptionalInt;

/**
 * Represents a layout strategy for mapping between local pane slot indexes
 * and raw inventory slots in the GUI.
 */
public interface PaneLayout {

    /**
     * Initializes this layout with the parent GUI's inventory type and size.
     * Called once when the layout is added to a page.
     *
     * @param type the inventory type of the parent GUI
     * @param size the total slot count of the parent GUI
     * @return this PaneLayout instance
     */
    PaneLayout initialize(InventoryType type, int size);

    /**
     * Returns whether this layout has been initialized.
     *
     * @return true if initialized, false otherwise
     */
    boolean isInitialized();

    /**
     * Maps a local slot index to the corresponding raw inventory slot.
     *
     * @param localSlot the index from the pane
     * @return the raw slot in the inventory, or empty if unmapped
     */
    OptionalInt toRawSlot(int localSlot);

    /**
     * Maps a raw inventory slot to the corresponding local index,
     * if this layout handles it.
     *
     * @param rawSlot the raw inventory slot
     * @return the local slot index, or empty if not handled
     */
    OptionalInt toLocalSlot(int rawSlot);

    /**
     * Returns the number of local slots supported by this layout.
     *
     * @return the capacity of this layout
     */
    int getCapacity();

    /**
     * Checks if the given raw inventory slot is handled by this layout.
     *
     * @param rawSlot the raw inventory slot
     * @return true if contained in this layout
     */
    boolean containsRawSlot(int rawSlot);

    /**
     * Checks if the given local slot index is valid for this layout.
     *
     * @param localSlot the pane's local index
     * @return true if contained in this layout
     */
    boolean containsLocalSlot(int localSlot);
}
