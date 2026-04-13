package games;

import model.*;

import java.util.*;

public class RandomGameFactory {

    private final GameTemplate template;
    private final Map<String, ParamRange> ranges;

    public RandomGameFactory(GameTemplate template, Map<String, ParamRange> ranges) {
        this.template = template;
        this.ranges = new HashMap<>(ranges);
    }

    public String getTemplateName() {
        return template.name();
    }

    public record Instance(GameState state, List<RandomizedResource> resources) {}

    public Instance sample(int players, Random rnd) {

        Map<String, RandomizedResource> res = new LinkedHashMap<>();
        for (String rn : template.resourceNames()) {
            ParamRange pr = ranges.get(rn);
            if (pr == null) {
                throw new IllegalArgumentException("Missing parameter range for resource: " + rn);
            }

            double x = pr.xMin() + rnd.nextDouble() * (pr.xMax() - pr.xMin());
            double p = pr.pMin() + rnd.nextDouble() * (pr.pMax() - pr.pMin());

            // einfache Skalierung für y:
            double k0 = players;
            double base = x * Math.pow(k0, p);

            double delta = 5.0;               // <- hier kannst du schrauben (z.B. 1, 5, 20, ...)
            double y = (base - delta) + rnd.nextDouble() * (2.0 * delta);

            y = Math.max(0.0, y);             // wenn du nur positive y willst

            res.put(rn, new RandomizedResource(rn, x, p, y));
        }


        List<Strategy> strategies = new ArrayList<>();
        List<String> sNames = template.strategyNames();
        List<List<String>> lists = template.strategyResourceLists();

        for (int i = 0; i < lists.size(); i++) {
            List<Resource> sr = new ArrayList<>();
            for (String rn : lists.get(i)) {
                sr.add(res.get(rn));
            }
            strategies.add(new Strategy(sNames.get(i), sr));
        }

        List<Player> ps = new ArrayList<>();
        for (int i = 0; i < players; i++) {
            ps.add(new Player("P" + i, strategies));
        }

        return new Instance(new GameState(ps, null), new ArrayList<>(res.values()));
    }
}
