package dev.splityosis.sysengine.guilib.layout;

import dev.splityosis.sysengine.guilib.intenral.AbstractPaneLayout;
import org.bukkit.event.inventory.InventoryType;

import java.util.*;

/**
 * A base class for layouts defined by a fixed list of raw slots.
 * Automatically handles local and raw mapping.
 */
public abstract class AbstractSlotsLayout<T extends AbstractSlotsLayout<T>> extends AbstractPaneLayout<T> {

    private List<Integer> rawSlots;
    private Map<Integer, Integer> rawToLocal;

    /**
     * Implementing class should return the ordered list of raw slot indexes.
     */
    protected abstract List<Integer> generateSlots(InventoryType type, int size);

    @Override
    public void onInitialize(InventoryType type, int size) {
        this.rawSlots = generateSlots(type, size);

        if (!rawSlots.isEmpty()) {
            int max = rawSlots.stream().mapToInt(Integer::intValue).max().getAsInt();
            int min = rawSlots.stream().mapToInt(Integer::intValue).min().getAsInt();
            if (max >= size || min < 0)
                throw new IllegalArgumentException("Slots must not be larger or equal than the inventory's size (" + size + ") or less than 0.");
        }

        this.rawToLocal = new HashMap<>(rawSlots.size());
        for (int i = 0; i < rawSlots.size(); i++) {
            rawToLocal.put(rawSlots.get(i), i);
        }
    }

    @Override
    public OptionalInt convertToRawSlot(int localSlot) {
        if (localSlot < 0 || localSlot >= rawSlots.size()) return OptionalInt.empty();
        return OptionalInt.of(rawSlots.get(localSlot));
    }

    @Override
    public OptionalInt convertToLocalSlot(int rawSlot) {
        Integer local = rawToLocal.get(rawSlot);
        return local != null ? OptionalInt.of(local) : OptionalInt.empty();
    }

    @Override
    public int getMaxCapacity() {
        return rawSlots.size();
    }
}
