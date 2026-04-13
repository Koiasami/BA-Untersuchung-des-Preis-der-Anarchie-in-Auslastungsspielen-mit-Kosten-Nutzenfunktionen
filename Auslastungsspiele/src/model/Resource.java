package model;

public class Resource {
    private final String name;
    private final ResourceValueFunction function;

    public Resource(String name, ResourceValueFunction function) {
        this.name = name;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    /** Value at load k: interpreted as COST in COST-mode, as UTILITY in UTILITY-mode. */
    public double valueAtLoad(int load) {
        return function.evaluate(load);
    }

    @Override
    public String toString() {
        return name;
    }
}
