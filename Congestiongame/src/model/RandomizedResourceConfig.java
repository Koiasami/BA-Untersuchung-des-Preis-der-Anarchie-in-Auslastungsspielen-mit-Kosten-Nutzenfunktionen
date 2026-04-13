package model;


public class RandomizedResourceConfig {

    public final double xMin, xMax;
    public final double pMin, pMax;
    public final double yMin, yMax;

    public RandomizedResourceConfig(
            double xMin, double xMax,
            double pMin, double pMax,
            double yMin, double yMax) {

        this.xMin = xMin;
        this.xMax = xMax;
        this.pMin = pMin;
        this.pMax = pMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }
}
