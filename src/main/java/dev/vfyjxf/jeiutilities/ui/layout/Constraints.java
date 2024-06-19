package dev.vfyjxf.jeiutilities.ui.layout;

public class Constraints {

    public static final int Infinity = Integer.MAX_VALUE;

    private final int minWidth;
    private final int minHeight;
    private final int maxWidth;
    private final int maxHeight;

    public Constraints(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public boolean isZero() {
        return minWidth == 0 && minHeight == 0 && maxWidth == 0 && maxHeight == 0;
    }

    public Constraints copy() {
        return new Constraints(minWidth, minHeight, maxWidth, maxHeight);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Constraints that)) return false;

        return minWidth == that.minWidth &&
                minHeight == that.minHeight &&
                maxWidth == that.maxWidth &&
                maxHeight == that.maxHeight;
    }

    @Override
    public int hashCode() {
        int result = minWidth;
        result = 31 * result + minHeight;
        result = 31 * result + maxWidth;
        result = 31 * result + maxHeight;
        return result;
    }


    @Override
    public String toString() {
        return "Constraints{" +
                "minWidth=" + minWidth +
                ", minHeight=" + minHeight +
                ", maxWidth=" + maxWidth +
                ", maxHeight=" + maxHeight +
                '}';
    }
}
