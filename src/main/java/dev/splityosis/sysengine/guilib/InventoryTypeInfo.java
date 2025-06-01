package dev.splityosis.sysengine.guilib;

import org.bukkit.event.inventory.InventoryType;

public final class InventoryTypeInfo {

    private InventoryTypeInfo() {}

    /**
     * Returns how many columns this inventory has.
     */
    public static int getWidth(InventoryType type) {
        String n = type.name();

        switch (n) {
            case "CHEST":
            case "BARREL":
            case "ENDER_CHEST":
            case "PLAYER":
            case "SHULKER_BOX":
                return 9;
            case "HOPPER":
                return 5;
            case "DROPPER":
            case "DISPENSER":
            case "CRAFTER":
                return 3;
            case "ENCHANTING":
                return 2;
            default:
                return -1;
        }

    }

    /** Rows = size / columns. */
    public static int getHeight(InventoryType type, int size) {
        int w = getWidth(type);
        return w > 0 ? (size / w) : -1;
    }
}
