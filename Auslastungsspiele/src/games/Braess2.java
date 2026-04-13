package games;

import java.util.*;

public class Braess2 implements GameTemplate {

    @Override public String name() { return "Braess2"; }

    @Override
    public List<String> resourceNames() {
        return List.of("A", "B", "C", "D");
    }

    @Override
    public List<String> strategyNames() {
        return List.of("AD", "B", "C");
    }

    @Override
    public List<List<String>> strategyResourceLists() {
        return List.of(
                List.of("A", "D"),
                List.of("B"),
                List.of("C"));
    }
}
