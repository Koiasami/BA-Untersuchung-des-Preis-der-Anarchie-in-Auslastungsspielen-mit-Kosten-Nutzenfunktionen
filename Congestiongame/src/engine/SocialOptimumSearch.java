package engine;

import model.*;

import java.util.*;

public class SocialOptimumSearch {

    private final List<Player> players;
    private final SocialCostCalculator scCalc;

    private GameState bestState = null;
    private double bestCost = Double.POSITIVE_INFINITY;

    public SocialOptimumSearch(List<Player> players,
                               SocialCostCalculator scCalc) {
        this.players = players;
        this.scCalc = scCalc;
    }

    // -------------------------------------------------
    // öffentlich: Optimum finden
    // -------------------------------------------------
    public Result findOptimum() {
        backtrack(0, new HashMap<>());
        return new Result(bestState, bestCost);
    }

    // -------------------------------------------------
    // rekursive Enumeration aller Profile
    // -------------------------------------------------
    private void backtrack(int index, Map<Player, Strategy> current) {

        if (index == players.size()) {
            GameState state = new GameState(players, current);
            double sc = scCalc.compute(state);

            if (sc < bestCost) {
                bestCost = sc;
                bestState = state.copy();
            }
            return;
        }

        Player p = players.get(index);
        for (Strategy s : p.getStrategies()) {
            current.put(p, s);
            backtrack(index + 1, current);
        }
    }

    // -------------------------------------------------
    // Ergebniscontainer
    // -------------------------------------------------
    public static class Result {
        public final GameState optimum;
        public final double cost;

        public Result(GameState optimum, double cost) {
            this.optimum = optimum;
            this.cost = cost;
        }
    }
}
