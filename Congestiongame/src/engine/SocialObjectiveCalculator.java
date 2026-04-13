package engine;

import model.*;

public class SocialObjectiveCalculator {

    private final PlayerObjectiveCalculator obj;

    public SocialObjectiveCalculator(PlayerObjectiveCalculator obj) {
        this.obj = obj;
    }

    public double compute(GameState state) {
        double sum = 0.0;
        for (Player p : state.getProfile().keySet()) {
            sum += obj.value(p, state);
        }
        return sum;
    }
}
