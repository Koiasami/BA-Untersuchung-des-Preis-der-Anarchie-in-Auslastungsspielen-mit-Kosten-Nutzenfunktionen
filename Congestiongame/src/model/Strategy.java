package model;

import java.util.*;

public class Strategy {
    private final String name;
    private final List<Resource> resources;

    public Strategy(String name, List<Resource> resources) {
        this.name = name;
        this.resources = resources;
    }

    public String getName() {
        return name;
    }

    public List<Resource> getResources() {
        return resources;
    }
}