package engine;

import model.GameState;
import model.Resource;
import model.Strategy;

import java.util.*;

public class PotentialCalculator {

    public enum Mode { COST, UTILITY }

    private final Mode mode;

    public PotentialCalculator(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }
    public double compute(GameState state) {
        Map<Resource, Integer> loads = new HashMap<>();

        // Count loads
        for (Strategy s : state.getProfile().values()) {
            for (Resource r : s.getResources()) {
                loads.put(r, loads.getOrDefault(r, 0) + 1);
            }
        }

        double potential = 0.0;

        for (Map.Entry<Resource, Integer> e : loads.entrySet()) {
            Resource r = e.getKey();
            int load = e.getValue();

            for (int k = 1; k <= load; k++) {
                double value = r.costAtLoad(k);
                potential += (mode == Mode.COST ? value : -value);
            }
        }

        return potential;
    }
}
