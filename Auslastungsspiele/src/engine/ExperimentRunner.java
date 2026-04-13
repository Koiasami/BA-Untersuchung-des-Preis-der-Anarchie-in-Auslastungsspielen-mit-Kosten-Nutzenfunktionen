package engine;

import games.RandomGameFactory;
import model.*;

import java.io.*;
import java.util.*;

public class ExperimentRunner {

    public record Result(
            int players,
            int gameIndex,
            int equilibriaCount,
            Double poa,
            double bestValue,
            double worstValue,
            GameState bestEq,
            GameState worstEq,
            List<RandomizedResource> resources
    ) {}

    private static final double EPS = 1e-9;

    private final RandomGameFactory factory;
    private final ObjectiveMode mode;

    private final int gamesPerPlayerCount;
    private final int minPlayers;
    private final int maxPlayers;

    private final int exactSearchMaxProfiles;
    private final int fallbackMultiStartIterations;

    // Filter only for console detail-printing (summary ignores threshold)
    private final double minPoAThreshold;

    // Output files
    private final File summaryFile = new File("summary_players.txt"); // summaries only
    private final File csvFile = new File("all_games.csv");          // all games (raw data)

    public ExperimentRunner(RandomGameFactory factory,
                            ObjectiveMode mode,
                            int minPlayers,
                            int maxPlayers,
                            int gamesPerPlayerCount,
                            int exactSearchMaxProfiles,
                            int fallbackMultiStartIterations,
                            double minPoAThreshold) {
        this.factory = factory;
        this.mode = mode;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.gamesPerPlayerCount = gamesPerPlayerCount;
        this.exactSearchMaxProfiles = exactSearchMaxProfiles;
        this.fallbackMultiStartIterations = fallbackMultiStartIterations;
        this.minPoAThreshold = minPoAThreshold;
    }

