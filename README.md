# BA-Untersuchung-des-Preis-der-Anarchie-in-Auslastungsspielen-mit-Kosten-Nutzenfunktionen
Programm Bachelorarbeit: Untersuchung des Preis der Anarchie in Auslastungsspielen mit Kosten/Nutzenfunktionen

Hinweise zur Ausfuehrung:


Graphen: es gibt 19 verschiedene Graphen-Klassen zur Auswahl. src\games 

Main: In der Main sucht man einen aus, Bsp.: GameTemplate template = new Bipartit5();

Dann kommentiert man aus/ein je nachdem, ob man Cost oder Utility berechnen moechte und startet. Bei bedarf kann man noch Spieleranzahl/thresholds/Anzahl Spiele anpassen. Jeweils in der Main kommentiert. 


// ============================
        // COST-RUN (minimieren)
        // ============================
       new ExperimentRunner(
              factory,
             ObjectiveMode.COST,
           MIN_PLAYERS,
         MAX_PLAYERS,
        GAMES_PER_PLAYERCOUNT,
       EXACT_SEARCH_MAX_PROFILES,
       MULTISTART_ITERATIONS,
             MIN_POA_THRESHOLD
       ).run(rnd);

        // ============================
        // UTILITY-RUN (maximieren)
        // ============================
       //new ExperimentRunner(
         //     factory,
          //  ObjectiveMode.UTILITY,
          //MIN_PLAYERS,
        //MAX_PLAYERS,
      //GAMES_PER_PLAYERCOUNT,
        //      EXACT_SEARCH_MAX_PROFILES,
          //    MULTISTART_ITERATIONS,
           //   MIN_POA_THRESHOLD
        //).run(rnd);
