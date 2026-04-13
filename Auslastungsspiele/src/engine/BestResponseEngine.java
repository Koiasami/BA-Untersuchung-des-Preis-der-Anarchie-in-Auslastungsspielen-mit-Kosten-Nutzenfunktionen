package engine;

import model.*;

import java.util.*;

public class BestResponseEngine {

    private final List<Player> players;
    private final ObjectiveMode mode;
    private final PotentialCalculator potentialCalc;

    public BestResponseEngine(List<Player> players, ObjectiveMode mode, PotentialCalculator calc) {
        this.players = players;
        this.mode = mode;
        this.potentialCalc = calc;
    }

    private boolean better(double newValue, double bestValue) {
        return mode == ObjectiveMode.COST ? (newValue < bestValue) : (newValue > bestValue);
    }

    public GameState findEquilibrium(GameState start) {

        for (Player p : players) {
            if (start.getStrategy(p) == null) {
                start.applyStrategy(p, p.getStrategies().get(0));
            }
        }

        boolean changed;
        do {
            changed = false;

            for (Player p : players) {
                double bestValue = potentialCalc.compute(start);
                Strategy bestStrategy = start.getStrategy(p);

                for (Strategy s : p.getStrategies()) {
                    start.applyStrategy(p, s);
                    double newValue = potentialCalc.compute(start);

                    if (better(newValue, bestValue)) {
                        bestValue = newValue;
                        bestStrategy = s;
                        changed = true;
                    }
                }

                start.applyStrategy(p, bestStrategy);
            }

        } while (changed);

        return start;
    }
}
