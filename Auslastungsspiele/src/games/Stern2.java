package games;

import java.util.*;

public class Stern2 implements GameTemplate {

    @Override public String name() { return "Stern2"; }

    @Override
    public List<String> resourceNames() {
        return List.of("M", "1");
    }

    @Override
    public List<String> strategyNames() {
        return List.of("M", "1");
    }

    @Override
    public List<List<String>> strategyResourceLists() {
        return List.of(
                List.of("M"),
                List.of("1"));
    }
}