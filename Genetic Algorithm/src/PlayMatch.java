import java.io.*;
import java.util.Random;

public class PlayMatch {
    private static String className = "PlayMatch";

    private static String mainDirectory = "/home/gaetano/Documenti/PlanetWars";
    private static String root = mainDirectory + "/";
    private static String playGamePath = root + "tools/";
    private static String mapsPath = root + "maps/";
    private static String logPath = root + "";
    private static String myBotPath = root + "my_bots/";
    private static String opponentBotsPath = root + "bots/";
    private static String showGamePath = root + "tools/";

    private static String playGameJar = "PlayGame-1.2.jar";
    private static String showGameJar = "ShowGame-1.2.jar";

    private static boolean playEnded = false;
    private static Match matchResult = null;

    private static String command(String playGameJarFileName, String mapFileName, int timeLimit, int numTurns, String logFileName,
                                  String myBotJarFileName, String opponentBotJarFileName, String showGameJarFileName){
        String command = "java -jar " + playGamePath + playGameJarFileName + " "
                + mapsPath + mapFileName
                + " " + String.valueOf(timeLimit)
                + " " + String.valueOf(numTurns)
                + " " + logPath + logFileName
                + " \"java -jar " + myBotPath + myBotJarFileName + "\""
                + " \"java -jar " + opponentBotsPath + opponentBotJarFileName + "\"";

        //command += (" | java -jar " + showGamePath + showGameJarFileName;

        return command;
    }

    public static Match play(String mapFileName, int timeLimit, int numTurns, String logFileName, String myBotFileName, String opponentBotFileName, double[] botParams){
        Match result = null;

        String botParameterString = " ";

        if (botParams != null){
            StringBuilder sb = new StringBuilder();
            sb.append(" ");
            for (double botParameter : botParams) {
                sb.append(botParameter);
                sb.append(" ");
            }
            botParameterString = sb.toString();
        }

        String myBotFileNameWithParams = myBotFileName + botParameterString;

        String nextCommand = command(playGameJar, mapFileName, timeLimit, numTurns, logFileName, myBotFileNameWithParams, opponentBotFileName, showGameJar);
        //String nextCommand = command(playGameJar, "map100.txt", 1000, 1000, "log.txt", "EvaBot.jar", "ExGenebot.jar", showGameJar);

        //Log(className, "main", "comando: " + nextCommand);

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"bash","-c", nextCommand});

            printMessages(process);

            //keep playmatch waiting until the end of process
            while(process.isAlive()){}
            //read result from .txt
            result = new Match("EvaBot_Log.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static void printMessages(Process process){
        InputStream inputStream = process.getInputStream();
        InputStream inputStreamError = process.getErrorStream();

        Thread inputStreamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                try {
                    while ((line = bufferedReader.readLine()) != null){
                        String line1 = line;
                        Log(className, "isThread", line1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        inputStreamThread.start();

        Thread inputStreamErrorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStreamReader inputStreamErrorReader = new InputStreamReader(inputStreamError);
                BufferedReader bufferedReader = new BufferedReader(inputStreamErrorReader);
                String line;
                try {
                    while((line = bufferedReader.readLine()) != null){
                        String line1 = line;
                        Log(className, "errisThread", line1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        inputStreamErrorThread.start();
    }

    private static void Log(String classCaller, String functionCaller, String message) {
        System.out.println("(" + classCaller + ")" + " b:y " + functionCaller + " : " + message);
    }
}
