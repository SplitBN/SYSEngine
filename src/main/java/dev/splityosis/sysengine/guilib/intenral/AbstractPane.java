package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.components.*;
import dev.splityosis.sysengine.guilib.events.*;
import dev.splityosis.sysengine.guilib.exceptions.UnsupportedPaneOperationException;
import dev.splityosis.sysengine.guilib.layout.FullLayout;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public abstract class AbstractPane<T extends AbstractPane<?>> implements Pane {

    private T self = (T) this;

    private AbstractGuiPage<?> parentGuiPage;
    private final AbstractPaneLayout<?> layout;
    private int weight = 0;
    private boolean visible = true;
    private final Set<GuiInteraction> allowedInteractions = GuiInteraction.ALLOWED_BY_DEFAULT.isEmpty() ?
            EnumSet.noneOf(GuiInteraction.class) :
            EnumSet.copyOf(GuiInteraction.ALLOWED_BY_DEFAULT);

    private GuiEvent<PaneClickEvent> onClick =e->{};
    private GuiEvent<PaneOpenEvent>  onOpen =e->{};
    private GuiEvent<PaneCloseEvent> onClose = e->{};
    private GuiEvent<GuiItemPreClickEvent> onItemPreClick = e->{};

    public AbstractPane(PaneLayout layout) {
        if (! (layout instanceof AbstractPaneLayout))
            throw new IllegalArgumentException("layout must be instance of AbstractPaneLayout");
        this.layout = (AbstractPaneLayout<?>) layout;
    }

    public AbstractPane() {
        this(new FullLayout());
    }

    public T self() {
        return self;
    }

    /**
     * Called when pane is attached to a GuiPage.
     * When this is called, {@link Pane#getParentPage()} isn't null and {@link Pane#getLayout()} gets initialized
     * @param page The page this got attached to
     */
    protected abstract void onAttach(GuiPage page);

    /**
     * Sets the given {@link GuiItem} in the specified local slot.
     * Called internally after sync has been verified.
     *
     * @param localSlot the local slot index in the pane
     * @param item the item to set, or {@code null} to clear the slot
     * @throws UnsupportedPaneOperationException if this pane doesn't support item setting
     */
    protected abstract void onDirectItemSet(int localSlot, GuiItem item) throws UnsupportedPaneOperationException;

    /**
     * Sets the given {@link GuiItem} in the specified local slot.
     * Verifies that the slot is synced with the rendered state before applying.
     *
     * @param localSlot the local slot index in the pane
     * @param item the item to set, or {@code null} to clear the slot
     * @throws UnsupportedPaneOperationException if this pane doesn't support item setting
     * @throws IllegalStateException if the slot is out of sync with the rendered state
     */
    public void setItemDirectly(int localSlot, @Nullable GuiItem item) throws UnsupportedPaneOperationException {
        if (parentGuiPage == null || isSynced(localSlot, item))
            onDirectItemSet(localSlot, item);
        else
            throw new IllegalStateException("Pane is internally out of sync with rendered form, interactions require the pane/slot being refreshed! local slot: " + localSlot);
    }

    private boolean isSynced(int localSlot, GuiItem item) {
        if (parentGuiPage == null)
            return true;

        PaneLayer paneLayer = parentGuiPage.getPaneLayer(this);
        if (paneLayer == null)
            return true;

        GuiItem rendered = paneLayer.getItemAtLocalSlot(localSlot);
        if (rendered == null)
            return item == null;
        else
            return rendered.equals(item);
    }

    /**
     * Sets the current Gui containing this Pane. Intended for internal use.
     */
    protected void setParentGuiPage(AbstractGuiPage<?> parentGuiPage) {
        this.parentGuiPage = parentGuiPage;
    }

    @Override
    public T setWeight(int weight) {
        this.weight = weight;
        if (parentGuiPage != null)
            parentGuiPage.onPanesListChange(false);
        return self;
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
    public boolean isAttached() {
        return getParentPage() != null;
    }

    @Override
    public T setVisible(boolean visible) {
        if (visible != this.visible) { // A change in state
            this.visible = visible;
            refresh();
        }

        return self;
    }

    @Override
    public T refresh() {
        if (parentGuiPage == null)
            throw new NullPointerException("Pane must be a part of a Page to be rendered");
        parentGuiPage.refresh(this);
        return self;
    }

    @Override
    public T refresh(int slot) {
        if (parentGuiPage == null)
            throw new NullPointerException("Pane must be a part of a Page to be rendered");

        getLayout().toRawSlot(slot).ifPresent(rawSlot -> {
            parentGuiPage.refresh(rawSlot);
        });
        return self;
    }

//    @Override
//    public T allowInteraction(GuiInteraction interaction) {
//        allowedInteractions.add(interaction);
//        return self;
//    }
//
//    @Override
//    public T disallowInteraction(GuiInteraction interaction) {
//        allowedInteractions.remove(interaction);
//        return self;
//    }

    @Override
    public boolean isInteractionAllowed(GuiInteraction interaction) {
        return allowedInteractions.contains(interaction);
    }

    @Override
    public Set<GuiInteraction> getAllowedInteractions() {
        return Collections.unmodifiableSet(allowedInteractions);
    }

//    @Override
//    public T setInteractionAllowed(GuiInteraction interaction, boolean allowed) {
//        if (allowed)
//            allowInteraction(interaction);
//        else
//            disallowInteraction(interaction);
//        return self;
//    }

    @Override
    public AbstractPaneLayout<?> getLayout() {
        return layout;
    }

    @Override
    public AbstractGuiPage<?> getParentPage() {
        return parentGuiPage;
    }

    @Override
    public T onClick(GuiEvent<PaneClickEvent> onClick) {
        this.onClick = onClick;
        return self;
    }

    @Override
    public T onOpen(GuiEvent<PaneOpenEvent> onOpen) {
        this.onOpen = onOpen;
        return self;
    }

    @Override
    public T onClose(GuiEvent<PaneCloseEvent> onClose) {
        this.onClose = onClose;
        return self;
    }

    @Override
    public T onItemPreClick(GuiEvent<GuiItemPreClickEvent> onItemClick) {
        this.onItemPreClick = onItemClick;
        return self;
    }

    @Override
    public GuiEvent<PaneClickEvent> getOnClick() {
        return onClick;
    }

    @Override
    public GuiEvent<PaneCloseEvent> getOnClose() {
        return onClose;
    }

    @Override
    public GuiEvent<PaneOpenEvent> getOnOpen() {
        return onOpen;
    }

    @Override
    public GuiEvent<GuiItemPreClickEvent> getOnItemPreClick() {
        return onItemPreClick;
    }

    //    protected void validateLocalSlot(int slot) {
//        if (slot < 0 || slot >= getLayout().getSlotCapacity())
//            throw new IllegalArgumentException("Invalid slot " + slot + " out of bounds for layout capacity "+getLayout().getSlotCapacity());
//    }

    /**
     * Must be called before associating an item with a pane.
     * This internally sets this pane as the item's parent
     */
    protected void registerItem(GuiItem guiItem) {
        if (! (guiItem instanceof AbstractGuiItem))
            throw new IllegalArgumentException("item is not an instance of AbstractGuiItem");
        AbstractGuiItem<?> abstractGuiItem = (AbstractGuiItem<?>) guiItem;
        if (abstractGuiItem.getParentPane() != null)
            throw new IllegalArgumentException("item already has parent pane");
        abstractGuiItem.setParentPane(this);
    }

    /**
     * Must be called before dis-associating an item with a pane.
     * This internally removes this pane as the item's parent
     */
    protected void unregisterItem(GuiItem guiItem) {
        if (! (guiItem instanceof AbstractGuiItem))
            throw new IllegalArgumentException("item is not an instance of AbstractGuiItem");
        AbstractGuiItem<?> abstractGuiItem = (AbstractGuiItem<?>) guiItem;
        if (!abstractGuiItem.getParentPane().equals(this))
            throw new IllegalArgumentException("item is not apart of this pane");
        abstractGuiItem.setParentPane(null);
    }

}
