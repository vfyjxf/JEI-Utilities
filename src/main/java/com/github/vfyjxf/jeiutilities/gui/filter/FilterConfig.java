package com.github.vfyjxf.jeiutilities.gui.filter;

public class FilterConfig {

    private boolean isFocusInput = true;
    private boolean isSearchInput = true;
    private boolean isSearchAdvancedTooltips = false;

    public boolean isFocusInput() {
        return isFocusInput;
    }

    public boolean isSearchInput() {
        return isSearchInput;
    }

    public boolean isSearchAdvancedTooltips() {
        return isSearchAdvancedTooltips;
    }

    public void setFocusInput(boolean focusInput) {
        isFocusInput = focusInput;
    }

    public void setSearchInput(boolean searchInput) {
        isSearchInput = searchInput;
    }

}
