package games;

import java.util.*;

public class Stern implements GameTemplate {

    @Override public String name() { return "Stern"; }

    @Override
    public List<String> resourceNames() {
        return List.of("M", "1", "2", "3", "4","5","6");
    }

    @Override
    public List<String> strategyNames() {
        return List.of("M", "123456", "1", "2", "3", "4","5","6");
    }

    @Override
    public List<List<String>> strategyResourceLists() {
        return List.of(
                List.of("M"),
                List.of("1","2","3","4","5","6"),
                List.of("1"),
                List.of("2"),
                List.of("3"),
                List.of("4"),
                List.of("5"),
                List.of("6"));
    }
}
