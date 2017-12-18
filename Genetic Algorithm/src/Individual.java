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
    public Match[] matches;
    public int fitness;
    final Random rand = new Random();

    // Creates an individual as an array of 'size' params and array of (number of opponents * number of number of maps) matches to calculate the fitness
    public Individual(String[] opponents, String[] maps, int sizeGenotype, double lowerBoundGene, double upperBoundGene) {
        this.genotype = new double[sizeGenotype];
        this.opponents = opponents;
        this.maps = maps;
        this.nMatches = opponents.length * maps.length;
        matches = new Match[this.nMatches];

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
        int fitness = 0;
        //ToDO la fitness deve basarsi sui benchmark che devono essere eseguiti prima di valutare la fitness
        this.fitness = fitness;
    }

    //run one benchmark
    private Match runOneMatch(){
        return null;
    }

    // Comparison method (Overrided): Compares the fitness of the individuals
    @Override
    public int compareTo(Individual o) {
        int f1 = this.fitness;
        int f2 = o.fitness;

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
        return "gene=" + gene() + " fit=" + fitness;
    }
}