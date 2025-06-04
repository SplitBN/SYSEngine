package dev.splityosis.sysengine.guilib.layout;

import org.bukkit.event.inventory.InventoryType;

import java.util.*;

/**
 * A layout defined explicitly by raw slot indexes.
 */
public class SlotsLayout extends AbstractSlotsLayout<SlotsLayout> {

    private final List<Integer> rawSlots;

    public SlotsLayout(List<Integer> rawSlots) {
        this.rawSlots = rawSlots;
    }

    public SlotsLayout(Integer... rawSlots) {
        this(Arrays.asList(rawSlots));
    }

    @Override
    protected List<Integer> generateSlots(InventoryType type, int size) {
        return rawSlots;
    }

    public List<Integer> getRawSlots() {
        return rawSlots;
    }
}
