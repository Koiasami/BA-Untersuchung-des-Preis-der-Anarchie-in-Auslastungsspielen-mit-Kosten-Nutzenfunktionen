package games;


import java.util.List;

public class Bipartit121 implements GameTemplate {

    @Override public String name() { return "Bipartit121"; }

    @Override
    public List<String> resourceNames() {
        return List.of("1", "2", "3", "4", "5","6");
    }

    @Override
    public List<String> strategyNames() {
        return List.of("1", "2", "3", "4", "5","6", "13","14","15","16","23", "24","25", "26", "35","45", "46", "135","145","146","235" ,"245", "246" );
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
                List.of("1","4"),
                List.of("1","5"),
                List.of("1","6"),
                List.of("2","3"),
                List.of("2","4"),
                List.of("2","5"),
                List.of("2","6"),
                List.of("3","5"),
                List.of("4","5"),
                List.of("4","6"),
                List.of("1","3","5"),
                List.of("1","4","5"),
                List.of("1","4","6"),
                List.of("2", "3","5"),
                List.of("2","4","5"),
                List.of("2","4","6"));
    } }