    public void run(Random rnd) {

        SocialObjectiveCalculator soc = new SocialObjectiveCalculator();

        appendSummaryHeader();
        appendCsvHeaderIfNeeded();

        for (int n = minPlayers; n <= maxPlayers; n++) {

            // ---------------------------
            // TIMING START (per player count)
            // ---------------------------
            long nStart = System.nanoTime();

            System.out.println("\n=======================================");
            System.out.println("TEMPLATE = " + factory.getTemplateName());
            System.out.println("MODE     = " + mode);
            System.out.println("PLAYERS  = " + n);
            System.out.println("PRINT PoA>= " + minPoAThreshold + " (summary ignores threshold)");
            System.out.println("=======================================");

            // Summary trackers (threshold ignored)
            double bestOverallPoA = Double.NEGATIVE_INFINITY;
            int bestOverallCount = 0;
            Result bestOverall = null;

            for (int g = 1; g <= gamesPerPlayerCount; g++) {

                RandomGameFactory.Instance inst = factory.sample(n, rnd);
                GameState base = inst.state();
                List<Player> players = base.getPlayers();

                int strategiesPerPlayer = players.get(0).getStrategies().size();
                double profiles = Math.pow(strategiesPerPlayer, n);

                Set<GameState> equilibria;

                // exact NE search if feasible
                if (profiles <= exactSearchMaxProfiles) {
                    equilibria = new AllEquilibriaSearch(players, new NashChecker(mode)).findAll();
                } else {
                    equilibria = new MultiStartEquilibriumSearch(
                            players, mode, new PotentialCalculator(), rnd
                    ).search(fallbackMultiStartIterations);
                }

                if (equilibria.isEmpty()) {
                    // still write a CSV row? we skip (no PoA)
                    continue;
                }

                // pick best/worst equilibrium by social objective
                double best = (mode == ObjectiveMode.COST)
                        ? Double.POSITIVE_INFINITY
                        : Double.NEGATIVE_INFINITY;

                double worst = (mode == ObjectiveMode.COST)
                        ? Double.NEGATIVE_INFINITY
                        : Double.POSITIVE_INFINITY;

                GameState bestS = null;
                GameState worstS = null;

                for (GameState eq : equilibria) {
                    double v = soc.compute(eq);

                    if (mode == ObjectiveMode.COST) {
                        if (v < best) { best = v; bestS = eq; }
                        if (v > worst) { worst = v; worstS = eq; }
                    } else {
                        if (v > best) { best = v; bestS = eq; }
                        if (v < worst) { worst = v; worstS = eq; }
                    }
                }

                Double poa = computePoA(best, worst);
                if (poa == null) {
                    // still write a CSV row without PoA? optional. We'll skip.
                    continue;
                }

                // Track best overall PoA (threshold ignored)
                if (poa > bestOverallPoA + EPS) {
                    bestOverallPoA = poa;
                    bestOverallCount = 1;
                    bestOverall = new Result(n, g, equilibria.size(), poa, best, worst, bestS, worstS, inst.resources());
                } else if (Math.abs(poa - bestOverallPoA) <= EPS) {
                    bestOverallCount++;
                }

                // Console: print only if threshold passed
                if (poa >= minPoAThreshold) {
                    System.out.println("\n--- Random Game #" + g + " ---");
                    System.out.println("NE found: " + equilibria.size());
                    System.out.printf("best=%.6f | worst=%.6f%n", best, worst);
                    System.out.printf("PoA=%.6f%n", poa);
                    System.out.println("Best strategy counts:  " + strategyCounts(bestS));
                    System.out.println("Worst strategy counts: " + strategyCounts(worstS));
                }

                // CSV: store ALL games with PoA defined
                appendCsvRow(inst, n, g, equilibria.size(), best, worst, poa, bestS, worstS);
            }

            // ===== SUMMARY PRINT (always, threshold ignored) =====
            System.out.println("\n=== SUMMARY (MAX PoA PER PLAYER COUNT) ===");

            if (bestOverall == null) {
                System.out.println("No equilibria / no defined PoA found.");
                appendNoDataToSummaryFile(n);

                // ---------------------------
                // TIMING END (even if no data)
                // ---------------------------
                long nEnd = System.nanoTime();
                double nSeconds = (nEnd - nStart) / 1_000_000_000.0;

                // Console/result.txt
                System.out.printf(Locale.US,
                        "TIMING: N=%d | total=%.3f s | avg=%.3f ms/game (games=%d)%n",
                        n, nSeconds, (nSeconds * 1000.0) / gamesPerPlayerCount, gamesPerPlayerCount);

                // Summary file (ALWAYS)
                appendTimingToSummaryFile(n, nSeconds, gamesPerPlayerCount);

                continue;
            }

            System.out.printf("Max PoA = %.6f (found %d times)%n", bestOverall.poa(), bestOverallCount);
            System.out.printf("Best = %.6f%n", bestOverall.bestValue());
            System.out.printf("Worst = %.6f%n", bestOverall.worstValue());

            System.out.println("Resources:");
            for (RandomizedResource r : bestOverall.resources()) {
                System.out.println("  " + r);
            }

            System.out.println("=== Strategy counts (BestEq) ===");
            System.out.println(strategyCounts(bestOverall.bestEq()));

            System.out.println("=== Strategy counts (WorstEq) ===");
            System.out.println(strategyCounts(bestOverall.worstEq()));

            System.out.println("=== Resource loads (BestEq) ===");
            System.out.println(resourceLoads(bestOverall.bestEq()));

            System.out.println("=== Resource loads (WorstEq) ===");
            System.out.println(resourceLoads(bestOverall.worstEq()));

            System.out.println("=== Best Equilibrium (profile) ===");
            System.out.println(profileToString(bestOverall.bestEq()));

            System.out.println("=== Worst Equilibrium (profile) ===");
            System.out.println(profileToString(bestOverall.worstEq()));

            // TXT: store ONLY summaries
            appendSummaryBlockToFile(bestOverall, bestOverallCount);

            // ---------------------------
            // TIMING END (per player count)
            // ---------------------------
            long nEnd = System.nanoTime();
            double nSeconds = (nEnd - nStart) / 1_000_000_000.0;
            double msPerGame = (nSeconds * 1000.0) / gamesPerPlayerCount;

            // Console/result.txt
            System.out.printf(Locale.US,
                    "TIMING: N=%d | total=%.3f s | avg=%.3f ms/game (games=%d)%n",
                    n, nSeconds, msPerGame, gamesPerPlayerCount);

            // Summary file (ALWAYS)
            appendTimingToSummaryFile(n, nSeconds, gamesPerPlayerCount);
        }
    }

    // PoA computation (always >= 1 if defined)
    private Double computePoA(double best, double worst) {
        if (mode == ObjectiveMode.COST) {
            if (best <= 0) return null;
            return worst / best;
        } else {
            if (worst <= 0) return null;
            return best / worst;
        }
    }

