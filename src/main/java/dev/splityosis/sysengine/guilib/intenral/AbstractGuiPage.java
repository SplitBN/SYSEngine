package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.Gui;
import dev.splityosis.sysengine.guilib.GuiItem;
import dev.splityosis.sysengine.guilib.GuiPage;
import dev.splityosis.sysengine.guilib.Pane;
import dev.splityosis.sysengine.guilib.events.GuiPageCloseEvent;
import dev.splityosis.sysengine.guilib.events.GuiPageOpenEvent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractGuiPage implements GuiPage, InventoryHolder {

    private Gui parentGui;
    private Inventory inventory;
    private List<Pane> panes = new ArrayList<>();

    private Map<Integer, List<GuiItem>> layeredSlots = new HashMap<>();

    private Consumer<GuiPageOpenEvent> onOpen = e->{};
    private Consumer<GuiPageCloseEvent> onClose = e->{};

    private String title;

    public AbstractGuiPage() {
        for (int i = 0; i < getInventorySize(); i++)
            layeredSlots.put(i, new ArrayList<>(Collections.nCopies(getPanesAmount(), null)));
    }

    /**
     * Sets the current Gui containing this Pane. Intended for internal use.
     */
    protected void setParentGui(Gui parentGui) {
        this.parentGui = parentGui;
    }

    protected void onPanesListChange() {
        panes.sort(Comparator.comparing(Pane::getWeight));
        render();
    }

    protected abstract Inventory createInventory(String title, InventoryHolder holder);

    @Override
    public GuiPage addPane(Pane pane) {
        if (! (pane instanceof AbstractPane))
            throw new IllegalArgumentException("pane is not an instance of AbstractPane");
        AbstractPane abstractPane = (AbstractPane) pane;
        if (abstractPane.getParentPage() != null)
            throw new IllegalArgumentException("pane already has parent page");
        abstractPane.setParentGuiPage(this);

        panes.add(pane);
        onPanesListChange();
        return this;
    }

    @Override
    public GuiPage removePane(Pane pane) {
        if (! (pane instanceof AbstractPane))
            throw new IllegalArgumentException("pane is not an instance of AbstractPane");
        AbstractPane abstractPane = (AbstractPane) pane;
        abstractPane.setParentGuiPage(null);

        panes.remove(pane);
        onPanesListChange();
        return this;
    }

    @Override
    public int getPanesAmount() {
        return panes.size();
    }

    @Override
    public GuiPage render() {
        // This completely refills layeredSlots and the entire inventory

        layeredSlots.clear();
        for (int i = 0; i < getInventorySize(); i++)
            layeredSlots.put(i, new ArrayList<>(Collections.nCopies(getPanesAmount(), null)));

        Map<Integer, GuiItem> finalLayer = new HashMap<>();

        // go through panes and place entries into layeredSlots as well as finalLayer
        for (int paneI = 0; paneI < panes.size(); paneI++) {
            Pane pane = panes.get(paneI);
            if (!pane.isVisible()) continue;

            int finalPaneI = paneI;
            pane.getLocalItems().forEach((local, guiItem) -> {
                pane.getLayout()
                        .toRawSlot(getInventoryType(), getInventorySize(), local)
                        .ifPresent(raw -> {
                            layeredSlots.get(raw).set(finalPaneI, guiItem);
                            if (guiItem != null)
                                finalLayer.put(raw, guiItem);
                        });
            });
        }

        // place final layer
        List<HumanEntity> viewers = inventory.getViewers();
        inventory = createInventory(getTitle(), this);
        for (int i = 0; i < inventory.getSize(); i++) {
            GuiItem gi = finalLayer.get(i);
            if (gi != null)
                inventory.setItem(i, gi.getItemStack());
        }

        viewers.forEach(humanEntity -> {
            humanEntity.openInventory(inventory);
        });

        return this;
    }

    @Override
    public GuiItem getItem(int slot) {
        List<GuiItem> stack = layeredSlots.get(slot);
        if (stack == null) return null;

        GuiItem top = null;
        for (GuiItem guiItem : stack)
            if (guiItem != null)
                top = guiItem;

        return top;
    }

    protected void updateItemInSlot(int slot) {
        GuiItem guiItem = getItem(slot);
        if (guiItem == null)
            inventory.setItem(slot, null);
        else
            inventory.setItem(slot, guiItem.getItemStack());
    }

    protected Map<Integer, List<GuiItem>> getLayeredSlots() {
        return layeredSlots;
    }

    @Override
    public GuiPage setOnOpen(Consumer<GuiPageOpenEvent> onOpen) {
        this.onOpen = onOpen;
        return this;
    }

    @Override
    public GuiPage setOnClose(Consumer<GuiPageCloseEvent> onClose) {
        this.onClose = onClose;
        return this;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public Gui getParentGui() {
        return parentGui;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public GuiPage setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public List<Pane> getPanes() {
        return panes;
    }

    @Override
    public GuiPage handleClick(InventoryClickEvent event) {

        // TODO for which slot is clicked get panes of that slot and call handleClick

        return this;
    }

}
