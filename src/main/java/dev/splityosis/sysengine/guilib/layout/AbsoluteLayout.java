package dev.splityosis.sysengine.guilib.layout;

import dev.splityosis.sysengine.guilib.components.PaneLayout;
import dev.splityosis.sysengine.guilib.intenral.AbstractPaneLayout;
import org.bukkit.event.inventory.InventoryType;

import java.util.OptionalInt;

/**
 * A PaneLayout that corresponds to the slots of the parent inventory
 */
public class AbsoluteLayout extends AbstractPaneLayout {

    private int size;

    @Override
    public void onInitialize(InventoryType type, int size) {
        this.size = size;
    }


    @Override
    public OptionalInt convertToRawSlot(int localIndex) {
        if (localIndex < 0 || localIndex >= size)
            return OptionalInt.empty();

        return OptionalInt.of(localIndex);
    }

    @Override
    public OptionalInt convertToLocalSlot(int rawSlot) {
        if (rawSlot < 0 || rawSlot >= size)
            return OptionalInt.empty();

        return OptionalInt.of(rawSlot);
    }

    @Override
    public int getMaxCapacity() {
        return size;
    }
}
