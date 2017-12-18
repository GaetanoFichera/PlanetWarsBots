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

    public static void main(String[] args) {
        play(null);

        Match matchResult = new Match("EvaBot_Log.txt");

        Log(className, "main", matchResult.toString());
    }

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

    public static Match play(double[] botParams){
        Match result = null;

        if (botParams != null){
            StringBuilder sb = new StringBuilder();
            for (double botParameter : botParams) {
                sb.append(botParameter);
                sb.append(" ");
            }
            sb.append("\" ");
            String botParameterString = sb.toString();
        }

        String nextCommand = command(playGameJar, "map100.txt", 1000, 1000,
                "log.txt", "EvaBot.jar", "ExGenebot.jar", showGameJar);

        Log(className, "main", "comando: " + nextCommand);

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"bash","-c", nextCommand});
            /*
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
            */


        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void test(){
        Random mRandom = new Random();
        mRandom.doubles(0,1);

        double MAX_RATIO_SHIPS_FL_AND_PLNS = mRandom.nextDouble();
        double MATCH_STATE_ORDERS_PARAM = mRandom.nextDouble();
        double ATTACKABLE_ENEMY_PARAM = mRandom.nextDouble();
        double ATTACKABLE_STRANGER_PARAM = mRandom.nextDouble();
        double HELP_FRIEND_PARAM = mRandom.nextDouble();
        double NUM_SHIP_DIFF_ENEMIES_PARAM = mRandom.nextDouble();
        double NUM_SHIP_DIFF_FRIENDS_PARAM = mRandom.nextDouble();
        double NUM_SHIP_DIFF_STRANGERS_PARAM = mRandom.nextDouble();
        double DIST_ENEMIES_PARAM = mRandom.nextDouble();
        double DIST_STRANGERS_PARAM = mRandom.nextDouble();
        double DIST_FRIENDS_PARAM = mRandom.nextDouble();
        double DEST_GROW_RATE_PARAM = mRandom.nextDouble();
        double E_FLEET_TO_E_DEST_PARAM = mRandom.nextDouble();
        double E_FLEET_TO_M_DEST_PARAM = mRandom.nextDouble();
        double E_FLEET_TO_S_DEST_PARAM = mRandom.nextDouble();
        double M_FLEET_TO_E_DEST_PARAM = mRandom.nextDouble();
        double M_FLEET_TO_M_DEST_PARAM = mRandom.nextDouble();
        double M_FLEET_TO_S_DEST_PARAM = mRandom.nextDouble();
        double BASE_MOVE_ENEMIES_PARAM = mRandom.nextDouble();
        double BASE_MOVE_STRANGERS_PARAM = mRandom.nextDouble();
        double BASE_MOVE_FRIENDS_PARAM = mRandom.nextDouble();
        double GAP_MOVE_ENEMIES_PARAM = mRandom.nextDouble();
        double GAP_MOVE_STRANGERS_PARAM = mRandom.nextDouble();
        double GAP_MOVE_FRIENDS_PARAM = mRandom.nextDouble();

        double[] botParameters = {
                MAX_RATIO_SHIPS_FL_AND_PLNS,
                MATCH_STATE_ORDERS_PARAM,
                ATTACKABLE_ENEMY_PARAM,
                ATTACKABLE_STRANGER_PARAM,
                HELP_FRIEND_PARAM,
                NUM_SHIP_DIFF_ENEMIES_PARAM,
                NUM_SHIP_DIFF_FRIENDS_PARAM,
                NUM_SHIP_DIFF_STRANGERS_PARAM,
                DIST_ENEMIES_PARAM,
                DIST_STRANGERS_PARAM,
                DIST_FRIENDS_PARAM,
                DEST_GROW_RATE_PARAM,
                E_FLEET_TO_E_DEST_PARAM,
                E_FLEET_TO_M_DEST_PARAM,
                E_FLEET_TO_S_DEST_PARAM,
                M_FLEET_TO_E_DEST_PARAM,
                M_FLEET_TO_M_DEST_PARAM,
                M_FLEET_TO_S_DEST_PARAM,
                BASE_MOVE_ENEMIES_PARAM,
                BASE_MOVE_STRANGERS_PARAM,
                BASE_MOVE_FRIENDS_PARAM,
                GAP_MOVE_ENEMIES_PARAM,
                GAP_MOVE_STRANGERS_PARAM,
                GAP_MOVE_FRIENDS_PARAM
        };

        StringBuilder sb = new StringBuilder();
        for (double botParameter : botParameters) {
            sb.append(botParameter);
            sb.append(" ");
        }
        sb.append("\" ");
        String botParameterString = sb.toString();

        String nextCommand = command(playGameJar, "map100.txt", 1000, 1000,
                "log.txt", "EvaBot.jar", "ExGenebot.jar", showGameJar);

        Log(className, "main", "comando: " + nextCommand);

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"bash","-c", nextCommand});
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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void Log(String classCaller, String functionCaller, String message) {
        System.out.println(classCaller + " inside " + functionCaller + ": " + message);
    }
}
