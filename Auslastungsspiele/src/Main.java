import engine.*;
import games.*;

import java.io.*;
import java.util.*;

public class Main {



    // Seed optional (für reproduzierbare Ergebnisse: new Random(42))
    private static final Random rnd = new Random();

    // ============================
    // EXPERIMENT-KONFIG
    // ============================
    static int MIN_PLAYERS = 2;
    static int MAX_PLAYERS = 6;

    // wie viele zufällige Spiele pro Spieleranzahl
    static int GAMES_PER_PLAYERCOUNT = 10000;

    // Exakte NE-Suche bis S^n <= THRESHOLD, sonst Fallback MultiStart
    static int EXACT_SEARCH_MAX_PROFILES = 200_000;

    // Fallback: wie viele MultiStart-Starts (falls Strategiespace groß)
    static int MULTISTART_ITERATIONS = 750;

    // PoA-Filter: nur Fälle mit PoA >= Threshold werden ausgegeben
    static double MIN_POA_THRESHOLD = 1.5;

    public static void main(String[] args) throws Exception {

        new PrintWriter("summary_players.txt").close();
        new PrintWriter("all_games.csv").close();

        PrintStream console = System.out;
        PrintStream file = new PrintStream(new FileOutputStream("result.txt"));
        System.setOut(new TeePrintStream(console, file));

        // ============================
        // TEMPLATE WÄHLEN
        // ============================
        GameTemplate template = new Bipartit5();
        // GameTemplate template = new PigouTemplate();
        // GameTemplate template = new Braess2(); // dein eigenes Template
       // GameTemplate template = new partit6();

        // ============================
        // PARAMETERBEREICHE PRO RESSOURCE
        // (hier: einheitlich; du kannst pro Ressource unterschiedliche Werte setzen)
        // ============================
        Map<String, ParamRange> ranges = new HashMap<>();
        for (String rn : template.resourceNames()) {
            ranges.put(rn, new ParamRange(
                    0.0, 5.0,   // x
                    -2.0, 2.0,   // p (>=0 -> wächst mit load; kann auch negativ sein)
                    1.0, 50.0   // y (positiver Offset -> positive Werte)
            ));
        }

        RandomGameFactory factory = new RandomGameFactory(template, ranges);

        System.out.println("=== RANDOM CONGESTION GAMES ===");
        System.out.println("Template = " + template.name());
        System.out.println("Players  = " + MIN_PLAYERS + ".." + MAX_PLAYERS);
        System.out.println("Games/N  = " + GAMES_PER_PLAYERCOUNT);
        System.out.println("Min PoA  = " + MIN_POA_THRESHOLD);

        // ============================
        // COST-RUN (minimieren)
        // ============================
       new ExperimentRunner(
              factory,
             ObjectiveMode.COST,
           MIN_PLAYERS,
         MAX_PLAYERS,
        GAMES_PER_PLAYERCOUNT,
       EXACT_SEARCH_MAX_PROFILES,
       MULTISTART_ITERATIONS,
             MIN_POA_THRESHOLD
       ).run(rnd);

        // ============================
        // UTILITY-RUN (maximieren)
        // ============================
       //new ExperimentRunner(
         //     factory,
          //  ObjectiveMode.UTILITY,
          //MIN_PLAYERS,
        //MAX_PLAYERS,
      //GAMES_PER_PLAYERCOUNT,
        //      EXACT_SEARCH_MAX_PROFILES,
          //    MULTISTART_ITERATIONS,
           //   MIN_POA_THRESHOLD
        //).run(rnd);

        file.close();
        System.setOut(console);
        System.out.println("\nDone. Results written to result.txt");
    }

    // ======================================================
    // TEE PRINT STREAM (Konsole + Datei)
    // ======================================================
    static class TeePrintStream extends PrintStream {

        private final PrintStream a;
        private final PrintStream b;

        TeePrintStream(PrintStream a, PrintStream b) {
            super(OutputStream.nullOutputStream());
            this.a = a;
            this.b = b;
        }

        @Override public void println(String x) { a.println(x); b.println(x); }
        @Override public void print(String x) { a.print(x); b.print(x); }

        @Override
        public PrintStream printf(String f, Object... o) {
            a.printf(f, o);
            b.printf(f, o);
            return this;
        }
    }
}
