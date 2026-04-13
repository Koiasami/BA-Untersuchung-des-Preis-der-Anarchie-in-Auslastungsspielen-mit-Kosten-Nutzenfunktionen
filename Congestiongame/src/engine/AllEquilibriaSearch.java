package engine;

import model.*;

import java.util.*;

public class AllEquilibriaSearch {

    private final List<Player> players;
    private final NashChecker checker;

    public AllEquilibriaSearch(List<Player> players, NashChecker checker) {
        this.players = players;
        this.checker = checker;
    }

    public Set<GameState> findAll() {
        Set<GameState> eq = new HashSet<>();
        backtrack(0, new HashMap<>(), eq);
        return eq;
    }

    private void backtrack(int idx, Map<Player, Strategy> cur, Set<GameState> out) {
        if (idx == players.size()) {
            GameState s = new GameState(players, cur);
            if (checker.isNash(s)) out.add(s.copy());
            return;
        }
        Player p = players.get(idx);
        for (Strategy st : p.getStrategies()) {
            cur.put(p, st);
            backtrack(idx + 1, cur, out);
        }
    }
}
