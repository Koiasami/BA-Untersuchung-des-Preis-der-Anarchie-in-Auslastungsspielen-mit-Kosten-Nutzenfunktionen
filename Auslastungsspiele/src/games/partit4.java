package games;

import java.util.List;

public class partit4 implements GameTemplate {

    @Override public String name() { return "partit4"; }

    @Override
    public List<String> resourceNames() {
        return List.of("1", "2", "3", "4", "5","6");
    }

    @Override
    public List<String> strategyNames() {
        return List.of("1", "2", "3", "4", "5","6","15", "46" );
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
            //    List.of("1","5"),
                List.of("4","6"));
    } }