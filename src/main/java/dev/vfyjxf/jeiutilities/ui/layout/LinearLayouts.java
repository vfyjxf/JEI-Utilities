package dev.vfyjxf.jeiutilities.ui.layout;

import dev.vfyjxf.jeiutilities.ui.widgets.Widget;
import dev.vfyjxf.jeiutilities.ui.widgets.WidgetGroup;

import java.util.List;

public final class LinearLayouts {

    private LinearLayouts() {
    }


    public static class Horizontal implements IWidgetLayout {

        public static final Horizontal DEFAULT = new Horizontal(0);

        private final int spacing;

        public Horizontal(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public <T extends Widget> void layout(WidgetGroup<T> panel, List<T> children) {
            int x = 0;
            for (T child : children) {
                child.setX(x);
                x += child.getSize().width + spacing;
            }
        }

    }

    public static class Vertical implements IWidgetLayout {

        public static final Vertical DEFAULT = new Vertical(0);

        private final int spacing;

        public Vertical(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public <T extends Widget> void layout(WidgetGroup<T> panel, List<T> children) {
            int y = 0;
            for (T child : children) {
                child.setY(y);
                y += child.getSize().height + spacing;
            }
        }

    }



}
