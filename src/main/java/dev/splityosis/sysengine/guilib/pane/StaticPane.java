package dev.splityosis.sysengine.guilib.pane;

import dev.splityosis.sysengine.guilib.components.GuiItem;
import dev.splityosis.sysengine.guilib.components.GuiPage;
import dev.splityosis.sysengine.guilib.components.PaneLayout;
import dev.splityosis.sysengine.guilib.exceptions.UnsupportedPaneOperationException;
import dev.splityosis.sysengine.guilib.intenral.AbstractPane;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores GuiItems in fixed slots. Items are set using local slot indexes similar to a bukkit inventory.
 */
public class StaticPane extends AbstractPane<StaticPane> {

    private Map<Integer, GuiItem> items;

    public StaticPane(PaneLayout layout) {
        super(layout);
        items = new LinkedHashMap<>();
    }

    @Override
    protected void onAttach(GuiPage page) {
        // Once layout is initialized reconstruct the map with initial capacity for optimization
        Map<Integer, GuiItem> temp = items;
        items = new LinkedHashMap<>(getLayout().getCapacity());
        items.putAll(temp);
    }

    @Override
    protected void onDirectItemSet(int localSlot, GuiItem item) {
        setItem(localSlot, item);
    }

    @Override
    public Map<Integer, GuiItem> getLocalItems() {
        return items;
    }


    /**
     * Clears all items in the pane.
     * @return This pane.
     */
    public StaticPane clear() {
        items.values().forEach(this::unregisterItem);
        items.clear();
        return this;
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
     * Adds an item in the first empty slot
     * @return The slot the item was set to or -1 if pane is full.
     */
    public int addItemAndGetSlot(GuiItem item) {
        int slot = firstEmptySlot();
        if (slot == -1)
            return -1;
        setItem(slot, item);
        return slot;
    }

    /**
     * Adds an item in the first empty slot if it exists
     */
    public StaticPane addItem(GuiItem item) {
        addItemAndGetSlot(item);
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

        if (from < 0 || to >= getLayout().getCapacity())
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
        return firstEmptySlot(0, getLayout().getCapacity() -1);
    }

}
