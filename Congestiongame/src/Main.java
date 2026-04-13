import engine.ExperimentRunner;
import games.*;
import java.io.*;
import java.util.*;

public class Main {

    private static final Random rnd = new Random();
    // private static final Random rnd = new Random(42);

    static int GAMES_PER_PLAYERCOUNT = 20;

    public static void main(String[] args) throws Exception {

        PrintStream console = System.out;
        PrintStream file = new PrintStream(new FileOutputStream("result.txt"));
        System.setOut(new TeePrintStream(console, file));

        // 1) Wähle Template: Braess / Pigou / später weitere
        GameTemplate template = new BraessTemplate();
        // GameTemplate template = new PigouTemplate();

        // 2) Parameterintervalle je Ressource (hier: einheitlich – du kannst pro Ressource variieren!)
        Map<String, ParamRange> ranges = new HashMap<>();
        for (String rn : template.resourceNames()) {
            ranges.put(rn, new ParamRange(
                    0.0, 5.0,    // x
                    -2.0, 2.0,   // p
                    0.0, 50.0    // y
            ));
        }

        RandomGameFactory factory = new RandomGameFactory(template, ranges);

        // 3) Runner: COST oder UTILITY
       /// ExperimentRunner runnerCost = new ExperimentRunner(
          //      factory,
            //    GAMES_PER_PLAYERCOUNT,
              //  ExperimentRunner.EvalMode.COST,
               // 200_000,  // maxProfilesForExactSearch (z.B. 3^10=59049 -> ok)
                //500       // iterationsPerGame (fallback MultiStart)
        //);

        ExperimentRunner runnerUtil = new ExperimentRunner(
                factory,
                GAMES_PER_PLAYERCOUNT,
                ExperimentRunner.EvalMode.UTILITY,
                200_000,
                500
        );

        System.out.println("=== RANDOM GAMES | TEMPLATE=" + template.name() + " ===");

        // Beispiel: Spieler 2..6
        //runnerCost.run(2, 6, rnd);
        runnerUtil.run(2, 6, rnd);

        file.close();
        System.setOut(console);
        System.out.println("\nFertig. Ergebnisse stehen in result.txt");
    }

    // ======================================================
    // TEE PRINT STREAM (deins)
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
        @Override public PrintStream printf(String f, Object... o) { a.printf(f, o); b.printf(f, o); return this; }
    }
}
