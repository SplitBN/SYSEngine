package dev.splityosis.sysengine.guilib.page;

import dev.splityosis.sysengine.guilib.intenral.AbstractGuiPage;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;


public class ChestPage extends AbstractGuiPage {

    @Override
    protected Inventory createInventory(String title, InventoryHolder holder) {
        return null;
    }

    @Override
    public InventoryType getInventoryType() {
        return null;
    }

    @Override
    public int getInventorySize() {
        return 0;
    }
}
