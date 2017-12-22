import java.io.*;

public class PlayMatch {

    //per sapere come procede la partita posso parsificare la risposta System.err di playMatch e vedere i turni, chi vince, se vanno in timed out

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
    private static String playGameMsg = "";

    private static MatchState matchState;

    private static String command(String playGameJarFileName, String mapFileName, int timeLimit, int numTurns, String logFileName,
                                  String myBotJarFileName, String opponentBotJarFileName, String showGameJarFileName){
        String command = "java -jar " + playGamePath + playGameJarFileName + " "
                + mapsPath + mapFileName
                + " " + String.valueOf(timeLimit)
                + " " + String.valueOf(numTurns)
                + " " + logPath + logFileName
                + " \"java -jar " + myBotPath + myBotJarFileName + "\""
                + " \"java -jar " + opponentBotsPath + opponentBotJarFileName + "\"";

        //command += (" | java -jar " + showGamePath + showGameJarFileName; //da aggiungere per visualizzare la partita

        return command;
    }

    public static MatchState play(String mapFileName, int timeLimit, int numTurns, String logFileName, String myBotFileName, String opponentBotFileName, double[] botParams){
        String botParameterString = " ";

        //se ci sono parametri da passare al bot li aggiungo ala stringa apposita
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

        matchState = new MatchState(nextCommand, myBotFileName, opponentBotFileName, MatchState.DRAW, 0, false, false);

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"bash","-c", nextCommand});

            readProcessMessages(process);

            //keep playmatch waiting until the end of process
            while(process.isAlive()){}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matchState;
    }

    private static void readProcessMessages(Process process){
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
                        //Log(className, "isThread", line1);
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
                        //Log(className, "errisThread", line1);
                        updateMatchResultFromErrLine(line1);
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

    //parsifica i messaggi che scrivo con il mio bot per sapere lo stato della partita
    private static void updateMatchResultFromErrLine(String message){
        if (message.contains("Turn")) matchState.increaseTurn();
        else if (message.contains("Player 1 Wins!")) matchState.setResult(MatchState.PLAYER_1_WINS);
        else if (message.contains("Player 2 Wins!")) matchState.setResult(MatchState.PLAYER_2_WINS);
        else if (message.contains("WARNING: player 1 timed out")) matchState.setPlayer1TimedOut();
        else if (message.contains("WARNING: player 2 timed out")) matchState.setPlayer2TimedOut();
        else if (message.contains("Number of Planets Player 1 ")) matchState.setP1Planets(Integer.valueOf(message.substring(message.indexOf("Number of Planets Player 1 ") + 27)));
        else if (message.contains("Number of Planets Player 2 ")) matchState.setP2Planets(Integer.valueOf(message.substring(message.indexOf("Number of Planets Player 2 ") + 27)));
        else if (message.contains("Ships on Player 1 Planets ")) matchState.setP1ShipsOnPlanets(Integer.valueOf(message.substring(message.indexOf("Ships on Player 1 Planets ") + 26)));
        else if (message.contains("Ships on Player 2 Planets ")) matchState.setP2ShipsOnPlanets(Integer.valueOf(message.substring(message.indexOf("Ships on Player 2 Planets ") + 26)));
        else if (message.contains("Ships on Player 1 Fleets ")) matchState.setP1ShipsOnFleets(Integer.valueOf(message.substring(message.indexOf("Ships on Player 1 Fleets ") + 25)));
        else if (message.contains("Ships on Player 2 Fleets ")) matchState.setP2ShipsOnFleets(Integer.valueOf(message.substring(message.indexOf("Ships on Player 2 Fleets ") + 25)));
    }
}
