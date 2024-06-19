package dev.vfyjxf.jeiutilities.ui.layout;

import dev.vfyjxf.jeiutilities.ui.widgets.Widget;
import dev.vfyjxf.jeiutilities.ui.widgets.WidgetGroup;

import java.util.List;

public interface IWidgetLayout {

    <T extends Widget> void layout(WidgetGroup<T> panel, List<T> children);

}
