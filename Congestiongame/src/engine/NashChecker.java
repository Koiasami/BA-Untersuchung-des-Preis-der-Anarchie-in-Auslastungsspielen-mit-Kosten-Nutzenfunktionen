package engine;

import model.*;

public class NashChecker {

    private static final double EPS = 1e-9;
    private final PlayerObjectiveCalculator obj;

    public NashChecker(PlayerObjectiveCalculator obj) {
        this.obj = obj;
    }

    public boolean isNash(GameState state) {

        for (Player p : state.getProfile().keySet()) {

            Strategy cur = state.getStrategy(p);
            double curVal = obj.value(p, state);

            for (Strategy alt : p.getStrategies()) {
                if (alt == cur) continue;

                state.applyStrategy(p, alt);
                double altVal = obj.value(p, state);
                state.applyStrategy(p, cur);

                if (obj.better(altVal, curVal)) return false;
            }
        }
        return true;
    }
}
