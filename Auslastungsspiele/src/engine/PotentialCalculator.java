package engine;

import model.*;

import java.util.*;

public class PotentialCalculator {

    /**
     * Rosenthal-style potential for congestion games with additive player values:
     *   Phi(s) = sum_r sum_{k=1..load_r(s)} value_r(k)
     *
     * If values are costs: BR-minimizing potential converges to a PNE.
     * If values are utilities: BR-maximizing potential converges to a PNE.
     */
    public double compute(GameState state) {

        Map<Resource, Integer> loads = new HashMap<>();
        for (Strategy s : state.getProfile().values()) {
            for (Resource r : s.getResources()) {
                loads.merge(r, 1, Integer::sum);
            }
        }

        double potential = 0.0;
        for (var e : loads.entrySet()) {
            Resource r = e.getKey();
            int load = e.getValue();
            for (int k = 1; k <= load; k++) {
                potential += r.valueAtLoad(k);
            }
        }
        return potential;
    }
}
