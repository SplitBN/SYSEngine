package dev.splityosis.sysengine.guilib.pane;

import dev.splityosis.sysengine.guilib.GuiItem;
import dev.splityosis.sysengine.guilib.GuiPage;
import dev.splityosis.sysengine.guilib.PaneLayout;
import dev.splityosis.sysengine.guilib.intenral.AbstractPane;

import java.util.LinkedHashMap;
import java.util.Map;

public class StaticPane extends AbstractPane {

    private Map<Integer, GuiItem> items;

    public StaticPane(PaneLayout layout) {
        super(layout);
        items = new LinkedHashMap<>(layout.getSlotCapacity());
    }

    @Override
    public void onAttach(GuiPage page) {

    }

    @Override
    public Map<Integer, GuiItem> getLocalItems() {
        return items;
    }

    /**
     * Sets an item in a local slot.
     * @return This pane.
     */
    public StaticPane setItem(int slot, GuiItem item) {
        registerItem(item);

        if (item == null)
            removeItem(slot);
        else
            items.put(slot, item);
        return this;
    }

    /**
     * Removes an item in a certain slot.
     * @return The GuiItem that was removed or null if nothing was there.
     */
    public GuiItem removeItem(int slot) {
        GuiItem item = items.remove(slot);
        if (item != null)
            unregisterItem(item);
        return item;
    }

    /**
     * Removes an item from the pane.
     * @return True if it existed and was removed.
     */
    public boolean removeItem(GuiItem item) {
        unregisterItem(item);
        return items.entrySet().removeIf(e -> e.getValue().equals(item));
    }

    @Override
    public int getSlot(GuiItem item) {
        return items.entrySet().stream()
                .filter(e -> e.getValue().equals(item))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(-1);
    }

    /**
     * Gets the first empty slot within bounds.
     * @param from Lower local slot index bound.
     * @param to Upper local slot index bound.
     * @return The first empty slot or -1 if none empty.
     */
    public int firstEmptySlot(int from, int to) {
        if (from > to)
            throw new IllegalArgumentException("from > to");

        if (from < 0 || to >= getLayout().getSlotCapacity())
            throw new IndexOutOfBoundsException("Slot index out of bounds");

        for (int i = from; i <= to; i++) {
            if (!items.containsKey(i))
                return i;
        }

        return -1;
    }

    /**
     * Gets the first empty slot.
     @return The first empty slot or -1 if none empty.
     */
    public int firstEmptySlot() {
        return firstEmptySlot(0, getLayout().getSlotCapacity() -1);
    }

}
