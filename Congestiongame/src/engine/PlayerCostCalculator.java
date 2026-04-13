package engine;

import model.*;

import java.util.*;

public class PlayerCostCalculator {

    public double cost(Player p, GameState state) {

        // Loads zählen
        Map<Resource, Integer> loads = new HashMap<>();
        for (Strategy s : state.getProfile().values()) {
            for (Resource r : s.getResources()) {
                loads.merge(r, 1, Integer::sum);
            }
        }

        // Spieler-Kosten: Summe der Ressourcenkosten auf seiner Strategie bei aktueller Last
        double c = 0.0;
        Strategy sp = state.getStrategy(p);
        for (Resource r : sp.getResources()) {
            int k = loads.getOrDefault(r, 0);
            c += r.costAtLoad(k);
        }
        return c;
    }
}
