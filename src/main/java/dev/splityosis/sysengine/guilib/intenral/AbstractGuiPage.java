package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.*;
import dev.splityosis.sysengine.guilib.events.GuiPageCloseEvent;
import dev.splityosis.sysengine.guilib.events.GuiPageOpenEvent;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractGuiPage implements GuiPage, InventoryHolder {

    private Gui parentGui;
    private Inventory inventory;

    private List<PaneLayer> paneLayers = new ArrayList<>();

    private Consumer<GuiPageOpenEvent> onOpen = e->{};
    private Consumer<GuiPageCloseEvent> onClose = e->{};

    private String title = getInventoryType().getDefaultTitle();

    public AbstractGuiPage() {

    }

    /**
     * Sets the current Gui containing this Pane. Intended for internal use.
     */
    protected void setParentGui(Gui parentGui) {
        this.parentGui = parentGui;
    }

    protected void onPanesListChange(boolean clear) {
        paneLayers.sort(Comparator.comparing(paneLayer -> paneLayer.getPane().getWeight()));

        if (clear)
            inventory.clear();

        for (PaneLayer paneLayer : paneLayers)
            render(paneLayer.getPane());
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
        abstractPane.getLayout().initialize(getInventoryType(), getInventorySize());
        abstractPane.onAttach(this);

        if (inventory == null)
            inventory = createInventory(getTitle(), this);

        paneLayers.add(new PaneLayer(pane));
        onPanesListChange(false);
        return this;
    }

    @Override
    public GuiPage removePane(Pane pane) {
        if (! (pane instanceof AbstractPane))
            throw new IllegalArgumentException("pane is not an instance of AbstractPane");
        AbstractPane abstractPane = (AbstractPane) pane;
        if (!abstractPane.getParentPage().equals(this))
            throw new IllegalArgumentException("pane is not apart of this page");
        abstractPane.setParentGuiPage(null);

        paneLayers.removeIf(paneLayer -> paneLayer.getPane().equals(pane));

        onPanesListChange(true);
        return this;
    }

    @Override
    public int getPanesAmount() {
        return paneLayers.size();
    }

    @Override
    public GuiPage render() {
        // This completely refills layeredSlots and the entire inventory
        Map<Integer, GuiItem> finalLayer = new HashMap<>();

        // go through panes and place entries into layeredSlots as well as finalLayer
        for (PaneLayer paneLayer : paneLayers) {
            Pane pane = paneLayer.getPane();
            if (!pane.isVisible()) continue;

            paneLayer.updateAll(); // pull the latest data from the pane
            finalLayer.putAll(paneLayer.getRawToItemMap());
        }

        // place final layer which is what should be showing
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
    public GuiPage render(Pane pane) {
        PaneLayer paneLayer = null;
        for (PaneLayer layer : paneLayers)
            if (layer.getPane().equals(pane)) {
                paneLayer = layer;
                break;
            }

        if (paneLayer == null)
            throw new IllegalArgumentException("pane is not apart of this GuiPage");

        paneLayer.updateAll();
        for (int localSlot = 0; localSlot < pane.getLayout().getSlotCapacity(); localSlot++)
            pane.getLayout().toRawSlot(localSlot).ifPresent(this::redrawItemInSlot);

        return this;
    }

    @Override
    public GuiPage render(int slot) {
        for (PaneLayer paneLayer : paneLayers)
            paneLayer.updateRawSlot(slot);

        redrawItemInSlot(slot);
        return null;
    }

    @Override
    public int getSlot(GuiItem guiItem) {
        for (Map.Entry<Integer, GuiItem> s : getCurrentItems().entrySet())
            if (s.getValue().equals(guiItem))
                return s.getKey();
        return -1;
    }

    @Override
    public GuiItem getItem(int slot) {
        GuiItem top = null;
        for (PaneLayer paneLayer : paneLayers) {
            if (!paneLayer.getPane().isVisible()) return null;
            GuiItem item = paneLayer.getItemAtRawSlot(slot);
            if (item != null)
                top = item;
        }

        return top;
    }

    @Override
    public Map<Integer, GuiItem> getCurrentItems() {
        Map<Integer, GuiItem> currentItems = new HashMap<>();
        for (PaneLayer paneLayer : paneLayers)
            currentItems.putAll(paneLayer.getRawToItemMap());
        return currentItems;
    }

    @Override
    public List<Player> getViewers() {
        return inventory.getViewers().stream().map(humanEntity -> (Player) humanEntity).collect(Collectors.toList());
    }

    @Override
    public GuiPage open(Player player) {
        GuiPageOpenEvent event = new GuiPageOpenEvent(this, player);
        onOpen.accept(event);

        if (!event.isCancelled())
            player.openInventory(inventory);

        return this;
    }

    /**
     * Redraws an item in a specific slot without updating layers.
     * Would be called be GuiItem.setItemStack, render(pane)
     */
    protected void redrawItemInSlot(int slot) {
        GuiItem guiItem = getItem(slot);
        if (guiItem == null)
            inventory.setItem(slot, null);
        else
            inventory.setItem(slot, guiItem.getItemStack());
    }

    /**
     * Updates all items, essentially replaces them in the inventory
     */
    protected void redrawAllItems() {
        getCurrentItems().forEach((raw, item) -> {
            if (item == null)
                inventory.setItem(raw, null);
            else
                inventory.setItem(raw, item.getItemStack());
        });
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
        // Make new inventory and open it for the viewers
        if (inventory == null) return this;

        List<HumanEntity> viewers = inventory.getViewers();
        inventory = createInventory(getTitle(), this);
        redrawAllItems();

        viewers.forEach(humanEntity -> {
            humanEntity.openInventory(inventory);
        });

        return this;
    }

    @Override
    public List<Pane> getPanes() {
        return Collections.unmodifiableList(paneLayers.stream().map(PaneLayer::getPane).collect(Collectors.toList()));
    }

    @Override
    public List<PaneLayer> getPaneLayers() {
        return paneLayers;
    }

    @Override
    public GuiPage handleClick(InventoryClickEvent event) {

        // TODO for which slot is clicked get panes of that slot and call handleClick

        return this;
    }

    @Override
    public Consumer<GuiPageCloseEvent> getOnClose() {
        return onClose;
    }

    @Override
    public Consumer<GuiPageOpenEvent> getOnOpen() {
        return onOpen;
    }
}
