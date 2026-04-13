package engine;

import model.*;

import java.util.*;

public class SocialObjectiveCalculator {

    /**
     * Social objective computed as:
     *   sum_over_resources load(r) * value_r(load(r))
     * In COST mode, this is the social cost (if resource values are costs).
     * In UTILITY mode, this is the social welfare (if resource values are utilities).
     */
    public double compute(GameState state) {
        Map<Resource, Integer> loads = new HashMap<>();
        for (Strategy s : state.getProfile().values()) {
            for (Resource r : s.getResources()) {
                loads.merge(r, 1, Integer::sum);
            }
        }

        double sum = 0.0;
        for (var e : loads.entrySet()) {
            Resource r = e.getKey();
            int k = e.getValue();
            sum += k * r.valueAtLoad(k);
        }
        return sum;
    }
}
