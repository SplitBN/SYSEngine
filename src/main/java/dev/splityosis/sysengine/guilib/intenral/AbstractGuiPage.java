package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.components.*;
import dev.splityosis.sysengine.guilib.events.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractGuiPage<T extends AbstractGuiPage<?>> implements GuiPage, InventoryHolder {

    private T self = (T) this;

    private AbstractGui<?> parentGui;
    private Inventory inventory;
    private final List<PaneLayer> paneLayers = new ArrayList<>();

    private GuiEvent<GuiPageOpenEvent> onOpen = e->{};

    private GuiEvent<GuiPageCloseEvent> onClose = e->{};
    private GuiEvent<GuiPageClickEvent> onClick = e->{};
    private String title = getInventoryType().getDefaultTitle();

    public AbstractGuiPage() {

    }

    public T self() {
        return self;
    }

    /**
     * Sets the current Gui containing this Pane. Intended for internal use.
     */
    protected void setParentGui(AbstractGui<?> parentGui) {
        this.parentGui = parentGui;
    }

    protected void onPanesListChange(boolean clear) {
        paneLayers.sort(Comparator.comparing(paneLayer -> paneLayer.getPane().getWeight()));

        if (inventory != null) {
            if (clear)
                inventory.clear();

            for (PaneLayer paneLayer : paneLayers)
                refresh(paneLayer.getPane());
        }
    }

    protected abstract Inventory createInventory(String title, InventoryHolder holder);

    @Override
    public T addPane(Pane pane) {
        return addPane(pane, guiPage -> {});
    }

    @Override
    public <E extends Pane> T addPane(E pane, Consumer<E> setup) {
        if (! (pane instanceof AbstractPane))
            throw new IllegalArgumentException("pane is not an instance of AbstractPane");
        AbstractPane<?> abstractPane = (AbstractPane<?>) pane;
        if (abstractPane.getParentPage() != null)
            throw new IllegalArgumentException("pane already has parent page");

        abstractPane.setParentGuiPage(this);
        abstractPane.getLayout().initialize(getInventoryType(), getInventorySize());
        abstractPane.onAttach(this);
        paneLayers.add(new PaneLayer(pane));

        setup.accept(pane);

        if (inventory == null)
            inventory = createInventory(getTitle(), this);

        onPanesListChange(false);
        return self;
    }

    @Override
    public T removePane(Pane pane) {
        if (! (pane instanceof AbstractPane))
            throw new IllegalArgumentException("pane is not an instance of AbstractPane");
        AbstractPane<?> abstractPane = (AbstractPane<?>) pane;
        if (!abstractPane.getParentPage().equals(this))
            throw new IllegalArgumentException("pane is not apart of this page");
        abstractPane.setParentGuiPage(null);

        paneLayers.removeIf(paneLayer -> paneLayer.getPane().equals(pane));

        onPanesListChange(true);
        return self;
    }

    @Override
    public int getPaneCount() {
        return paneLayers.size();
    }

    @Override
    public T refresh() {
        // This completely refills layeredSlots and the entire inventory
        Map<Integer, GuiItem> finalLayer = new HashMap<>();

        // go through panes and place entries into layeredSlots as well as finalLayer
        for (PaneLayer paneLayer : paneLayers) {
            Pane pane = paneLayer.getPane();
            if (!pane.isVisible()) continue;

            paneLayer.updateAll(); // pull the latest data from the pane
            paneLayer.getRawToItemMap().entrySet().stream()
                    .filter(entry -> entry.getValue().isVisible())
                    .forEach(entry -> finalLayer.put(entry.getKey(), entry.getValue()));

        }

        // place final layer which is what should be showing
        List<HumanEntity> viewers = new ArrayList<>(inventory.getViewers());
        inventory = createInventory(getTitle(), this);
        for (int i = 0; i < inventory.getSize(); i++) {
            GuiItem gi = finalLayer.get(i);
            if (gi != null)
                inventory.setItem(i, gi.getItemStack());
        }

        viewers.forEach(humanEntity -> {
            humanEntity.openInventory(inventory);
        });

        return self;
    }

    @Override
    public T refresh(Pane pane) {
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

        return self;
    }

    @Override
    public T refresh(int slot) {
        for (PaneLayer paneLayer : paneLayers)
            paneLayer.updateRawSlot(slot);

        redrawItemInSlot(slot);
        return self;
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
            if (!paneLayer.getPane().isVisible())
                continue;
            GuiItem item = paneLayer.getItemAtRawSlot(slot);
            if (item != null && item.isVisible())
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

    protected T open(Player player) {
        player.openInventory(inventory);
        return self;
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
    public T onOpen(GuiEvent<GuiPageOpenEvent> onOpen) {
        this.onOpen = onOpen;
        return self;
    }

    @Override
    public T onClose(GuiEvent<GuiPageCloseEvent> onClose) {
        this.onClose = onClose;
        return self;
    }

    @Override
    public T onClick(GuiEvent<GuiPageClickEvent> onClick) {
        this.onClick = onClick;
        return self;
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
    public T setTitle(String title) {
        this.title = title;
        // Make new inventory and open it for the viewers
        if (inventory == null) return self;

        List<HumanEntity> viewers = new ArrayList<>(inventory.getViewers());
        inventory = createInventory(getTitle(), this);
        redrawAllItems();

        viewers.forEach(humanEntity -> {
            humanEntity.openInventory(inventory);
        });

        return self;
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
    public T handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInv = event.getClickedInventory();
        Inventory guiInv = this.inventory;
        int rawSlot = event.getSlot();

        // 1) Click outside any inventory
        if (clickedInv == null) {
//            System.out.println("[DEBUG] Click was outside of inventory");
            // TODO perhaps an onOutsideClick
            return self;
        }

        boolean isItemPlace = GuiInteraction.ITEM_PLACE.matches(event);

        // 2) Click in player's own inventory
        if (!clickedInv.equals(guiInv)) {
            // TODO perhaps onPlayerInventoryClick or sum like that

            if (!isItemPlace) {
                // player doing stuff in their inventory we don't care
                return self;
            }

            // So the player is trying to shift click an item in
            int firstEmpty = guiInv.firstEmpty();

            if (firstEmpty == -1) // no space in inv anyways doesnt matter
                return self;

            // Find the pane that owns the slot that the item is being put into and check if it allows it
            Pane topPane = getTopPaneAt(firstEmpty);
            if (topPane == null || !topPane.isInteractionAllowed(GuiInteraction.ITEM_PLACE)) {
//                System.out.println("[DEBUG] Pane doesn't allow item place, canceling");
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }

            return self;
        }

        // 3) Click inside GUI
        GuiItem clickedItem = getItem(rawSlot);
        boolean hasClickedItem = (clickedItem != null);
//        System.out.println("[DEBUG] Gui was clicked, the item is " + (hasClickedItem ? clickedItem.getItemStack() : "null"));

        // 3a) Page‐level event
        GuiPageClickEvent pageEvt = new GuiPageClickEvent(this, player, event, clickedItem, rawSlot);
        getOnClick().call(pageEvt);
        if (pageEvt.isCancelled()) {
//            System.out.println("[DEBUG] GuiPageClickEvent was cancelled");
            return self;
        }

        // 3b) Find interaction owner pane
        Pane interactionOwner = null;
        if (hasClickedItem) {
            interactionOwner = clickedItem.getParentPane();
//            System.out.println("[DEBUG] Interaction owner = clickedItem.parentPane");
        } else {
            interactionOwner = getTopPaneAt(rawSlot);
//            System.out.println("[DEBUG] Interaction owner = topPaneAt(" + rawSlot + ") → " + interactionOwner);
        }

        if (interactionOwner == null) {
//            System.out.println("[DEBUG] No pane covers this slot. cancelling");
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return self; // Return cuz no item and no pane was clicked
        }

        // 3c) Check pane's allowedInteractions
        GuiInteraction matchedInteraction = null;
        // TODO perhaps handle each specific interaction and make an onItemPlace onItemDrop etc...
        for (GuiInteraction gi : interactionOwner.getAllowedInteractions())
            if (gi.matches(event)) {
                matchedInteraction = gi;
                break;
            }

        if (matchedInteraction == null) {
//            System.out.println("[DEBUG] Pane " + interactionOwner + " does not allow this interaction → cancelling");
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
//        else
//            System.out.println("[DEBUG] Pane " + interactionOwner + " allows " + matchedInteraction);

        // 3d) Fire PaneClickEvent for every pane covering rawSlot (top→bottom)
        for (int i = paneLayers.size() - 1; i >= 0; i--) {
            PaneLayer layer = paneLayers.get(i);
            Pane pane = layer.getPane();
            System.out.println("clickedpane.isvisible = "+ pane.isVisible());
            if (!pane.isVisible()) continue;

            OptionalInt maybeLocal = pane.getLayout().toLocalSlot(rawSlot);
            if (!maybeLocal.isPresent()) continue;
            int local = maybeLocal.getAsInt();

//            System.out.println("[DEBUG] calling pane click event since pane covers this slot, local: " + local);

            PaneClickEvent paneEvt = new PaneClickEvent(pane, player, event, local, rawSlot);
            pane.getOnClick().call(paneEvt);
        }

        System.out.println("[DEBUG] No pane event was cancelled, allowed to handle item");
        // 3e) If there was a clickedItem, fire pre‐click then item‐click
        if (hasClickedItem) {
            Pane parentPane = clickedItem.getParentPane();
            int local = parentPane.getLayout().toLocalSlot(rawSlot).orElse(-1);
            System.out.println("[DEBUG] Preparing to fire GuiItemPreClickEvent for local=" + local);

            GuiItemPreClickEvent preEvt = new GuiItemPreClickEvent(
                    parentPane, player, event, local, rawSlot, clickedItem
            );
            parentPane.getOnItemPreClick().call(preEvt);
            if (preEvt.isCancelled()) {
                System.out.println("[DEBUG] GuiItemPreClickEvent was cancelled. stopping");
                return self;
            }
            System.out.println("[DEBUG] GuiItemPreClickEvent allowed firing item click event");

            GuiItemClickEvent itemEvt = new GuiItemClickEvent(player, event, local, rawSlot, clickedItem);
            clickedItem.getOnClick().call(itemEvt);
            System.out.println("[Debug] GuiItemClickEvent fired for " + clickedItem);
        }
        else {
            System.out.println("[DEBUG] no item was clicked");
        }

        return self;
    }

    @Override
    public T handleDrag(InventoryDragEvent event) {
        if (!GuiInteraction.ITEM_DRAG.matches(event)) {
//            System.out.println("[DEBUG] handleDrag: not ITEM_DRAG → ignoring");
            return self;
        }

        int guiSize = event.getView().getTopInventory().getSize();
        Set<Integer> guiSlots = event.getRawSlots().stream()
                .filter(slot -> slot < guiSize)
                .collect(Collectors.toSet());
//        System.out.println("[DEBUG] Drag affected GUI slots: " + guiSlots);

        if (guiSlots.isEmpty()) {
//            System.out.println("[DEBUG] No GUI slots in drag → ALLOWING");
            return self;
        }


        for (int raw : guiSlots) {
            Pane topPane = getTopPaneAt(raw);
//            System.out.println("[DEBUG] Checking drag at raw=" + raw + ", topPane=" + topPane);
            // Cancel if drag affected a slot with no pane or a pane that doesn't allow dragging
            if (topPane == null || !topPane.isInteractionAllowed(GuiInteraction.ITEM_DRAG)) {
//                System.out.println("[DEBUG] Pane " + topPane + " disallows ITEM_DRAG → cancelling");
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return self;
            }
        }

//        System.out.println("[DEBUG] All panes allow ITEM_DRAG; letting drag proceed");

        return self;
    }

    public PaneLayer getPaneLayer(Pane pane) {
        return paneLayers.stream().filter(layer -> layer.getPane().equals(pane)).findFirst().orElse(null);
    }

    public Pane getTopPaneAt(int rawSlot) {
        for (int i = paneLayers.size()-1; i >= 0; i--) {
            PaneLayer paneLayer = paneLayers.get(i);
            Pane pane = paneLayer.getPane();

            if (!pane.isVisible() || !pane.getLayout().containsRawSlot(rawSlot))
                continue;

            return pane;
        }

        return null;
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