    // -------- Strategy counts (how many players chose each strategy) --------
    private String strategyCounts(GameState s) {
        if (s == null) return "";
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Strategy st : s.getProfile().values()) {
            counts.merge(st.getName(), 1, Integer::sum);
        }
        return counts.toString();
    }

    // -------- Resource loads (how many players use each resource) --------
    private String resourceLoads(GameState s) {
        if (s == null) return "";
        Map<String, Integer> loads = new LinkedHashMap<>();
        for (Strategy st : s.getProfile().values()) {
            for (Resource r : st.getResources()) {
                loads.merge(r.getName(), 1, Integer::sum);
            }
        }
        return loads.toString();
    }

    // -------- Profile to string (P0=..., P1=...) --------
    private String profileToString(GameState s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        // stable order (optional): sort by player name
        List<Map.Entry<Player, Strategy>> entries = new ArrayList<>(s.getProfile().entrySet());
        entries.sort(Comparator.comparing(e -> e.getKey().getName()));
        for (var e : entries) {
            sb.append(e.getKey().getName())
                    .append("=")
                    .append(e.getValue().getName())
                    .append(" ");
        }
        return sb.toString().trim();
    }

    // ==========================
    // SUMMARY TXT (summaries only)
    // ==========================
    private void appendSummaryHeader() {
        try (PrintWriter out = new PrintWriter(new FileWriter(summaryFile, true))) {
            out.println("\n===== TEMPLATE = " + factory.getTemplateName() + " | MODE = " + mode + " =====");
        } catch (IOException ignored) {}
    }

    private void appendNoDataToSummaryFile(int players) {
        try (PrintWriter out = new PrintWriter(new FileWriter(summaryFile, true))) {
            out.println("Players=" + players + " | NO VALID EQUILIBRIA / POA");
            out.println("---------------------------------------------------");
        } catch (IOException ignored) {}
    }

    private void appendSummaryBlockToFile(Result r, int count) {
        try (PrintWriter out = new PrintWriter(new FileWriter(summaryFile, true))) {

            out.println("Players=" + r.players()
                    + " | Mode=" + mode
                    + " | Template=" + factory.getTemplateName()
                    + " | MaxPoA=" + String.format(Locale.US, "%.6f", r.poa())
                    + " | Found=" + count);

            out.println("Best=" + String.format(Locale.US, "%.6f", r.bestValue())
                    + " | Worst=" + String.format(Locale.US, "%.6f", r.worstValue()));

            out.println("Resources:");
            for (RandomizedResource res : r.resources()) {
                out.println("  " + res);
            }

            out.println("StrategyCounts BestEq:  " + strategyCounts(r.bestEq()));
            out.println("StrategyCounts WorstEq: " + strategyCounts(r.worstEq()));
            out.println("ResourceLoads  BestEq:  " + resourceLoads(r.bestEq()));
            out.println("ResourceLoads  WorstEq: " + resourceLoads(r.worstEq()));

            out.println("BestEq Profile:  " + profileToString(r.bestEq()));
            out.println("WorstEq Profile: " + profileToString(r.worstEq()));

            out.println("---------------------------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appends per-player-count timing information to the summary file.
     * This is always written (independent of PoA threshold and even if no valid equilibria exist).
     */
    private void appendTimingToSummaryFile(int players, double seconds, int games) {
        try (PrintWriter out = new PrintWriter(new FileWriter(summaryFile, true))) {
            out.printf(Locale.US,
                    "Timing: Players=%d | total=%.3f s | avg=%.3f ms/game (games=%d)%n",
                    players, seconds, (seconds * 1000.0) / games, games);
            out.println("---------------------------------------------------");
        } catch (IOException ignored) {}
    }

    // ==========================
    // CSV OUTPUT (all games)
    // ==========================
    private void appendCsvHeaderIfNeeded() {
        if (csvFile.exists()) return;
        try (PrintWriter out = new PrintWriter(new FileWriter(csvFile))) {
            out.println("template,mode,players,gameIndex,neCount,bestValue,worstValue,poa,resources,bestEq,worstEq,bestStrategyCounts,worstStrategyCounts,bestResourceLoads,worstResourceLoads");
        } catch (IOException ignored) {}
    }

    private void appendCsvRow(RandomGameFactory.Instance inst,
                              int players,
                              int gameIndex,
                              int neCount,
                              double best,
                              double worst,
                              double poa,
                              GameState bestEq,
                              GameState worstEq) {

        try (PrintWriter out = new PrintWriter(new FileWriter(csvFile, true))) {

            String resources = inst.resources().stream()
                    .map(Object::toString)
                    .reduce((a, b) -> a + " | " + b)
                    .orElse("");

            String bestProfile = profileToString(bestEq);
            String worstProfile = profileToString(worstEq);

            String bestCounts = strategyCounts(bestEq);
            String worstCounts = strategyCounts(worstEq);

            String bestLoads = resourceLoads(bestEq);
            String worstLoads = resourceLoads(worstEq);

            out.printf(Locale.US,
                    "%s,%s,%d,%d,%d,%.6f,%.6f,%.6f,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    factory.getTemplateName(),
                    mode,
                    players,
                    gameIndex,
                    neCount,
                    best,
                    worst,
                    poa,
                    resources,
                    bestProfile,
                    worstProfile,
                    bestCounts,
                    worstCounts,
                    bestLoads,
                    worstLoads
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}