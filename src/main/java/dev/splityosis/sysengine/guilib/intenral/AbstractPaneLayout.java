package dev.splityosis.sysengine.guilib.intenral;

import dev.splityosis.sysengine.guilib.PaneLayout;

public abstract class AbstractPaneLayout implements PaneLayout {

    private boolean initialized;

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    protected void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
