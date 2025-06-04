package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.components.GuiItem;
import dev.splityosis.sysengine.guilib.components.Pane;
import dev.splityosis.sysengine.guilib.events.GuiEvent;
import dev.splityosis.sysengine.guilib.events.GuiItemClickEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractGuiItem<T extends AbstractGuiItem<?>> implements GuiItem, Cloneable {

    private T self = (T) this;
    private AbstractPane<?> parentPane;

    private ItemStack itemStack = new ItemStack(Material.AIR);
    private GuiEvent<GuiItemClickEvent> onClick = e->{};

    private boolean isVisible = true;

    public AbstractGuiItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public AbstractGuiItem() {
        this(null);
    }

    public T self() {
        return self;
    }

    @Override
    public Pane getParentPane() {
        return parentPane;
    }

    protected void setParentPane(AbstractPane<?> parentPane) {
        this.parentPane = parentPane;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public T setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return self;
    }

    @Override
    public T refresh() {
        if (parentPane != null) {
            AbstractGuiPage<?> page = parentPane.getParentPage();
            if (page != null) {
                int slot = page.getSlot(this);
                if (slot != -1)
                    page.redrawItemInSlot(slot);
            }
        }
        return self;
    }

    @Override
    public T onClick(GuiEvent<GuiItemClickEvent> onClick) {
        this.onClick = onClick;
        return self;
    }

    @Override
    public GuiEvent<GuiItemClickEvent> getOnClick() {
        return onClick;
    }

    @Override
    public T clone() {
        try {
            T guiItem = (T) super.clone();
            guiItem.setParentPane(null);
            return guiItem;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T setVisible(boolean visible) {
        isVisible = visible;
        return self;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }
}
