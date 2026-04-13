package games;

import java.util.*;

public class Stern3 implements GameTemplate {

    @Override public String name() { return "Stern3"; }

    @Override
    public List<String> resourceNames() {
        return List.of("M", "1", "2");
    }

    @Override
    public List<String> strategyNames() {
        return List.of("M", "12", "1", "2");
    }

    @Override
    public List<List<String>> strategyResourceLists() {
        return List.of(
                List.of("M"),
                List.of("1","2"),
                List.of("1"),
                List.of("2"));
    }
}