package dev.splityosis.sysengine.guilib.item;

import dev.splityosis.sysengine.guilib.intenral.AbstractGuiItem;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class DefaultGuiItem extends AbstractGuiItem<DefaultGuiItem> {

    private Supplier<ItemStack> itemSupplier;

    public DefaultGuiItem(ItemStack itemStack) {
        super(null);
        this.itemSupplier = () -> itemStack;
    }

    public DefaultGuiItem(Supplier<ItemStack> itemSupplier) {
        super(null);
        this.itemSupplier = itemSupplier;
    }

    @Override
    public ItemStack getItemStack() {
        return itemSupplier.get();
    }
}
