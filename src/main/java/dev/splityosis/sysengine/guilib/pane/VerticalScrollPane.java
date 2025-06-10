package dev.splityosis.sysengine.guilib.pane;

import dev.splityosis.sysengine.common.collections.OffsetList;
import dev.splityosis.sysengine.guilib.components.GuiItem;
import dev.splityosis.sysengine.guilib.components.GuiPage;
import dev.splityosis.sysengine.guilib.components.PaneLayout;
import dev.splityosis.sysengine.guilib.exceptions.UnsupportedPaneOperationException;
import dev.splityosis.sysengine.guilib.intenral.AbstractPane;

import java.util.*;

public class VerticalScrollPane extends AbstractPane<VerticalScrollPane> {

    private final int logicalWidth;   // number of columns
    private final int logicalHeight;  // number of visible rows

    private final OffsetList<GuiItem[]> rows = new OffsetList<>();
    private int rowOffset = 0;

    private final Map<Integer, GuiItem> items = new HashMap<>();
    private boolean populated = false;

    public VerticalScrollPane(int logicalWidth, int logicalHeight, PaneLayout layout) {
        super(layout);
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
    }

    @Override
    protected void onAttach(GuiPage page) {
        // no-op
    }

    @Override
    protected void onDirectItemSet(int localSlot, GuiItem item) throws UnsupportedPaneOperationException {
        int row = getRowIndexAt(localSlot);
        int col = getColumnIndexAt(localSlot);
        ensureRowExists(row);
        getRow(row)[col] = item;
    }

    @Override
    public Map<Integer, GuiItem> getLocalItems() {
        if (!populated) {
            populated = true;
            updateVisibleItems();
        }
        return items;
    }

    public int getMinRow() {
        return rows.getMinIndex();
    }

    public int getMaxRow() {
        return rows.getMaxIndex();
    }

    public int getRowCount() {
        return rows.size();
    }

    public GuiItem[] getRow(int row) {
        return rows.get(row);
    }

    public VerticalScrollPane clear() {
        rows.clear();
        return this;
    }

    public VerticalScrollPane clearRow(int row) {
        GuiItem[] rowItems = rows.get(row);
        for (int c = 0; c < logicalWidth; c++) {
            rowItems[c] = null;
        }
        return this;
    }

    public VerticalScrollPane removeRow(int row) {
        rows.removeAt(row);
        return this;
    }

    public VerticalScrollPane setRow(int row, GuiItem[] items) {
        ensureRowExists(row);
        GuiItem[] rowItems = rows.get(row);
        for (int c = 0; c < logicalWidth; c++) {
            rowItems[c] = c < items.length ? items[c] : null;
        }
        return this;
    }

    public VerticalScrollPane setItem(int row, int col, GuiItem item) {
        ensureRowExists(row);
        if (col < 0 || col >= logicalWidth) {
            throw new IndexOutOfBoundsException("Column " + col + " out of bounds [0 - " + (logicalWidth - 1) + "]");
        }
        rows.get(row)[col] = item;
        return this;
    }

    public int getRowIndexAt(int slot) {
        return rowOffset + slot / logicalWidth;
    }

    public int getColumnIndexAt(int slot) {
        return slot % logicalWidth;
    }

    public VerticalScrollPane setScrollOffset(int offset) {
        this.rowOffset = offset;
        updateVisibleItems();
        return this;
    }

    public VerticalScrollPane scrollUp(int rows) {
        return setScrollOffset(this.rowOffset - rows);
    }

    public VerticalScrollPane scrollDown(int rows) {
        return setScrollOffset(this.rowOffset + rows);
    }

    public int getScrollOffset() {
        return this.rowOffset;
    }

    public VerticalScrollPane scrollToIncludeRow(int row) {
        if (row < rowOffset) {
            return setScrollOffset(row);
        }
        if (row >= rowOffset + logicalHeight) {
            return setScrollOffset(row - logicalHeight + 1);
        }
        return this;
    }

    public List<GuiItem[]> getVisibleRows() {
        List<GuiItem[]> visible = new ArrayList<>(logicalHeight);
        for (int r = 0; r < logicalHeight; r++) {
            int realRow = rowOffset + r;
            visible.add(rows.containsIndex(realRow) ? rows.get(realRow) : null);
        }
        return visible;
    }

    public int getLogicalWidth() {
        return logicalWidth;
    }

    public int getLogicalHeight() {
        return logicalHeight;
    }

    private void updateVisibleItems() {
        items.clear();
        for (int r = 0; r < logicalHeight; r++) {
            int realRow = rowOffset + r;
            if (!rows.containsIndex(realRow)) continue;
            GuiItem[] rowItems = rows.get(realRow);
            for (int c = 0; c < logicalWidth; c++) {
                GuiItem item = rowItems[c];
                if (item != null) {
                    int slot = r * logicalWidth + c;
                    items.put(slot, item);
                }
            }
        }
    }

    private void ensureRowExists(int row) {
        while (rows.getMinIndex() > row) {
            rows.addFront(new GuiItem[logicalWidth]);
        }
        while (rows.getMaxIndex() < row) {
            rows.addBack(new GuiItem[logicalWidth]);
        }
    }
}
