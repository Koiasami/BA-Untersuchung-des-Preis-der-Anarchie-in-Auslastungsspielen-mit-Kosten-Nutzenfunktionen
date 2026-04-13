package games;


import java.util.List;

public class Bipartit5 implements GameTemplate {

    @Override public String name() { return "Bipartit5"; }

    @Override
    public List<String> resourceNames() {
        return List.of("1", "2", "3", "4", "5");
    }

    @Override
    public List<String> strategyNames() {
        return List.of(
                "1", "2", "3", "4", "5", "12", "14", "15", "23", "34", "35", "145", "345");
    }

    @Override
    public List<List<String>> strategyResourceLists() {
        return List.of(
                List.of("1"),
                List.of("2"),
                List.of("3"),
                List.of("4"),
                List.of("5"),
                List.of("1","2"),
                List.of("1","4"),
                List.of("1","5"),
                List.of("2","3"),
                List.of("3","4"),
                List.of("3","5"),
                List.of("1","4","5"),
                List.of("3","4","5"));
    } }

