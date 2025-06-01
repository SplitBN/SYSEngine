package dev.splityosis.sysengine.guilib.layout;

import dev.splityosis.sysengine.guilib.PaneLayout;
import dev.splityosis.sysengine.guilib.intenral.AbstractPaneLayout;
import org.bukkit.event.inventory.InventoryType;

import java.util.OptionalInt;

/**
 * A PaneLayout that corresponds to the slots of the parent inventory
 */
public class AbsoluteLayout extends AbstractPaneLayout {

    private int size;
    private boolean initialized = false;

    @Override
    public PaneLayout initialize(InventoryType type, int size) {
        initialized = true;
        this.size = size;
        return this;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public OptionalInt toRawSlot(int localIndex) {
        if (localIndex < 0 || localIndex >= size)
            return OptionalInt.empty();

        return OptionalInt.of(localIndex);
    }

    @Override
    public OptionalInt toLocalSlot(int rawSlot) {
        if (rawSlot < 0 || rawSlot >= size)
            return OptionalInt.empty();

        return OptionalInt.of(rawSlot);
    }

    @Override
    public int getSlotCapacity() {
        return size;
    }
}
