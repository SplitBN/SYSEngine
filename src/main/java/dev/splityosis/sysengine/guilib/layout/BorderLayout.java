package dev.splityosis.sysengine.guilib.layout;

import dev.splityosis.sysengine.guilib.InventoryTypeInfo;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;

/**
 * A layout that includes only the outer border slots in clockwise order
 * Top left -> Top right -> Bottom right -> Bottom left -> Top left.
 */
public class BorderLayout extends AbstractSlotsLayout<BorderLayout> {

    @Override
    protected List<Integer> generateSlots(InventoryType type, int size) {
        List<Integer> slots = new ArrayList<>();
        int columns = InventoryTypeInfo.getWidth(type);
        int rows = size / columns;

        // Top
        for (int col = 0; col < columns; col++)
            slots.add(col);

        // Right
        for (int row = 1; row < rows; row++)
            slots.add(row * columns + (columns - 1));

        // Bottom
        for (int col = columns - 2; col >= 0; col--)
            slots.add((rows - 1) * columns + col);

        // Left
        for (int row = rows - 2; row > 0; row--)
            slots.add(row * columns);

        return slots;
    }
}