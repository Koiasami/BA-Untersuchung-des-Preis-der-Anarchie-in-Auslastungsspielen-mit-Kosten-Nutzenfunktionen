package model;

public class RandomizedResource extends Resource {

    public final double x;
    public final double p;
    public final double y;

    public RandomizedResource(String name, double x, double p, double y) {
        super(name, k -> x * Math.pow(k, p) + y);
        this.x = x;
        this.p = p;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("%s: %.4f * k^%.4f + %.4f", getName(), x, p, y);
    }
}
