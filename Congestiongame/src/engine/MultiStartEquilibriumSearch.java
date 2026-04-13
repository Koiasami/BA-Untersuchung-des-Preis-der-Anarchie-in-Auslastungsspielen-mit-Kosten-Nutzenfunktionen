package engine;

import model.*;

import java.util.*;

public class MultiStartEquilibriumSearch {

    private final List<Player> players;
    private final PotentialCalculator calculator;
    private final Random rnd = new Random();

    public MultiStartEquilibriumSearch(List<Player> players,
                                       PotentialCalculator calculator) {
        this.players = players;
        this.calculator = calculator;
    }

    // Zufälliger Startzustand
    private GameState randomStartState() {
        Map<Player, Strategy> init = new HashMap<>();
        for (Player p : players) {
            List<Strategy> s = p.getStrategies();
            init.put(p, s.get(rnd.nextInt(s.size())));
        }
        return new GameState(players, init);
    }

    // Suche alle NE über Multi-Start
    public Set<GameState> search(int iterations) {
        Set<GameState> equilibria = new HashSet<>();

        BestResponseEngine engine = new BestResponseEngine(players, calculator);

        for (int i = 0; i < iterations; i++) {
            GameState start = randomStartState();
            GameState eq = engine.findEquilibrium(start.copy());
            equilibria.add(eq);
        }
        return equilibria;
    }
}
