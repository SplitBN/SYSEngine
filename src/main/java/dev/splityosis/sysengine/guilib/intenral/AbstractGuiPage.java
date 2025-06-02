package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.components.*;
import dev.splityosis.sysengine.guilib.events.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractGuiPage implements GuiPage, InventoryHolder {

    private AbstractGui parentGui;
    private Inventory inventory;

    private List<PaneLayer> paneLayers = new ArrayList<>();

    private GuiEvent<GuiPageOpenEvent> onOpen = e->{};
    private GuiEvent<GuiPageCloseEvent> onClose = e->{};
    private GuiEvent<GuiPageClickEvent> onClick = e->{};

    private String title = getInventoryType().getDefaultTitle();

    public AbstractGuiPage() {

    }

    /**
     * Sets the current Gui containing this Pane. Intended for internal use.
     */
    protected void setParentGui(Gui parentGui) {
        if (! (parentGui instanceof AbstractGui))
            throw new IllegalStateException("Parent Gui has to be an instance of AbstractGui");
        this.parentGui = (AbstractGui) parentGui;
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
        for (int localSlot = 0; localSlot < pane.getLayout().getCapacity(); localSlot++)
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


    protected GuiPage open(Player player) {
        GuiPageOpenEvent event = new GuiPageOpenEvent(this, player);
        onOpen.call(event);

        if (!event.isCancelled()) {
            for (int i = getPanes().size() - 1; i >= 0; i--) {
                Pane pane = getPanes().get(i);
                PaneOpenEvent paneCloseEvent = new PaneOpenEvent(pane, player);
                pane.getOnOpen().call(paneCloseEvent);
            }
            player.openInventory(inventory);
        }

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
    public GuiPage onOpen(GuiEvent<GuiPageOpenEvent> onOpen) {
        this.onOpen = onOpen;
        return this;
    }

    @Override
    public GuiPage onClose(GuiEvent<GuiPageCloseEvent> onClose) {
        this.onClose = onClose;
        return this;
    }

    @Override
    public GuiPage onClick(GuiEvent<GuiPageClickEvent> onClick) {
        this.onClick = onClick;
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
        GuiItem guiItem = getItem(event.getSlot());

        Player player = (Player) event.getWhoClicked();
        int rawSlot = event.getSlot();

        GuiPageClickEvent pageClickEvent = new GuiPageClickEvent(this, player, event, guiItem, rawSlot);
        onClick.call(pageClickEvent);

        if (pageClickEvent.isCancelled())
            return this;

        for (int i = paneLayers.size() - 1; i >= 0; i--) {
            PaneLayer paneLayer = paneLayers.get(i);
            int localSlot = paneLayer.getPane().getLayout().toLocalSlot(rawSlot).orElse(-1);
            System.out.println("Raw slot " + rawSlot + " local slot " + localSlot);

            if (!paneLayer.getPane().isVisible() || localSlot == -1)
                continue;

            System.out.println("calling pane click event");

            PaneClickEvent paneClickEvent = new PaneClickEvent(paneLayer.getPane(), player, event, localSlot, rawSlot);
            paneLayer.getPane().getOnClick().call(paneClickEvent);
        }

        if (guiItem != null) {
            int localSlot = guiItem.getParentPane().getLayout().toLocalSlot(rawSlot).orElse(-1);
            GuiItemPreClickEvent guiItemPreClickEvent = new GuiItemPreClickEvent(guiItem.getParentPane(), player, event, localSlot, rawSlot, guiItem);

            guiItem.getParentPane().getOnItemPreClick().call(guiItemPreClickEvent);

            if (guiItemPreClickEvent.isCancelled())
                return this;

            GuiItemClickEvent guiItemClickEvent = new GuiItemClickEvent(player, event, localSlot, rawSlot, guiItem);
            guiItem.getOnClick().call(guiItemClickEvent);
        }

        return this;
    }

    @Override
    public GuiEvent<GuiPageCloseEvent> getOnClose() {
        return onClose;
    }

    @Override
    public GuiEvent<GuiPageOpenEvent> getOnOpen() {
        return onOpen;
    }

    @Override
    public GuiEvent<GuiPageClickEvent> getOnClick() {
        return onClick;
    }
}
