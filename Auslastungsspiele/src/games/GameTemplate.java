package games;

import java.util.*;

public interface GameTemplate {
    String name();
    List<String> resourceNames();
    List<String> strategyNames();

    /** Strategy i = list of resource names used by that strategy */
    List<List<String>> strategyResourceLists();
}
