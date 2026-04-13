package games;

import java.util.*;

public class Stern5 implements GameTemplate {

    @Override public String name() { return "Stern5"; }

    @Override
    public List<String> resourceNames() {
        return List.of("M", "1", "2", "3", "4");
    }

    @Override
    public List<String> strategyNames() {
        return List.of("M", "1234", "1", "2", "3", "4");
    }

    @Override
    public List<List<String>> strategyResourceLists() {
        return List.of(
                List.of("M"),
                List.of("1","2","3","4"),
                List.of("1"),
                List.of("2"),
                List.of("3"),
                List.of("4"));
    }
}