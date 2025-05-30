package dev.splityosis.sysengine.guilib.pane;

import dev.splityosis.sysengine.guilib.GuiItem;
import dev.splityosis.sysengine.guilib.PaneLayout;
import dev.splityosis.sysengine.guilib.intenral.AbstractPane;

import java.util.Collections;
import java.util.Map;

public class StaticPane extends AbstractPane {

    public StaticPane(PaneLayout layout) {
        super(layout);
    }

    @Override
    public Map<Integer, GuiItem> getLocalItems() {
        return Collections.emptyMap();
    }
}
