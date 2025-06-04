package dev.splityosis.sysengine.guilib.layout;

import dev.splityosis.sysengine.guilib.InventoryTypeInfo;
import dev.splityosis.sysengine.guilib.intenral.AbstractPaneLayout;
import org.bukkit.event.inventory.InventoryType;


import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

/**
 * A layout that fills a rectangular box inside the inventory.
 * <p>
 * defined by the box's starting slot, width and height.
 * <p>
 * Example: In a 6x9 inventory, a 3x3 box starting from slot 21 would map:
 * [0, 1, 2, 3, 4, 5, 6, 7, 8] <-> [21, 22, 23, 30, 31, 32, 39, 40, 41].
 */

public class BoxLayout extends AbstractPaneLayout<BoxLayout> {

    private final int startRawSlot;
    private final int width;
    private final int height;

    private int guiWidth;
    private int guiHeight;

    private int startRow;
    private int startCol;

    private Map<Integer, Integer> localToRaw = new HashMap<>();
    private Map<Integer, Integer> rawToLocal = new HashMap<>();

    /**
     * @param startSlot the raw slot index in the inventory where this region starts
     * @param width number of columns in the region
     * @param height number of rows in the region
     */
    public BoxLayout(int startSlot, int width, int height) {
        this.startRawSlot = startSlot;
        this.width = width;
        this.height = height;
    }

    @Override
    public void onInitialize(InventoryType type, int size) {
        this.guiWidth = InventoryTypeInfo.getWidth(type);

        if (guiWidth < 1)
            throw new IllegalArgumentException("Layout doesn't support InventoryType '"+type.name()+"'");

        this.guiHeight = InventoryTypeInfo.getHeight(type, size);

        this.startRow = startRawSlot / guiWidth;
        this.startCol = startRawSlot % guiWidth;

        if (startRow + height > guiHeight)
            throw new IllegalArgumentException("Layout out of bounds for row '"+(startRow + height));

        if (startCol + width > guiWidth)
            throw new IllegalArgumentException("Layout out of bounds for col '"+(startCol + width));

        localToRaw.clear();
        rawToLocal.clear();

        int local = 0;
        for (int row = 0; row < height; row++)
            for (int col = 0; col < width; col++) {
                int raw = startRawSlot + col + row * guiWidth;
                localToRaw.put(local, raw);
                rawToLocal.put(raw, local);
                local++;
            }
    }

    @Override
    public OptionalInt convertToRawSlot(int localSlot) {
        Integer rawSlot = localToRaw.get(localSlot);
        return rawSlot == null ? OptionalInt.empty() : OptionalInt.of(rawSlot);
    }

    @Override
    public OptionalInt convertToLocalSlot(int rawSlot) {
        Integer localSlot = rawToLocal.get(rawSlot);
        return localSlot == null ? OptionalInt.empty() : OptionalInt.of(localSlot);
    }

    @Override
    public int getMaxCapacity() {
        return width*height;
    }
}
