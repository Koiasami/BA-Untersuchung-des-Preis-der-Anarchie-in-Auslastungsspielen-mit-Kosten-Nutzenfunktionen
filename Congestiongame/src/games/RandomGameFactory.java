package games;

import model.*;

import java.util.*;

public class RandomGameFactory {

    private final GameTemplate template;
    private final Map<String, ParamRange> ranges;

    public RandomGameFactory(GameTemplate template, Map<String, ParamRange> ranges) {
        this.template = template;
        this.ranges = ranges;
    }

    public record Instance(GameState state, List<RandomizedResource> resources) {}

    public Instance sample(int players, Random rnd) {

        // Ressourcen instanziieren
        Map<String, RandomizedResource> res = new HashMap<>();
        for (String rn : template.resourceNames()) {
            ParamRange pr = ranges.get(rn);
            if (pr == null) throw new IllegalArgumentException("No range for resource: " + rn);

            double x = pr.xMin() + rnd.nextDouble() * (pr.xMax() - pr.xMin());
            double p = pr.pMin() + rnd.nextDouble() * (pr.pMax() - pr.pMin());
            double y = pr.yMin() + rnd.nextDouble() * (pr.yMax() - pr.yMin());

            res.put(rn, new RandomizedResource(rn, x, p, y));
        }

        // Strategien bauen
        List<Strategy> strategies = new ArrayList<>();
        List<List<String>> lists = template.strategyResourceLists();
        List<String> snames = template.strategyNames();

        for (int i = 0; i < lists.size(); i++) {
            List<Resource> sr = new ArrayList<>();
            for (String rn : lists.get(i)) sr.add(res.get(rn));
            strategies.add(new Strategy(snames.get(i), sr));
        }

        // Spieler bauen
        List<Player> ps = new ArrayList<>();
        for (int i = 0; i < players; i++) {
            ps.add(new Player("P" + i, strategies));
        }

        return new Instance(new GameState(ps, null), new ArrayList<>(res.values()));
    }
}
