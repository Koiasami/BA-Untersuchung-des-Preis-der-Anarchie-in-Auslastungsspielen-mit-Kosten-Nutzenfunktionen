package games;

import java.util.*;

public interface GameTemplate {
    String name();
    List<String> resourceNames();
    List<List<String>> strategyResourceLists(); // Ressourcen-Namen je Strategie
    List<String> strategyNames();
}
