package engine;

import model.*;

import java.util.*;

public class PlayerValueCalculator {

    /** Player's objective value (sum of resource values on chosen strategy at current loads). */
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
            int k = loads.getOrDefault(r, 0);
            sum += r.valueAtLoad(k);
        }

        return sum;
    }
}
