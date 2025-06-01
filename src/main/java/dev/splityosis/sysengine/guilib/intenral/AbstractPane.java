package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.GuiItem;
import dev.splityosis.sysengine.guilib.GuiPage;
import dev.splityosis.sysengine.guilib.Pane;
import dev.splityosis.sysengine.guilib.PaneLayout;
import dev.splityosis.sysengine.guilib.events.PaneClickEvent;
import dev.splityosis.sysengine.guilib.events.PaneCloseEvent;
import dev.splityosis.sysengine.guilib.events.PaneOpenEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public abstract class AbstractPane implements Pane {

    private AbstractGuiPage parentGuiPage;
    private AbstractPaneLayout layout;
    private int weight = 0;
    private boolean visible = true;

    private Consumer<PaneClickEvent> onClick =e->{};
    private Consumer<PaneOpenEvent>  onOpen =e->{};
    private Consumer<PaneCloseEvent> onClose =e->{};

    public AbstractPane(PaneLayout layout) {
        if (! (layout instanceof AbstractPaneLayout))
            throw new IllegalArgumentException("layout must be instance of AbstractPaneLayout");
        this.layout = (AbstractPaneLayout) layout;
    }

    /**
     * Called when pane is attached to a GuiPage.
     * When this is called, {@link Pane#getParentPage()} isn't null and {@link Pane#getLayout()} gets initialized
     * @param page The page this got attached to
     */
    public abstract void onAttach(GuiPage page);

    /**
     * Sets the current Gui containing this Pane. Intended for internal use.
     */
    protected void setParentGuiPage(AbstractGuiPage parentGuiPage) {
        this.parentGuiPage = parentGuiPage;
    }

    @Override
    public Pane setWeight(int weight) {
        this.weight = weight;
        if (parentGuiPage != null)
            parentGuiPage.onPanesListChange(false);
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
        if (visible != this.visible) { // A change in state
            this.visible = visible;
            render();
        }

        return this;
    }

    @Override
    public Pane render() {
        if (parentGuiPage == null)
            throw new NullPointerException("Pane must be a part of a Page to be rendered");
        parentGuiPage.render(this);
        return this;
    }

    @Override
    public Pane render(int slot) {
        if (parentGuiPage == null)
            throw new NullPointerException("Pane must be a part of a Page to be rendered");

        getLayout().toRawSlot(slot).ifPresent(rawSlot -> {
            parentGuiPage.render(rawSlot);
        });
        return this;
    }

    @Override
    public AbstractPaneLayout getLayout() {
        return layout;
    }

    @Override
    public AbstractGuiPage getParentPage() {
        return parentGuiPage;
    }

    @Override
    public Pane handleClick(InventoryClickEvent event) {
        // TODO figure out if this method is even needed, since it needs to know if the item is at the top before calling it, so might as well let page do it
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
        AbstractGuiItem abstractGuiItem = (AbstractGuiItem) guiItem;
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
        AbstractGuiItem abstractGuiItem = (AbstractGuiItem) guiItem;
        if (!abstractGuiItem.getParentPane().equals(this))
            throw new IllegalArgumentException("item is not apart of this pane");
        abstractGuiItem.setParentPane(null);
    }




}
