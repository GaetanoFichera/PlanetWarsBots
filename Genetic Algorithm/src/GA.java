import com.google.gson.Gson;

import java.io.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class GA {
    private static final int POPULATION = 1;

    private static final int REPETITION_FOR_MAP = 10;
    private static final int MAPS_SIZE = 1;

    private static Individual theBest = null;

    public static void main(String[] args) {
        Instant startInstant, startOneIndividual, endOneIndividual;
        long durationOneIndividual;
        long durationPreviousOneIndividual = 0;
        long remainingTime;

        String[] opponents = {
                //"BullyBot.jar",
                //"DualBot.jar",
                "ExGenebot.jar",
                "Genebot.jar",
                //"ProspectorBot.jar",
                //"RageBot.jar",
                //"RandomBot.jar",
                // ZerlingRush.jar",
                //"SwarmBot.jar"
        };

        String[] maps = new String[MAPS_SIZE * REPETITION_FOR_MAP];

        int repetitionCounter = 0;
        int mapNameCounter = 0;
        int i = 0;
        while (i < maps.length) {
            if (repetitionCounter < REPETITION_FOR_MAP){
                //maps[i] = "map" + String.valueOf(mapNameCounter + 1) + ".txt";
                maps[i] = "map100.txt";
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

        startInstant = Instant.now();

        for (int j = 0; j < POPULATION; j++){
            startOneIndividual = Instant.now();

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

            updateTheBest(one);

            endOneIndividual = Instant.now();

            durationOneIndividual = ChronoUnit.SECONDS.between(startOneIndividual,endOneIndividual);

            if (j == 0) durationPreviousOneIndividual = durationOneIndividual;

            remainingTime = (durationPreviousOneIndividual + durationOneIndividual) / 2 * (POPULATION - (j + 1));

            durationPreviousOneIndividual = durationOneIndividual;

            printOnFile("Remaining_Time.txt", String.valueOf(remainingTime));

            Log("GA", "LF the Best", "Remaining Time: " + String.valueOf(remainingTime) + " secs");
        }

        //aggiungere modo per salvare su di un file solo il migliore

        /*
        Individual theFirst = new Individual(opponents, maps, sizeParams, lB, uB);

        theFirst.random();
        theFirst.calculateFitness();
        theFirst.toString();
        */
    }

    public static void updateTheBest(Individual individual){
        String theBestString = "";
        if (theBest != null)
        {
            if (individual.fitness > theBest.fitness) {
                theBest = individual;
                Gson gson = new Gson();
                theBestString = gson.toJson(theBest);
                printOnFile("Gene_1_The_Best_Eva", theBestString);
            }
        }else {
            theBest = individual;
            Gson gson = new Gson();
            theBestString = gson.toJson(theBest);
            printOnFile("Gene_1_The_Best_Eva", theBestString);
        }


    }

    private static void printOnFile(String fileName, String toPrint){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "utf-8"))) {
            writer.write(toPrint);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void Log(String classCaller, String functionCaller, String message) {
        System.out.println("(" + classCaller + ")" + " b:y " + functionCaller + " : " + message);
    }
}
