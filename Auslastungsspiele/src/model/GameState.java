package model;

import java.util.*;

public class GameState {

    private final List<Player> players;
    private final Map<Player, Strategy> currentStrategies;

    public GameState(List<Player> players, Map<Player, Strategy> initial) {
        this.players = List.copyOf(players);
        this.currentStrategies = new HashMap<>();

        if (initial != null) {
            this.currentStrategies.putAll(initial);
        }

        // Force initialization: every player has a strategy
        for (Player p : players) {
            if (!currentStrategies.containsKey(p) || currentStrategies.get(p) == null) {
                currentStrategies.put(p, p.getStrategies().get(0));
            }
        }
    }

    public Strategy getStrategy(Player p) {
        return currentStrategies.get(p);
    }

    public void applyStrategy(Player p, Strategy s) {
        if (p == null || s == null) {
            throw new IllegalArgumentException("Player and Strategy must not be null.");
        }
        currentStrategies.put(p, s);
    }

    public Map<Player, Strategy> getProfile() {
        return Collections.unmodifiableMap(currentStrategies);
    }

    public GameState copy() {
        return new GameState(players, currentStrategies);
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameState other)) return false;
        return currentStrategies.equals(other.currentStrategies);
    }

    @Override
    public int hashCode() {
        return currentStrategies.hashCode();
    }
}
