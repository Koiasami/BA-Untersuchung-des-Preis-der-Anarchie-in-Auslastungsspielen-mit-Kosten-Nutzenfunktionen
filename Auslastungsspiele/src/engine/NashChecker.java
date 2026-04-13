package engine;

import model.*;

public class NashChecker {

    private static final double EPS = 1e-9;

    private final ObjectiveMode mode;
    private final PlayerValueCalculator pvc = new PlayerValueCalculator();

    public NashChecker(ObjectiveMode mode) {
        this.mode = mode;
    }

    public boolean isNash(GameState state) {

        for (Player p : state.getProfile().keySet()) {

            Strategy cur = state.getStrategy(p);
            double curVal = pvc.value(p, state);

            for (Strategy alt : p.getStrategies()) {
                if (alt == cur) continue;

                state.applyStrategy(p, alt);
                double altVal = pvc.value(p, state);
                state.applyStrategy(p, cur);

                if (mode == ObjectiveMode.COST) {
                    if (altVal < curVal - EPS) return false;
                } else {
                    if (altVal > curVal + EPS) return false;
                }
            }
        }
        return true;
    }
}
