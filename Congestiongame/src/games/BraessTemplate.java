package games;

import java.util.*;

public class BraessTemplate implements GameTemplate {

    @Override public String name() { return "Braess"; }

    @Override
    public List<String> resourceNames() {
        return List.of("A->B", "A->C", "B->D", "C->D", "B->C");
    }

    @Override
    public List<List<String>> strategyResourceLists() {
        return List.of(
                List.of("A->B", "B->D"),              // ABD
                List.of("A->C", "C->D"),              // ACD
                List.of("A->B", "B->C", "C->D")       // ABCD
        );
    }

    @Override
    public List<String> strategyNames() {
        return List.of("ABD", "ACD", "ABCD");
    }
}
