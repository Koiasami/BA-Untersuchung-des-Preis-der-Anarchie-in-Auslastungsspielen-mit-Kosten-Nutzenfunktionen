package engine;

import games.RandomGameFactory;
import model.GameState;
import model.Player;

import java.util.*;

public class ExperimentRunner {

    public enum EvalMode { COST, UTILITY }

    private final RandomGameFactory factory;
    private final int iterationsPerGame;   // nur relevant, falls fallback MultiStart
    private final int gamesPerPlayerCount;
    private final EvalMode evalMode;

    // exakte NE Suche bis zu dieser Profilanzahl, sonst fallback MultiStart
    private final int maxProfilesForExactSearch;

    public ExperimentRunner(RandomGameFactory factory,
                            int gamesPerPlayerCount,
                            EvalMode evalMode,
                            int maxProfilesForExactSearch,
                            int iterationsPerGame) {
        this.factory = factory;
        this.gamesPerPlayerCount = gamesPerPlayerCount;
        this.evalMode = evalMode;
        this.maxProfilesForExactSearch = maxProfilesForExactSearch;
        this.iterationsPerGame = iterationsPerGame;
    }

    public void run(int minPlayers, int maxPlayers, Random rnd) {

        for (int n = minPlayers; n <= maxPlayers; n++) {

            System.out.println("\n=======================================");
            System.out.println("SPIELERANZAHL = " + n + " | MODE = " + evalMode);
            System.out.println("=======================================");

            double globalMaxPoA = Double.NEGATIVE_INFINITY;
            int globalMaxCount = 0;

            RandomGameFactory.Instance bestInst = null;
            GameState bestState = null;
            GameState worstState = null;
            double bestValue = 0, worstValue = 0;

            for (int g = 1; g <= gamesPerPlayerCount; g++) {

                var inst = factory.sample(n, rnd);
                GameState base = inst.state();
                List<Player> players = new ArrayList<>(base.getProfile().keySet());

                // Entscheidung: exakt oder MultiStart
                int S = players.get(0).getStrategies().size();
                double profiles = Math.pow(S, n);

                Set<GameState> equilibria;

                if (profiles <= maxProfilesForExactSearch) {
                    NashChecker.Mode nm = (evalMode == EvalMode.COST)
                            ? NashChecker.Mode.COST
                            : NashChecker.Mode.UTILITY;
                    equilibria = new AllEquilibriaSearch(players, new NashChecker(nm)).findAll();
                } else {
                    // Fallback: MultiStart + Potential (nur wenn groß)
                    PotentialCalculator.Mode pm = (evalMode == EvalMode.COST)
                            ? PotentialCalculator.Mode.COST
                            : PotentialCalculator.Mode.UTILITY;
                    MultiStartEquilibriumSearch search =
                            new MultiStartEquilibriumSearch(players, new PotentialCalculator(pm));
                    equilibria = search.search(iterationsPerGame);
                }

                if (equilibria.isEmpty()) {
                    System.out.println("\n--- Spiel #" + g + ": keine Gleichgewichte gefunden ---");
                    continue;
                }

                // best/worst unter den gefundenen NE bzgl SocialObjective
                SocialObjectiveCalculator.Mode sm = (evalMode == EvalMode.COST)
                        ? SocialObjectiveCalculator.Mode.COST
                        : SocialObjectiveCalculator.Mode.UTILITY;

                SocialObjectiveCalculator soc = new SocialObjectiveCalculator(sm);

                double best, worst;
                GameState bestS = null, worstS = null;

                if (evalMode == EvalMode.COST) {
                    best = Double.POSITIVE_INFINITY;
                    worst = Double.NEGATIVE_INFINITY;
                    for (GameState eq : equilibria) {
                        double v = soc.compute(eq);
                        if (v < best) { best = v; bestS = eq; }
                        if (v > worst) { worst = v; worstS = eq; }
                    }
                } else {
                    best = Double.NEGATIVE_INFINITY;
                    worst = Double.POSITIVE_INFINITY;
                    for (GameState eq : equilibria) {
                        double v = soc.compute(eq);
                        if (v > best) { best = v; bestS = eq; }
                        if (v < worst) { worst = v; worstS = eq; }
                    }
                }

                // PoA (wie “gewohnt”: schlechtestes / bestes), aber Utility braucht vorsicht
                Double poa = computePoA(best, worst);

                System.out.println("\n--- Zufälliges Spiel #" + g + " ---");
                System.out.println("NE gefunden: " + equilibria.size());
                System.out.printf("best=%.4f | worst=%.4f%n", best, worst);

                if (poa == null) {
                    // bei Utility kann Quotient unsinnig sein -> Gap ausgeben
                    System.out.printf("PoA-Ratio nicht definiert (Werte <= 0). GAP=%.4f%n", (best - worst));
                } else {
                    System.out.printf("PoA=%.6f%n", poa);
                }

                System.out.println("Resources:");
                inst.resources().forEach(r -> System.out.println("  " + r));

                // globales Maximum (wenn Ratio definiert, sonst über GAP)
                double scoreForMax = (poa != null) ? poa : (best - worst);

                if (scoreForMax > globalMaxPoA + 1e-9) {
                    globalMaxPoA = scoreForMax;
                    globalMaxCount = 1;
                    bestInst = inst;
                    bestValue = best;
                    worstValue = worst;
                    bestState = bestS;
                    worstState = worstS;
                } else if (Math.abs(scoreForMax - globalMaxPoA) <= 1e-9) {
                    globalMaxCount++;
                }
            }

            // Summary pro Spielerzahl
            System.out.println("\n=== SUMMARY (MAX) ===");
            if (bestInst == null) {
                System.out.println("Kein gültiges Spiel gefunden.");
            } else {
                System.out.printf("Max-Score = %.6f (gefunden %d mal)%n", globalMaxPoA, globalMaxCount);
                System.out.printf("Best = %.4f%n", bestValue);
                System.out.printf("Worst = %.4f%n", worstValue);

                System.out.println("Resources:");
                bestInst.resources().forEach(r -> System.out.println("  " + r));

                System.out.println("=== Bestes Gleichgewicht ===");
                bestState.getProfile().forEach((p, s) -> System.out.println(p.getName() + ": " + s.getName()));

                System.out.println("=== Schlechtestes Gleichgewicht ===");
                worstState.getProfile().forEach((p, s) -> System.out.println(p.getName() + ": " + s.getName()));
            }
        }
    }

    private Double computePoA(double best, double worst) {
        // COST: PoA = worst/best, braucht best>0
        // UTILITY: “gewohnt” wäre best/worst, braucht worst>0 und best>0
        // Wenn nicht erfüllt: null => wir reporten GAP.
        if (evalMode == EvalMode.COST) {
            if (best <= 0) return null;
            return worst / best;
        } else {
            if (best <= 0 || worst <= 0) return null;
            return best / worst;
        }
    }
}
