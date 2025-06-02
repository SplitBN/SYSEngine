package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.components.Gui;
import dev.splityosis.sysengine.guilib.components.GuiPage;
import dev.splityosis.sysengine.guilib.events.GuiCloseEvent;
import dev.splityosis.sysengine.guilib.events.GuiEvent;
import dev.splityosis.sysengine.guilib.events.GuiOpenEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractGui implements Gui {

    private int currentPageIndex;
    private List<AbstractGuiPage> pages = new ArrayList<>();
    private AbstractGuiPage currentPage;

    private GuiEvent<GuiOpenEvent> onOpen = e->{};
    private GuiEvent<GuiCloseEvent> onClose = e->{};

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
        if (! (page instanceof AbstractGuiPage))
            throw new IllegalArgumentException("page must be an instance of AbstractGuiPage");

        AbstractGuiPage abstractGuiPage = (AbstractGuiPage) page;
        if (abstractGuiPage.getParentGui() != null)
            throw new IllegalArgumentException("page already has parent gui");

        abstractGuiPage.setParentGui(this);
        pages.add(abstractGuiPage);
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
        onOpen.call(event);

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
    public Gui onOpen(GuiEvent<GuiOpenEvent> onOpen) {
        this.onOpen = onOpen;
        return this;
    }

    @Override
    public Gui onClose(GuiEvent<GuiCloseEvent> onClose) {
        this.onClose = onClose;
        return this;
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
