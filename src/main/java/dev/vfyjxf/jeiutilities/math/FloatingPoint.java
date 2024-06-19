package dev.vfyjxf.jeiutilities.math;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class FloatingPoint {
    public double x;
    public double y;

    public FloatingPoint() {
        this(0, 0);
    }

    public FloatingPoint(Point p) {
        this(p.x, p.y);
    }

    public FloatingPoint(FloatingPoint p) {
        this(p.x, p.y);
    }

    public FloatingPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public FloatingPoint getFloatingLocation() {
        return new FloatingPoint(x, y);
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public FloatingPoint copy() {
        return getFloatingLocation();
    }

    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void translate(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FloatingPoint) {
            FloatingPoint pt = (FloatingPoint) obj;
            return (x == pt.x) && (y == pt.y);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        return result;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }
}