package dev.splityosis.sysengine.guilib.item;

import dev.splityosis.sysengine.guilib.intenral.AbstractGuiItem;
import org.bukkit.inventory.ItemStack;

public class DefaultGuiItem extends AbstractGuiItem<DefaultGuiItem> {

    public DefaultGuiItem(ItemStack itemStack) {
        super(itemStack);
    }

    public DefaultGuiItem() {
        super(null);
    }
}
