import java.util.Random;

/**
 * Gli individui che verranno utilizzati all'interno dell'algoritmo genetico devono raccogliere le informazioni riguardo
 * le esecuzioni del bot su tutte le mappe contro tutti i bot, creando una struttura dati che ricorda per ogni mappa per
 * ogni avversario quanti turni ci ha messo per vincere e con quante navi
 */

public class Individual implements Comparable<Individual> {
    public double[] genotype;
    public String[] opponents;
    public String[] maps;
    public int nMatches;
    public boolean[] matches;
    public double fitness; //for now percentage of victories
    final Random rand = new Random();

    // Creates an individual as an array of 'size' params and array of (number of opponents * number of number of maps) matches to calculate the fitness
    public Individual(String[] opponents, String[] maps, int sizeGenotype, double lowerBoundGene, double upperBoundGene) {
        this.genotype = new double[sizeGenotype];
        this.opponents = opponents;
        this.maps = maps;
        this.nMatches = opponents.length * maps.length;
        matches = new boolean[this.nMatches];

        rand.doubles(lowerBoundGene,upperBoundGene);

        gene();
    }

    // Generates random values for each gene
    void random() {
        for (int i = 0; i < genotype.length; i++) {
            genotype[i] = rand.nextDouble();
        }
    }

    //Append each value of genotype
    private String gene(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genotype.length; i++) {
            sb.append(genotype[i]);
            sb.append(" - ");
        }
        return sb.toString();
    }

    // Computes the fitness value
    void calculateFitness() {
        double fitness = 0;

        //Log(getClass().getName(), "calculateFitness", runOneMatch().toString());

        int matchCounter = 0;

        int runtimeVictories = 0;

        double runtimePercentageVictories = 0.0;

        int averageTurns = 0;

        for (int botCounter = 0; botCounter < opponents.length; botCounter++){
            for (int mapCounter = 0; mapCounter < maps.length; mapCounter++){
                matches[matchCounter] = runOneMatch(maps[mapCounter], "EvaBot.jar", opponents[botCounter]);

                Log(getClass().getName(), "fitness", String.valueOf(matchCounter + 1)
                        + " : " + "opponent: " + opponents[botCounter] + " map: " + maps[mapCounter]
                        + " " + matches[matchCounter]);

                    if (matches[matchCounter]) runtimeVictories++;

                runtimePercentageVictories = ((double) runtimeVictories) / (((double) matchCounter) + 1) * 100;

                //averageTurns += matches[matchCounter].getnTurns();

                Log(getClass().getName(), "fitness", "Average Victories Percentage: " + runtimePercentageVictories + "%"
                + " Partite Fatte: " + (matchCounter + 1) + " / " + nMatches);

                matchCounter++;

            }
        }

        /*
        int victories = 0;
        int averageTurns = 0;

        for (Match m : matches){
            if (m != null)
                if (m.getnTurns() != 0)
                    if (m.isMyWin()) {
                        victories += 1;
                        averageTurns += m.getnTurns();
                    }
        }

        if (victories != 0) averageTurns /= victories;

        fitness = (double) (victories / nMatches);

        this.fitness = fitness;*/

        this.fitness = runtimePercentageVictories;
        //this.fitness = (double) averageTurns / (double) nMatches;
    }

    //run one benchmark
    private boolean runOneMatch(){
        return PlayMatch.play("map1.txt", 1000, 1000, "log.txt", "EvaBot.jar", "ExGenebot.jar", genotype);
    }

    private boolean runOneMatch(String mapFileName, String myBotFileName, String opponentFileName){
        return PlayMatch.play(mapFileName, 1000, 1000, "log.txt", myBotFileName, opponentFileName, genotype);
    }

    // Comparison method (Overrided): Compares the fitness of the individuals
    @Override
    public int compareTo(Individual o) {
        double f1 = this.fitness;
        double f2 = o.fitness;

        if (f1 < f2)
            return 1;
        else if (f1 > f2)
            return -1;
        else
            return 0;
    }

    // Shows an individual as a String
    @Override
    public String toString() {
        return "gene=" + gene() + " fit=" + fitness + "%";
    }

    private static void Log(String classCaller, String functionCaller, String message) {
        System.out.println("(" + classCaller + ")" + " b:y " + functionCaller + " : " + message);
    }
}