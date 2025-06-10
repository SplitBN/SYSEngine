package dev.splityosis.sysengine.guilib.page;

import dev.splityosis.sysengine.common.function.BiSupplier;
import dev.splityosis.sysengine.guilib.intenral.AbstractGuiPage;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class DefaultPage extends AbstractGuiPage<DefaultPage> {

    private BiSupplier<String, InventoryHolder, Inventory> creator;
    private int size;

    public DefaultPage(int size) {
        this.size = size;
        creator = (s, holder) -> Bukkit.createInventory(holder, size, s);
    }

    public DefaultPage(InventoryType type) {
        size = type.getDefaultSize();
        creator = (s, holder) -> Bukkit.createInventory(holder, type, s);
    }

    @Override
    protected Inventory createInventory(String title, InventoryHolder holder) {
        return creator.get(title, holder);
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
