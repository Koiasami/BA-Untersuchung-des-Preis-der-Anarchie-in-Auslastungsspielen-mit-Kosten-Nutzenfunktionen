package games;

import java.util.*;

public class PigouTemplate implements GameTemplate {

    @Override public String name() { return "Pigou"; }

    @Override
    public List<String> resourceNames() {
        return List.of("FAST", "SLOW");
    }

    @Override
    public List<List<String>> strategyResourceLists() {
        return List.of(
                List.of("FAST"),
                List.of("SLOW")
        );
    }

    @Override
    public List<String> strategyNames() {
        return List.of("FAST", "SLOW");
    }
}
