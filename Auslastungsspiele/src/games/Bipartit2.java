package games;


import java.util.List;

public class Bipartit2 implements GameTemplate {

    @Override public String name() { return "Bipartit2"; }

    @Override
    public List<String> resourceNames() {
        return List.of("1", "2", "3", "4", "5","6");
    }

    @Override
    public List<String> strategyNames() {
        return List.of(
                "1","2","3","4","5","6", "13","15","16", "24","25","26", "35", "46", "135","246");
    }

    @Override
    public List<List<String>> strategyResourceLists() {
        return List.of(
                List.of("1"),
                List.of("2"),
                List.of("3"),
                List.of("4"),
                List.of("5"),
                List.of("6"),
                List.of("1","3"),
                List.of("1","5"),
                List.of("1","6"),
                List.of("2","4"),
                List.of("2","5"),
                List.of("2","6"),
                List.of("3","5"),
                List.of("4","6"),
                List.of("1","3","5"),
                List.of("2","4","6")
        );
    } }

