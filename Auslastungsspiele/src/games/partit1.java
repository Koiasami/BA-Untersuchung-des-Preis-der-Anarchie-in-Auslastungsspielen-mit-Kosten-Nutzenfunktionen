package games;


import java.util.List;

public class partit1 implements GameTemplate {

    @Override public String name() { return "partit1"; }

    @Override
    public List<String> resourceNames() {
        return List.of("1", "2", "3", "4", "5","6");
    }

    @Override
    public List<String> strategyNames() {
        return List.of("1", "2", "3", "4", "5","6","15", "24", "26", "35", "46","246" );
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
                List.of("1","5"),
                List.of("2","4"),
                List.of("2","6"),
                List.of("3","5"),
                List.of("4","6"),
                List.of("2","4","6"));
    } }

