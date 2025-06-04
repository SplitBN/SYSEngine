package dev.splityosis.sysengine.guilib.layout;

import dev.splityosis.sysengine.guilib.intenral.AbstractPaneLayout;
import org.bukkit.event.inventory.InventoryType;

import java.util.OptionalInt;

/**
 * A layout that fills every slot of the inventory.
 * <p>
 * Local index 0 maps to slot 0, 1 maps to 1, and so on.
 * <p>
 * Example: A 3x9 inventory has 27 usable slots indexed from 0 to 26.
 */

public class FullLayout extends AbstractPaneLayout<FullLayout> {

    private int size;

    @Override
    public void onInitialize(InventoryType type, int size) {
        this.size = size;
    }


    @Override
    public OptionalInt convertToRawSlot(int localSlot) {
        if (localSlot < 0 || localSlot >= size)
            return OptionalInt.empty();

        return OptionalInt.of(localSlot);
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
