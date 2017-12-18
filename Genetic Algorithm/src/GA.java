public class GA {
    public static void main(String[] args) {
        String[] opponents = {
                "ExGeneBot"
        };

        String[] maps = {
                "map1.txt"
        };
        int sizeParams = 24;
        int lB = 0;
        int uB = 1;
        Individual theFirst = new Individual(opponents, maps, sizeParams, lB, uB);

        theFirst.random();
        theFirst.calculateFitness();
        
    }
}
