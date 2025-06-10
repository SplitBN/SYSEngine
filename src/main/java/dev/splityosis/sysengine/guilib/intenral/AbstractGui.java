package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.components.Gui;
import dev.splityosis.sysengine.guilib.components.GuiPage;
import dev.splityosis.sysengine.guilib.events.GuiCloseEvent;
import dev.splityosis.sysengine.guilib.events.GuiEvent;
import dev.splityosis.sysengine.guilib.events.GuiOpenEvent;
import dev.splityosis.sysengine.guilib.page.DefaultPage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractGui<T extends AbstractGui<?>> implements Gui {

    private T self = (T) this;

    private int currentPageIndex;
    private final List<AbstractGuiPage<?>> pages = new ArrayList<>();
    private AbstractGuiPage<?> currentPage;

    private GuiEvent<GuiOpenEvent> onOpen = e->{};
    private GuiEvent<GuiCloseEvent> onClose = e->{};

    public T self() {
        return self;
    }

    @Override
    public int getActivePageIndex() {
        return currentPageIndex;
    }

    @Override
    public int getPageCount() {
        return pages.size();
    }

    @Override
    public GuiPage getPage(int index) {
        if (index < 0 || index >= pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());
        return pages.get(index);
    }

    @Override
    public GuiPage getActivePage() {
        return currentPage;
    }

    @Override
    public T setActivePage(int index) {
        if (index < 0 || index >= pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());

        GuiPage oldPage = currentPage;

        currentPageIndex = index;
        currentPage = pages.get(index);

        if (oldPage != null)
            for (Player viewer : oldPage.getViewers())
                open(viewer);

        return self;
    }

    @Override
    public T addPage(GuiPage page) {
        return addPage(page, guiPage -> {});
    }

    @Override
    public Gui addPage(int rows, Consumer<GuiPage> setup) {
        return addPage(new DefaultPage(rows*9), setup);
    }

    @Override
    public Gui addPage(InventoryType inventoryType, Consumer<GuiPage> setup) {
        return addPage(new DefaultPage(inventoryType), setup);
    }

    @Override
    public Gui addPage(String title, int rows, Consumer<GuiPage> setup) {
        return addPage(new DefaultPage(rows*9), defaultPage -> {
            defaultPage.setTitle(title);
            setup.accept(defaultPage);
        });
    }

    @Override
    public Gui addPage(String title, InventoryType type, Consumer<GuiPage> setup) {
        return addPage(new DefaultPage(type), defaultPage -> {
            defaultPage.setTitle(title);
            setup.accept(defaultPage);
        });
    }

    @Override
    public <E extends GuiPage> T addPage(E page, Consumer<E> setup) {
        if (! (page instanceof AbstractGuiPage))
            throw new IllegalArgumentException("page must be an instance of AbstractGuiPage");

        AbstractGuiPage<?> abstractGuiPage = (AbstractGuiPage<?>) page;
        if (abstractGuiPage.getParentGui() != null)
            throw new IllegalArgumentException("page already has parent gui");

        abstractGuiPage.setParentGui(this);
        pages.add(abstractGuiPage);
        setup.accept(page);

        if (currentPage == null)
            setActivePage(0);

        return self;
    }

    @Override
    public List<GuiPage> getPages() {
        return Collections.unmodifiableList(pages);
    }

    @Override
    public T open(Player player) {
        currentPage.open(player);
        return self;
    }

    @Override
    public Collection<Player> getViewers() {
        return pages.stream()
                .map(GuiPage::getViewers)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public T onOpen(GuiEvent<GuiOpenEvent> onOpen) {
        this.onOpen = onOpen;
        return self;
    }

    @Override
    public T onClose(GuiEvent<GuiCloseEvent> onClose) {
        this.onClose = onClose;
        return self;
    }

    @Override
    public GuiEvent<GuiCloseEvent> getOnClose() {
        return onClose;
    }

    @Override
    public GuiEvent<GuiOpenEvent> getOnOpen() {
        return onOpen;
    }
}
