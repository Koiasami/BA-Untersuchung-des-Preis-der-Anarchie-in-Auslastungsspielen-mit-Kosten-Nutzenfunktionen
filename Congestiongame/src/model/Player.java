package model;

import java.util.*;

public class Player {
    private final String name;
    private final List<Strategy> strategies;

    public Player(String name, List<Strategy> strategies) {
        this.name = name;
        this.strategies = strategies;
    }

    public String getName() {
        return name;
    }

    public List<Strategy> getStrategies() {
        return strategies;
    }
}
