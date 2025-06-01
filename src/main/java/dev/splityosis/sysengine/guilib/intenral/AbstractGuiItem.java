package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.GuiItem;
import dev.splityosis.sysengine.guilib.Pane;
import dev.splityosis.sysengine.guilib.events.GuiItemClickEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public abstract class AbstractGuiItem implements GuiItem {

    private AbstractPane parentPane;

    private ItemStack itemStack = new ItemStack(Material.AIR);
    private Consumer<GuiItemClickEvent> onClick = e->{};

    public AbstractGuiItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public AbstractGuiItem() {

    }

    @Override
    public Pane getParentPane() {
        return parentPane;
    }

    protected void setParentPane(AbstractPane parentPane) {
        this.parentPane = parentPane;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public GuiItem setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    @Override
    public GuiItem update() {
        if (parentPane != null) {
            AbstractGuiPage page = parentPane.getParentPage();
            if (page != null) {
                int slot = page.getSlot(this);
                if (slot != -1)
                    page.redrawItemInSlot(slot);
            }
        }
        return this;
    }

    @Override
    public GuiItem onClick(Consumer<GuiItemClickEvent> onClick) {
        this.onClick = onClick;
        return this;
    }
}
