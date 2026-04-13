package engine;

import model.*;

public class PlayerUtilityCalculator {

    private final PlayerCostCalculator costCalc = new PlayerCostCalculator();
    private final double B; // großer positiver "Benefit"

    public PlayerUtilityCalculator(double B) {
        this.B = B;
    }

    public double utility(Player p, GameState state) {
        return B - costCalc.cost(p, state);
    }
}
