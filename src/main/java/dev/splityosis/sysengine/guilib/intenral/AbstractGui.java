package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.Gui;
import dev.splityosis.sysengine.guilib.GuiPage;
import dev.splityosis.sysengine.guilib.events.GuiCloseEvent;
import dev.splityosis.sysengine.guilib.events.GuiOpenEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractGui implements Gui {

    private int currentPageIndex;
    private List<GuiPage> pages = new ArrayList<>();
    private GuiPage currentPage;

    private Consumer<GuiOpenEvent> onOpen = e->{};
    private Consumer<GuiCloseEvent> onClose = e->{};

    @Override
    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    @Override
    public int getPagesAmount() {
        return pages.size();
    }

    @Override
    public GuiPage getPage(int index) {
        if (index < 0 || index >= pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());
        return pages.get(index);
    }

    @Override
    public GuiPage getCurrentPage() {
        return currentPage;
    }

    @Override
    public Gui setCurrentPage(int index) {
        if (index < 0 || index >= pages.size())
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + pages.size());

        GuiPage oldPage = currentPage;

        currentPageIndex = index;
        currentPage = pages.get(index);

        if (oldPage != null)
            for (Player viewer : oldPage.getViewers())
                open(viewer);

        return this;
    }

    @Override
    public Gui addPage(GuiPage page) {
        pages.add(page);
        if (currentPage == null)
            setCurrentPage(0);

        return this;
    }

    @Override
    public List<GuiPage> getPages() {
        return Collections.unmodifiableList(pages);
    }

    @Override
    public Gui open(Player player) {
        GuiOpenEvent event = new GuiOpenEvent(this, player);

        onOpen.accept(event);
        if (!event.isCancelled())
            currentPage.open(player);

        return this;
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
    public Gui setOnOpen(Consumer<GuiOpenEvent> onOpen) {
        this.onOpen = onOpen;
        return this;
    }

    @Override
    public Gui setOnClose(Consumer<GuiCloseEvent> onClose) {
        this.onClose = onClose;
        return this;
    }

    @Override
    public Consumer<GuiCloseEvent> getOnClose() {
        return onClose;
    }

    @Override
    public Consumer<GuiOpenEvent> getOnOpen() {
        return onOpen;
    }
}
