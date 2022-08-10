package com.github.vfyjxf.jeiutilities.gui.filter;

import mezz.jei.gui.overlay.IFilterTextSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FilterTextContent implements IFilterTextSource {

    private final List<Listener> listeners = new ArrayList<>();
    private String filterText = "";

    @Override
    public @NotNull String getFilterText() {
        return this.filterText;
    }

    @Override
    public boolean setFilterText(@NotNull String filterText) {
        if (this.filterText.equals(filterText)) {
            return false;
        }
        this.filterText = filterText;
        for (Listener listener : this.listeners) {
            listener.onChange(filterText);
        }
        return true;
    }

    @Override
    public void addListener(@NotNull Listener listener) {
        this.listeners.add(listener);
    }

}
