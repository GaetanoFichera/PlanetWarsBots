import java.io.*;

public class GA {
    private static final int POPULATION = 100;

    public static void main(String[] args) {
        String[] opponents = {
                //"BullyBot.jar",
                //"DualBot.jar",
                "ExGenebot.jar",
                "Genebot.jar",
                //"ProspectorBot.jar",
                //"RageBot.jar",
                //"RandomBot.jar"
        };

        int repetitionMap = 5;

        String[] maps = new String[100 * repetitionMap];

        int repetitionCounter = 0;
        int mapNameCounter = 0;
        int i = 0;
        while (i < maps.length) {
            if (repetitionCounter < repetitionMap){
                maps[i] = "map" + String.valueOf(mapNameCounter + 1) + ".txt";
                repetitionCounter++;
                i++;
            }else{
                repetitionCounter = 0;
                mapNameCounter++;
            }
        }

        int sizeParams = 24;

        int lB = 0;
        int uB = 1;

        for (int j = 0; j < POPULATION; j++){
            Individual one = new Individual(opponents, maps, sizeParams, lB, uB);

            one.random();
            one.calculateFitness();

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("Result_Gener_1_Eva_" + String.valueOf(j + 1) + ".txt"), "utf-8"))) {
                writer.write(one.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
        Individual theFirst = new Individual(opponents, maps, sizeParams, lB, uB);

        theFirst.random();
        theFirst.calculateFitness();
        theFirst.toString();
        */
    }
}
