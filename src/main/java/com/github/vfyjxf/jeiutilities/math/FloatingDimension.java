package com.github.vfyjxf.jeiutilities.math;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class FloatingDimension {
    public double width;
    public double height;

    public FloatingDimension() {
        this(0, 0);
    }

    public FloatingDimension(Dimension d) {
        this(d.width, d.height);
    }

    public FloatingDimension(FloatingDimension d) {
        this(d.width, d.height);
    }

    public FloatingDimension(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public FloatingDimension getFloatingSize() {
        return new FloatingDimension(width, height);
    }

    public Dimension getSize() {
        return new Dimension(width, height);
    }

    public void setSize(FloatingDimension d) {
        setSize(d.width, d.height);
    }

    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FloatingDimension) {
            FloatingDimension d = (FloatingDimension) obj;
            return (width == d.width) && (height == d.height);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Double.hashCode(width);
        result = 31 * result + Double.hashCode(height);
        return result;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }

    public FloatingDimension copy() {
        return getFloatingSize();
    }
}