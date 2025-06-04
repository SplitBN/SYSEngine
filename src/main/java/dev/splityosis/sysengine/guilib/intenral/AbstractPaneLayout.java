package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.components.PaneLayout;
import org.bukkit.event.inventory.InventoryType;

import java.util.OptionalInt;

public abstract class AbstractPaneLayout<T extends AbstractPaneLayout<?>> implements PaneLayout {

    private T self = (T) this;
    private boolean initialized;

    public T self() {
        return self;
    }

    @Override
    public final boolean isInitialized() {
        return initialized;
    }

    @Override
    public final T initialize(InventoryType type, int size) {
        initialized = true;
        onInitialize(type, size);
        return self;
    }

    @Override
    public final OptionalInt toLocalSlot(int rawSlot) {
        if (!isInitialized())
            throw new IllegalStateException("Layout not initialized, Layout gets initialized upon attachment to a GuiPage");

        return convertToLocalSlot(rawSlot);
    }

    @Override
    public final OptionalInt toRawSlot(int localSlot) {
        if (!isInitialized())
            throw new IllegalStateException("Layout not initialized, Layout gets initialized upon attachment to a GuiPage");

        return convertToRawSlot(localSlot);
    }

    @Override
    public final int getCapacity() {
        if (!isInitialized())
            throw new IllegalStateException("Layout not initialized, Layout gets initialized upon attachment to a GuiPage");

        return getMaxCapacity();
    }

    @Override
    public boolean containsLocalSlot(int localSlot) {
        if (!isInitialized())
            throw new IllegalStateException("Layout not initialized, Layout gets initialized upon attachment to a GuiPage");
        return localSlot >= 0 && localSlot < getMaxCapacity();
    }

    @Override
    public boolean containsRawSlot(int rawSlot) {
        if (!isInitialized())
            throw new IllegalStateException("Layout not initialized, Layout gets initialized upon attachment to a GuiPage");
        return convertToLocalSlot(rawSlot).isPresent();
    }

    protected abstract void onInitialize(InventoryType type, int size);

    /**
     * Converts a local index (from the pane) to a raw inventory slot.
     *
     * @param localSlot index provided by the pane
     * @return raw inventory slot, or empty if not part of the layout
     */
    protected abstract OptionalInt convertToRawSlot(int localSlot);

    /**
     * Converts a raw inventory slot to a local index, if this layout handles it.
     *
     * @param rawSlot raw inventory slot clicked or referenced
     * @return local index, or empty if the slot doesn't belong to this layout
     */
    protected abstract OptionalInt convertToLocalSlot(int rawSlot);

    /**
     * Returns how many local indexes this layout supports.
     */
    protected abstract int getMaxCapacity();

}
