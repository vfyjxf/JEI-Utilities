package dev.vfyjxf.jeiutilities.math;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class FloatingRectangle {
    public double x;
    public double y;
    public double width;
    public double height;

    public FloatingRectangle() {
        this(0, 0, 0, 0);
    }

    public FloatingRectangle(FloatingRectangle r) {
        this(r.x, r.y, r.width, r.height);
    }

    public FloatingRectangle(Rectangle r) {
        this(r.x, r.y, r.width, r.height);
    }

    public FloatingRectangle(int width, int height) {
        this(0, 0, width, height);
    }

    public FloatingRectangle(Point p, Dimension d) {
        this(p.x, p.y, d.width, d.height);
    }

    public FloatingRectangle(Point p, FloatingDimension d) {
        this(p.x, p.y, d.width, d.height);
    }

    public FloatingRectangle(FloatingPoint p, Dimension d) {
        this(p.x, p.y, d.width, d.height);
    }

    public FloatingRectangle(FloatingPoint p, FloatingDimension d) {
        this(p.x, p.y, d.width, d.height);
    }

    public FloatingRectangle(Point p) {
        this(p.x, p.y, 0, 0);
    }

    public FloatingRectangle(FloatingPoint p) {
        this(p.x, p.y, 0, 0);
    }

    public FloatingRectangle(Dimension d) {
        this(0, 0, d.width, d.height);
    }

    public FloatingRectangle(FloatingDimension d) {
        this(0, 0, d.width, d.height);
    }

    public FloatingRectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public double getMinX() {
        return x;
    }

    public double getMaxX() {
        return x + width;
    }

    public double getCenterX() {
        return x + width / 2;
    }

    public double getY() {
        return y;
    }

    public double getMinY() {
        return y;
    }

    public double getMaxY() {
        return y + height;
    }

    public double getCenterY() {
        return y + height / 2;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public FloatingRectangle getFloatingBounds() {
        return new FloatingRectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void setBounds(FloatingRectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

    public void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

    public void setBounds(double x, double y, double width, double height) {
        reshape(x, y, width, height);
    }

    @Deprecated
    public void reshape(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public FloatingPoint getFloatingLocation() {
        return new FloatingPoint(x, y);
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public void setLocation(Point p) {
        setLocation(p.x, p.y);
    }

    public void setLocation(FloatingPoint p) {
        setLocation(p.x, p.y);
    }

    public void setLocation(double x, double y) {
        move(x, y);
    }

    @Deprecated
    public void move(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void translate(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }


    public FloatingRectangle copy() {
        return getFloatingBounds();
    }

    public Dimension getSize() {
        return new Dimension(width, height);
    }

    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }

    public void setSize(FloatingDimension d) {
        setSize(d.width, d.height);
    }

    public void setSize(double width, double height) {
        resize(width, height);
    }

    @Deprecated
    public void resize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    public boolean contains(FloatingPoint p) {
        return contains(p.x, p.y);
    }

    public boolean contains(int x, int y) {
        return inside(x, y);
    }

    public boolean contains(double x, double y) {
        return inside((int) x, (int) y);
    }

    public boolean contains(Rectangle r) {
        return contains(r.x, r.y, r.width, r.height);
    }

    public boolean contains(FloatingRectangle r) {
        return contains(r.x, r.y, r.width, r.height);
    }

    public boolean contains(double X, double Y, double W, double H) {
        return contains(X, Y) && contains(W, H);
    }

    @Deprecated
    public boolean inside(double x, double y) {
        double thisX = this.x;
        double thisY = this.y;
        return !isEmpty() && x >= thisX && x <= thisX + this.width && y >= thisY && y <= thisY + this.height;
    }

    public boolean intersects(FloatingRectangle r) {
        double tw = this.width;
        double th = this.height;
        double rw = r.width;
        double rh = r.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        double tx = this.x;
        double ty = this.y;
        double rx = r.x;
        double ry = r.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
    }

    public FloatingRectangle intersection(FloatingRectangle r) {
        double tx1 = this.x;
        double ty1 = this.y;
        double rx1 = r.x;
        double ry1 = r.y;
        double tx2 = tx1;
        tx2 += this.width;
        double ty2 = ty1;
        ty2 += this.height;
        double rx2 = rx1;
        rx2 += r.width;
        double ry2 = ry1;
        ry2 += r.height;
        if (tx1 < rx1)
            tx1 = rx1;
        if (ty1 < ry1)
            ty1 = ry1;
        if (tx2 > rx2)
            tx2 = rx2;
        if (ty2 > ry2)
            ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never overflow (they will never be
        // larger than the smallest of the two source w,h)
        // they might underflow, though...
        if (tx2 < Double.MIN_VALUE)
            tx2 = Double.MIN_VALUE;
        if (ty2 < Double.MIN_VALUE)
            ty2 = Double.MIN_VALUE;
        return new FloatingRectangle(tx1, ty1, (int) tx2, (int) ty2);
    }

    public FloatingRectangle union(FloatingRectangle r) {
        double tx2 = this.width;
        double ty2 = this.height;
        if (tx2 < 0 || ty2 < 0) {
            // This rectangle has negative dimensions...
            // If r has non-negative dimensions then it is the answer.
            // If r is non-existant (has a negative dimension), then both
            // are non-existant and we can return any non-existant rectangle
            // as an answer.  Thus, returning r meets that criterion.
            // Either way, r is our answer.
            return new FloatingRectangle(r);
        }
        double rx2 = r.width;
        double ry2 = r.height;
        if (rx2 < 0 || ry2 < 0) {
            return new FloatingRectangle(this);
        }
        double tx1 = this.x;
        double ty1 = this.y;
        tx2 += tx1;
        ty2 += ty1;
        double rx1 = r.x;
        double ry1 = r.y;
        rx2 += rx1;
        ry2 += ry1;
        if (tx1 > rx1)
            tx1 = rx1;
        if (ty1 > ry1)
            ty1 = ry1;
        if (tx2 < rx2)
            tx2 = rx2;
        if (ty2 < ry2)
            ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never underflow since both original rectangles
        // were already proven to be non-empty
        // they might overflow, though...
        if (tx2 > Double.MAX_VALUE)
            tx2 = Double.MAX_VALUE;
        if (ty2 > Double.MAX_VALUE)
            ty2 = Double.MAX_VALUE;
        return new FloatingRectangle(tx1, ty1, (int) tx2, (int) ty2);
    }

    public void add(double newx, double newy) {
        if (width < 0 || height < 0) {
            this.x = newx;
            this.y = newy;
            this.width = this.height = 0;
            return;
        }
        double x1 = this.x;
        double y1 = this.y;
        double x2 = this.width;
        double y2 = this.height;
        x2 += x1;
        y2 += y1;
        if (x1 > newx)
            x1 = newx;
        if (y1 > newy)
            y1 = newy;
        if (x2 < newx)
            x2 = newx;
        if (y2 < newy)
            y2 = newy;
        x2 -= x1;
        y2 -= y1;
        if (x2 > Double.MAX_VALUE)
            x2 = Double.MAX_VALUE;
        if (y2 > Double.MAX_VALUE)
            y2 = Double.MAX_VALUE;
        reshape(x1, y1, (int) x2, (int) y2);
    }

    public void add(FloatingPoint pt) {
        add(pt.x, pt.y);
    }

    public void add(Point pt) {
        add(pt.x, pt.y);
    }

    public void add(FloatingRectangle r) {
        double tx2 = this.width;
        double ty2 = this.height;
        if (tx2 < 0 || ty2 < 0) {
            reshape(r.x, r.y, r.width, r.height);
        }
        double rx2 = r.width;
        double ry2 = r.height;
        if (rx2 < 0 || ry2 < 0) {
            return;
        }
        double tx1 = this.x;
        double ty1 = this.y;
        tx2 += tx1;
        ty2 += ty1;
        double rx1 = r.x;
        double ry1 = r.y;
        rx2 += rx1;
        ry2 += ry1;
        if (tx1 > rx1)
            tx1 = rx1;
        if (ty1 > ry1)
            ty1 = ry1;
        if (tx2 < rx2)
            tx2 = rx2;
        if (ty2 < ry2)
            ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never underflow since both original
        // rectangles were non-empty
        // they might overflow, though...
        if (tx2 > Double.MAX_VALUE)
            tx2 = Double.MAX_VALUE;
        if (ty2 > Double.MAX_VALUE)
            ty2 = Double.MAX_VALUE;
        reshape(tx1, ty1, (int) tx2, (int) ty2);
    }

    public void grow(double h, double v) {
        double x0 = this.x;
        double y0 = this.y;
        double x1 = this.width;
        double y1 = this.height;
        x1 += x0;
        y1 += y0;

        x0 -= h;
        y0 -= v;
        x1 += h;
        y1 += v;

        if (x1 < x0) {
            // Non-existant in X direction
            // Final width must remain negative so subtract x0 before
            // it is clipped so that we avoid the risk that the clipping
            // of x0 will reverse the ordering of x0 and x1.
            x1 -= x0;
            if (x1 < Double.MIN_VALUE)
                x1 = Double.MIN_VALUE;
            if (x0 < Double.MIN_VALUE)
                x0 = Double.MIN_VALUE;
            else if (x0 > Double.MAX_VALUE)
                x0 = Double.MAX_VALUE;
        } else { // (x1 >= x0)
            // Clip x0 before we subtract it from x1 in case the clipping
            // affects the representable area of the rectangle.
            if (x0 < Double.MIN_VALUE)
                x0 = Double.MIN_VALUE;
            else if (x0 > Double.MAX_VALUE)
                x0 = Double.MAX_VALUE;
            x1 -= x0;
            // The only way x1 can be negative now is if we clipped
            // x0 against MIN and x1 is less than MIN - in which case
            // we want to leave the width negative since the result
            // did not intersect the representable area.
            if (x1 < Double.MIN_VALUE)
                x1 = Double.MIN_VALUE;
            else if (x1 > Double.MAX_VALUE)
                x1 = Double.MAX_VALUE;
        }

        if (y1 < y0) {
            // Non-existant in Y direction
            y1 -= y0;
            if (y1 < Double.MIN_VALUE)
                y1 = Double.MIN_VALUE;
            if (y0 < Double.MIN_VALUE)
                y0 = Double.MIN_VALUE;
            else if (y0 > Double.MAX_VALUE)
                y0 = Double.MAX_VALUE;
        } else { // (y1 >= y0)
            if (y0 < Double.MIN_VALUE)
                y0 = Double.MIN_VALUE;
            else if (y0 > Double.MAX_VALUE)
                y0 = Double.MAX_VALUE;
            y1 -= y0;
            if (y1 < Double.MIN_VALUE)
                y1 = Double.MIN_VALUE;
            else if (y1 > Double.MAX_VALUE)
                y1 = Double.MAX_VALUE;
        }

        reshape((int) x0, (int) y0, (int) x1, (int) y1);
    }

    public boolean isEmpty() {
        return (width <= 0) || (height <= 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof FloatingRectangle) {
            FloatingRectangle r = (FloatingRectangle) obj;
            return ((x == r.x) && (y == r.y) && (width == r.width) && (height == r.height));
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        result = 31 * result + Double.hashCode(width);
        result = 31 * result + Double.hashCode(height);
        return result;
    }
}