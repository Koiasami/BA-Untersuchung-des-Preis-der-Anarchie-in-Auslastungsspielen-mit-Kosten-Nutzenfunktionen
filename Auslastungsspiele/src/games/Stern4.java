package games;

import java.util.*;

public class Stern4 implements GameTemplate {

    @Override public String name() { return "Stern4"; }

    @Override
    public List<String> resourceNames() {
        return List.of("M", "1", "2", "3");
    }

    @Override
    public List<String> strategyNames() {
        return List.of("M", "123", "1", "2", "3");
    }

    @Override
    public List<List<String>> strategyResourceLists() {
        return List.of(
                List.of("M"),
                List.of("1","2","3"),
                List.of("1"),
                List.of("2"),
                List.of("3"));
    }
}