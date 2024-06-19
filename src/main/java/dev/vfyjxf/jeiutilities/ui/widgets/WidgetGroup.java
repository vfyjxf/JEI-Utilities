package dev.vfyjxf.jeiutilities.ui.widgets;

import dev.vfyjxf.jeiutilities.api.event.IWidgetEvent;
import dev.vfyjxf.jeiutilities.ui.layout.IWidgetLayout;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WidgetGroup<T extends Widget> extends Widget implements Iterable<T> {

    protected IWidgetLayout layout;
    protected List<T> children = new ArrayList<>();

    public List<T> children() {
        return new ArrayList<>(children);
    }

    public int size() {
        return children.size();
    }

    public WidgetGroup<T> add(T widget) {
        this.add(children.size(), widget);
        return this;
    }

    public boolean add(int index, T widget) {
        if (!children.contains(widget)) {
            var context = context();
            listeners(IWidgetEvent.onChildAdded).onChildAdded(context, widget);
            if (context.isCancelled()) return false;
            children.add(index, widget);
            widget.setParent(this);
            listeners(IWidgetEvent.onChildAddedPost).onChildAdded(context, widget);
            return true;
        }
        return false;
    }

    public boolean remove(T widget) {
        int index = children.indexOf(widget);
        if (index < 0) return false;
        return remove(index);
    }

    public boolean remove(int index) {
        if (index < 0 || index >= children.size()) return false;
        Widget child = children.get(index);
        child.onDelete();
        child.setParent(null);
        return children.remove(index) != null;
    }

    public void clear() {
        for (int i = 0; i < children.size(); i++) {
            remove(i);
        }
        initialized = false;
    }

    public boolean contains(T widget) {
        return children.contains(widget);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return children.iterator();
    }

}
