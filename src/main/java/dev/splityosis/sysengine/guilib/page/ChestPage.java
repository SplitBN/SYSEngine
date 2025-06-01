package dev.splityosis.sysengine.guilib.page;

import dev.splityosis.sysengine.guilib.intenral.AbstractGuiPage;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ChestPage extends AbstractGuiPage {

    private int size;

    public ChestPage(int size) {
        this.size = size;
    }

    @Override
    protected Inventory createInventory(String title, InventoryHolder holder) {
        return Bukkit.createInventory(holder, size, title);
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.CHEST;
    }

    @Override
    public int getInventorySize() {
        return size;
    }
}
