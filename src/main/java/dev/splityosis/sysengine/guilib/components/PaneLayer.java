package dev.splityosis.sysengine.guilib.components;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

/**
 * A data class that holds "The GuiItems that the GuiPage recognizes from the Pane".
 */
public class PaneLayer {

    private final Pane pane;
    private final Map<Integer,GuiItem> rawToItemMap = new HashMap<>();

    public PaneLayer(Pane pane) {
        this.pane = pane;
    }

    public GuiItem getItemAtRawSlot(int rawSlot) {
        return rawToItemMap.get(rawSlot);
    }

    public GuiItem getItemAtLocalSlot(int localSlot) {
        int raw = pane.getLayout().toRawSlot(localSlot).orElse(-1);
        return getItemAtRawSlot(raw);
    }

    public void updateAll() {
        rawToItemMap.clear();

        for (Map.Entry<Integer, GuiItem> e : pane.getLocalItems().entrySet()) {
            int local = e.getKey();
            GuiItem item = e.getValue();
            pane.getLayout().toRawSlot(local).ifPresent(raw -> rawToItemMap.put(raw, item));
        }
    }

    public void updateRawSlot(int rawSlot) {
        OptionalInt maybeLocal = pane.getLayout().toLocalSlot(rawSlot);

        if (maybeLocal.isPresent()) {

            int local = maybeLocal.getAsInt();
            GuiItem item = pane.getLocalItems().get(local);
            if (item == null)
                rawToItemMap.remove(rawSlot);
            else
                rawToItemMap.put(rawSlot, item);

        } else
            rawToItemMap.remove(rawSlot);
    }

    public void updateLocalSlot(int localSlot) {
        pane.getLayout().toRawSlot(localSlot).ifPresent(rawSlot -> {
            GuiItem item = pane.getLocalItems().get(localSlot);
            if (item == null)
                rawToItemMap.remove(rawSlot);
            else
                rawToItemMap.put(rawSlot, item);
        });
    }

    public Map<Integer, GuiItem> getRawToItemMap() {
        return rawToItemMap;
    }

    public Pane getPane() {
        return pane;
    }
}
