package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.components.GuiItem;
import dev.splityosis.sysengine.guilib.components.Pane;
import dev.splityosis.sysengine.guilib.events.GuiEvent;
import dev.splityosis.sysengine.guilib.events.GuiItemClickEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractGuiItem implements GuiItem, Cloneable {

    private AbstractPane parentPane;

    private ItemStack itemStack = new ItemStack(Material.AIR);
    private GuiEvent<GuiItemClickEvent> onClick = e->{};

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
    public GuiItem onClick(GuiEvent<GuiItemClickEvent> onClick) {
        this.onClick = onClick;
        return this;
    }

    @Override
    public GuiEvent<GuiItemClickEvent> getOnClick() {
        return onClick;
    }

    @Override
    public GuiItem clone() {
        try {
            AbstractGuiItem guiItem = (AbstractGuiItem) super.clone();
            guiItem.setParentPane(null);
            return guiItem;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
