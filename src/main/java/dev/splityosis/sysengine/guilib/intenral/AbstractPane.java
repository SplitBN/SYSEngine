package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.GuiItem;
import dev.splityosis.sysengine.guilib.Pane;
import dev.splityosis.sysengine.guilib.PaneLayout;
import dev.splityosis.sysengine.guilib.events.PaneClickEvent;
import dev.splityosis.sysengine.guilib.events.PaneCloseEvent;
import dev.splityosis.sysengine.guilib.events.PaneOpenEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractPane implements Pane {

    private AbstractGuiPage parentGuiPage;
    private PaneLayout layout;
    private int weight = 0;
    private boolean visible = true;

    private Consumer<PaneClickEvent> onClick =e->{};
    private Consumer<PaneOpenEvent>  onOpen =e->{};
    private Consumer<PaneCloseEvent> onClose =e->{};

    public AbstractPane(PaneLayout layout) {
        this.layout = layout;
    }

    /**
     * Sets the current Gui containing this Pane. Intended for internal use.
     */
    protected void setParentGuiPage(AbstractGuiPage parentGuiPage) {
        this.parentGuiPage = parentGuiPage;
    }

    @Override
    public Pane setWeight(int weight) {
        this.weight = weight;
        parentGuiPage.onPanesListChange();
        parentGuiPage.render();
        return this;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public Pane setVisible(boolean visible) {
        boolean old = this.visible;
        this.visible = visible;

        if (old != visible) // A change in state
            getParentPage().render();

        return this;
    }

    @Override
    public Pane render() {
        // get local items and place in the inventory IF the owner if the current item

        int paneI = getParentPage().getPanes().indexOf(this);
        if (paneI == -1) return this;

        Map<Integer, List<GuiItem>> layeredSlots = getParentPage().getLayeredSlots();

        getLocalItems().forEach((localSlot, guiItem) -> {
            getLayout().toRawSlot(parentGuiPage.getInventoryType(), parentGuiPage.getInventorySize(), localSlot)
                    .ifPresent(rawSlot -> {
                        List<GuiItem> stack = layeredSlots.computeIfAbsent(rawSlot, k -> new ArrayList<>(Collections.nCopies(parentGuiPage.getPanesAmount(), null)));
                        stack.set(paneI, guiItem);
                        parentGuiPage.updateItemInSlot(rawSlot);
                    });
        });

        return this;
    }

    @Override
    public PaneLayout getLayout() {
        return layout;
    }

    @Override
    public AbstractGuiPage getParentPage() {
        return parentGuiPage;
    }

    @Override
    public Pane handleClick(InventoryClickEvent event) {
        return null;
    }

    @Override
    public Pane setOnClick(Consumer<PaneClickEvent> onClick) {
        this.onClick = onClick;
        return this;
    }

    @Override
    public Pane setOnOpen(Consumer<PaneOpenEvent> onOpen) {
        this.onOpen = onOpen;
        return this;
    }

    @Override
    public Pane setOnClose(Consumer<PaneCloseEvent> onClose) {
        this.onClose = onClose;
        return this;
    }


}
