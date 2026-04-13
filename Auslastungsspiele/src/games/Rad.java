package games;


import java.util.*;

public class Rad implements GameTemplate {

    @Override public String name() { return "Rad"; }

    @Override
    public List<String> resourceNames() {
        return List.of("M", "1", "2", "3", "4","5");
    }

    @Override
    public List<String> strategyNames() {
        return List.of("M", "1", "2", "3", "4","5", "13","24","25", "35", "14");
    }

    @Override
    public List<List<String>> strategyResourceLists() {
        return List.of(
                List.of("M"),
                List.of("1"),
                List.of("2"),
                List.of("3"),
                List.of("4"),
                List.of("5"),
                List.of("1","3"),
                List.of("2","4"),
                List.of("2","5"),
                List.of("3","5"),
                List.of("1","4"));
    }
}
