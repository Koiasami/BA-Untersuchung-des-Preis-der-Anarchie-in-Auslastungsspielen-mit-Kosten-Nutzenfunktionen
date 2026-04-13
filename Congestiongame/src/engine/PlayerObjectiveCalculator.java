package engine;

import model.*;

import java.util.*;

public class PlayerObjectiveCalculator {

    public enum Mode { COST, UTILITY }

    private final Mode mode;

    public PlayerObjectiveCalculator(Mode mode) {
        this.mode = mode;
    }

    public double value(Player p, GameState state) {

        Map<Resource, Integer> loads = new HashMap<>();
        for (Strategy s : state.getProfile().values()) {
            for (Resource r : s.getResources()) {
                loads.merge(r, 1, Integer::sum);
            }
        }

        Strategy sp = state.getStrategy(p);
        double sum = 0.0;

        for (Resource r : sp.getResources()) {
            int k = loads.get(r);
            sum += r.valueAtLoad(k);
        }

        return sum; // interpreted as cost OR utility depending on mode
    }

    public boolean better(double a, double b) {
        return mode == Mode.COST ? a < b : a > b;
    }
}
