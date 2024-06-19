package dev.vfyjxf.jeiutilities.ui.widgets;

import dev.vfyjxf.jeiutilities.ui.layout.Constraints;
import dev.vfyjxf.jeiutilities.ui.layout.LinearLayouts;

public class Column<T extends Widget> extends WidgetGroup<T> {

    public Column() {
        this.layout = LinearLayouts.Horizontal.DEFAULT;
    }

    public Column(int spacing) {
        this.layout = new LinearLayouts.Horizontal(spacing);
    }

    @Override
    protected Widget layout(WidgetGroup<?> parent, Constraints constraints) {
        return super.layout(parent, constraints);
    }
}
