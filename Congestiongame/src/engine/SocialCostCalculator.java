package engine;

import model.GameState;
import model.Resource;
import model.Strategy;

import java.util.*;

public class SocialCostCalculator {

    public double compute(GameState state) {

        Map<Resource, Integer> loads = new HashMap<>();

        // Loads zählen
        for (Strategy s : state.getProfile().values()) {
            for (Resource r : s.getResources()) {
                loads.merge(r, 1, Integer::sum);
            }
        }

        double cost = 0.0;

        // Soziale Kosten: jede Ressource genau einmal
        for (Map.Entry<Resource, Integer> e : loads.entrySet()) {
            Resource r = e.getKey();
            int k = e.getValue();
            cost += k * r.costAtLoad(k);
        }

        return cost;
    }
}
