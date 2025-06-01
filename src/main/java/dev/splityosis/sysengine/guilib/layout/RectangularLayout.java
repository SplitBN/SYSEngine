package dev.splityosis.sysengine.guilib.layout;

import dev.splityosis.sysengine.guilib.InventoryTypeInfo;
import dev.splityosis.sysengine.guilib.PaneLayout;
import dev.splityosis.sysengine.guilib.intenral.AbstractPaneLayout;
import org.bukkit.event.inventory.InventoryType;


import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

/**
 * A rectangular region layout.
 */

public class RectangularLayout extends AbstractPaneLayout {

    private final int startRawSlot;
    private final int width;
    private final int height;

    private int guiWidth;
    private int guiHeight;

    private int startRow;
    private int startCol;

    private Map<Integer, Integer> localToRaw = new HashMap<>();
    private Map<Integer, Integer> rawToLocal = new HashMap<>();

    private boolean initialized = false;

    /**
     * @param startSlot the raw slot index in the inventory where this region starts
     * @param width number of columns in the region
     * @param height number of rows in the region
     */
    public RectangularLayout(int startSlot, int width, int height) {
        this.startRawSlot = startSlot;
        this.width = width;
        this.height = height;
    }

    @Override
    public PaneLayout initialize(InventoryType type, int size) {
        this.guiWidth = InventoryTypeInfo.getWidth(type);
        this.initialized = true;

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

        return this;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public OptionalInt toRawSlot(int localIndex) {
        if (localIndex >= localToRaw.size() || localIndex < 0)
            return OptionalInt.empty();

        return OptionalInt.of(localToRaw.get(localIndex));
    }

    @Override
    public OptionalInt toLocalSlot(int rawSlot) {
        if (rawSlot >= rawToLocal.size() || rawSlot < 0)
            return OptionalInt.empty();

        return OptionalInt.of(rawToLocal.get(rawSlot));
    }

    @Override
    public int getSlotCapacity() {
        return width*height;
    }
}
