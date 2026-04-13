package model;

public class Resource {

    private final String name;
    private final ResourceCostFunction function;

    public Resource(String name, ResourceCostFunction function) {
        this.name = name;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public double valueAtLoad(int load) {
        return function.evaluate(load);
    }
}

