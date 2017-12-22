import Utils.*;

import java.io.*;
import java.util.*;

public class EvaBot {
    /*
    1)Per ogni Pianeta mio stilo una classifica contenente tutti i gli altri pianeti verso cui può effettuare uno spostamento
    e calcolo un punteggio indicando un numero di navi da voler spostare

    2)Successivamente unisco le classifiche in una classifica globale

    3)Prendo le prime X scelte in base ad un fattore di scelta stando attento a non rischiare di mandare più navi di quelle disponibili sul pianeta

    Potrei aggiungere di controllare tra le Y migliori scelte e cercare di combinarle bilanciando gli spostamenti
    */

    /*
    Struttura Dati Necessaria

    Campi:
        - ID Pianeta Sorgente
        - ID Pianeta Destinatario
        - Numero Navi da Spostare
        - Punteggio dello Spostamento

    Utilizzo una Map

    Campi:
        -Int Indice
        -Array:
                - ID Pianeta Sorgente
                - ID Pianeta Destinatario
                - Numero Navi da Spostare
                - Double Punteggio Spostamento
     */
    private static final String botName = "EvaBot";

    //40% di vittoria contro GeneBot e ExGeneBot
    private static final double[] bestParams_old_old = {0.9521199520042053,0.3214532808467474,0.1565592129559492,0.18077898247733326,0.6537232108980625,0.8870415460852961,0.13125768785265113,0.8160122473687168,0.12936076765406113,0.19843723281603487,0.6582209821059629,0.9650583791001727,0.34812759947768535,0.632542373991515,0.8395643789652064,0.7139070769653754,0.6765249664915166,0.6832252145927129,0.5684188448747213,0.2032081918919676,0.9454044383655258,0.8046678511013863,0.988047269434463,0.6108964766820721};
    //38% di vittoria contro GeneBot ExGeneBot e ChaoticBot
    private static final double[] bestParams_old = {0.7782558990981306,0.4246729385020427,0.9891739770043135,0.4433861351644306,0.18516344164927445,0.37783884804629086,0.5731259098965399,0.32496718408498193,0.14204370708548542,0.684878297154384,0.3575957224521551,0.3959540698958577,0.5316323799189452,0.5134221182365685,0.1262585333434697,0.8616727457434327,0.6840698438784862,0.2296782144219791,0.7203491143577774,0.5328547284777451,0.2785410576943925,0.45644672955775734,0.3365043597480648,0.7634858311006439};
    //ha vinto contro ExGeneBot e GeneBot sulle mappe 1,7,77 con entrambi 2-1
    private static final double[] bestParams = {0.7615783157280458,0.5272650506137831,0.8612774028004647,0.7472544771517531,0.5445537944185589,0.28415663098345534,0.1787979390356792,0.0546440098278137,0.8420267351936596,0.30104062583814506,0.5645271998866632,0.07763631005401994,0.4720316672161372,0.9121261886395438,0.98081523314667,0.9338261684033725,0.794658703876194,0.5668073317217562,0.7544047890376645,0.47828936747304673,0.796955575064134,0.7812007341689419,0.6117390373535242,0.08603809234852255};

    //Friendship Value
    private static final int WE_ARE_ENEMIES = 0;
    private static final int WE_ARE_STRANGERS = 1;
    private static final int WE_ARE_FRIENDS = 2;

    private static double MAX_RATIO_SHIPS_FL_AND_PLNS = 0; //rapporto limite tra navi sulle flotte e navi sui pianeti (Ex. con 0.25 ho che il limite è flotte 1/4 di navi su pianeta)

    private static double MATCH_STATE_ORDERS_PARAM = 0;

    //Behavoiur Parameters
    private static double ATTACKABLE_ENEMY_PARAM = 0; //Valore Massimo 1 - Rapporto minimo tra navi pianeta nemico e navi mio pianeta (Ex. da 1 in giu vuol dire che io ho più navi)
    private static double ATTACKABLE_STRANGER_PARAM = 0;
    private static double HELP_FRIEND_PARAM = 0;

    //Score Weights                                                                      E = Enemy S = Stranger M = Mine
    private static double NUM_SHIP_DIFF_ENEMIES_PARAM = 0;
    private static double NUM_SHIP_DIFF_FRIENDS_PARAM = 0;
    private static double NUM_SHIP_DIFF_STRANGERS_PARAM = 0;

    private static double DIST_ENEMIES_PARAM = 0;
    private static double DIST_STRANGERS_PARAM = 0;
    private static double DIST_FRIENDS_PARAM = 0;

    private static double DEST_GROW_RATE_PARAM = 0;

    private static double E_FLEET_TO_E_DEST_PARAM = 0; //Peso il numero di navi nemiche dirette su un pianeta nemico
    private static double E_FLEET_TO_M_DEST_PARAM = 0;
    private static double E_FLEET_TO_S_DEST_PARAM = 0;
    private static double M_FLEET_TO_E_DEST_PARAM = 0;
    private static double M_FLEET_TO_M_DEST_PARAM = 0;
    private static double M_FLEET_TO_S_DEST_PARAM = 0;

    private static double BASE_MOVE_ENEMIES_PARAM = 0;
    private static double BASE_MOVE_STRANGERS_PARAM = 0;
    private static double BASE_MOVE_FRIENDS_PARAM = 0;

    private static double GAP_MOVE_ENEMIES_PARAM = 0;
    private static double GAP_MOVE_STRANGERS_PARAM = 0;
    private static double GAP_MOVE_FRIENDS_PARAM = 0;

    private static double mMatchBalance = 0.5;
    private static int mTurn = 0;
    private static ArrayList<Integer> mLastSourcesOrder = new ArrayList<>(); //memorizza gli ultimi X pianeti che hanno ceduto navi
    private static Map<Integer, Integer> myPlanetShips = new HashMap<>(); //to save number of ships on each planet each turn
    private static ArrayList<Integer> mThisTurnSourcesOrder = new ArrayList<>();
    private static int NUM_ORDER_TO_MEM = 10;

    private static final Random mRandom = new Random();
    private static final Random mRandomInt = new Random();

    public static void DoTurn(PlanetWars pw) {
        try {
            //Empty list of last turn orders
            mThisTurnSourcesOrder = new ArrayList<>();

            //keep number of turns
            mTurn++;

            updateMatchBalance(pw);

            //check if is possibile do a move
            if (grantPermission(pw))
                //if yes execute order
                if (!executeOrder(pw)); //Log(botName, "DoTurn", "Order Done!");

        } catch (Exception e){
            //e.printStackTrace();
            Log(botName, "DoTurn - Error", e.getMessage());
        }
    }

    public static void main(String[] args) {
        //randomParams();
        //setParamsByStringArray(args);

        //mRandomInt.ints(2, 6);
        setBestParams();

        String line = "";
        String message = "";
        int c;
        try {
            while ((c = System.in.read()) >= 0) {
                switch (c) {
                    case '\n':
                        if (line.equals("go")) {
                            PlanetWars pw = new PlanetWars(message);
                            DoTurn(pw);
                            pw.FinishTurn();

                            printMatchState(pw);

                            message = "";
                        } else {
                            message += line + "\n";
                        }
                        line = "";
                        break;
                    default:
                        line += (char)c;
                        break;
                }
            }
        } catch (Exception e) {
            // Owned.
            Log(botName, "main - Error", e.getMessage());
        }
    }

    //Grant permission to attack if my and enemy ships are more than zero and the ratio beetwen Ships on my Fleets and Ships on my Planets are less than a factor

    /**
     * Grand permission to move ships if my ships and enemy ships are more than zero and the ratio between my ships on
     * fleets and my ships on planets are less than MAX_RATIO_SHIPS_FL_AND_PLNS
     * @param pw
     * @return
     */
    private static boolean grantPermission(PlanetWars pw){
        if (myShipsOnPlanets(pw) != 0 && enemyShipsOnPlanets(pw) != 0)
            if ((myShipsOnFleets(pw) / myShipsOnPlanets(pw)) <= MAX_RATIO_SHIPS_FL_AND_PLNS) return true;
            else return false;
        else return false;
    }

    private static boolean executeOrder(PlanetWars pw){
        //create a global rank
        List<Rank> globalRank = globalRankingPlanets(pw);
        
        //continue if the global rank has at least one rank
        if (globalRank.size() != 0) {
            int orders = 1;
            //Numbers of orders depends on match state and a factor
            orders = (int) (orders + (orders * mMatchBalance / MATCH_STATE_ORDERS_PARAM));
            //size of global rank is tha upperbound for orders
            if (orders >= globalRank.size()) orders = globalRank.size();
            //execute orders
            for (int i = 0; i < orders; i++){
                issueOrder(pw, globalRank.get(i));
            }
            return true;
        } else return false;
    }

    //Calcola lo stato della partita, ed il double in uscita andrà da 0 a 1, dove 0 si è in stato di sconfitta e 1 in stato di vittoria
    private static void updateMatchBalance(PlanetWars pw){
        int myShipsOnPlanets = myShipsOnPlanets(pw);
        int myShipsOnFleets = myShipsOnFleets(pw);
        int myPlanets = pw.MyPlanets().size();
        int enemyShipsOnPlanets = enemyShipsOnPlanets(pw);
        int enemyShipsOnFleets = enemyShipsOnFleets(pw);
        int enemyPlanets = pw.EnemyPlanets().size();

        int myShips = myShipsOnFleets + myShipsOnPlanets;
        int enemyShips = enemyShipsOnFleets + enemyShipsOnPlanets;

        int myScore = myShips / myPlanets;
        int enemyScore = enemyShips / enemyPlanets;

        int totalScore = myScore + enemyScore;

        mMatchBalance = (double) myScore / (double) totalScore;
        //Log(botName, "matchBalance", String.valueOf(mMatchBalance));

        //mMatchBalance = 0.5;
    }

    //Calcola la classifica globale
    private static List<Rank> globalRankingPlanets(PlanetWars pw){
        List<Rank> globalRanking = new ArrayList<>();

        for (Planet p : pw.MyPlanets()){
            localRankingPlanets(pw, p, globalRanking);
        }

        sortList(globalRanking);
        return globalRanking;
    }

    //Calcola la classifica locale per ogni pianeta
    private static void localRankingPlanets(PlanetWars pw, Planet planet, List<Rank> ranking) {

        for (Planet p : pw.EnemyPlanets()) Rank(pw, planet, p, WE_ARE_ENEMIES, ranking);
        for (Planet p : pw.NeutralPlanets()) Rank(pw, planet, p, WE_ARE_STRANGERS, ranking);
        for (Planet p : pw.MyPlanets()){
            if (p.PlanetID() != planet.PlanetID()) Rank(pw, planet, p, WE_ARE_FRIENDS, ranking);
        }
    }

    //Calcola lo spostamento ed il punteggio per un singolo pianeta
    private static void Rank(PlanetWars pw, Planet source, Planet destination, int friendship, List<Rank> ranking){
        double score = 0;
        int shipsToMove = 0;

        if(friendship == WE_ARE_ENEMIES) {
            score = enemiesScore(pw, source, destination);
            if(score != 0) shipsToMove = enemiesMove(pw, source, destination);
        }
        else if (friendship == WE_ARE_STRANGERS){
            score = strangersScore(pw, source, destination);
            if(score != 0) shipsToMove = strangersMove(pw, source, destination);

        }
        else if (friendship == WE_ARE_FRIENDS){
            score = friendsScore(pw, source, destination);
            if(score != 0) shipsToMove = friendsMove(pw, source, destination);
        }
        if (score > 0 && shipsToMove > 0){ //Creo ed Aggiungo il nuovo Rank solo se soddisfa la condizione
            ranking.add(new Rank(source.PlanetID(), destination.PlanetID(), shipsToMove, score));
        }
    }

    //Calcolo il numero di navi da spostare da un mio pianeta ad un altro
    private static int enemiesMove(PlanetWars pw, Planet source, Planet destination){
        return move(pw, source, destination, BASE_MOVE_ENEMIES_PARAM, GAP_MOVE_ENEMIES_PARAM);
    }

    private static int strangersMove(PlanetWars pw, Planet source, Planet destination){
        return move(pw, source, destination, BASE_MOVE_STRANGERS_PARAM, GAP_MOVE_STRANGERS_PARAM);
    }

    private static int friendsMove(PlanetWars pw, Planet source, Planet destination){
        return move(pw, source, destination, BASE_MOVE_FRIENDS_PARAM, GAP_MOVE_FRIENDS_PARAM);
    }

    private static int move(PlanetWars pw, Planet source, Planet destination, double baseMoveWeight, double gapMoveWeight){
        int shipsMove = 0;

        int baseShipsMove = 0;
        baseShipsMove = (int) (destination.NumShips() * baseMoveWeight);

        int gapShipsMove = 0;
        gapShipsMove = (int) ((source.NumShips() - destination.NumShips()) * gapMoveWeight);

        shipsMove = baseShipsMove + gapShipsMove;

        return shipsMove;
    }

    //Calcola il punteggio di un pianeta destinatario rispetto ad un pianeta sorgente
    private static double enemiesScore(PlanetWars pw, Planet source, Planet destination){
        double score = 0;

        if (destination.NumShips() / source.NumShips() <= ATTACKABLE_ENEMY_PARAM){

            score = ((source.NumShips() - destination.NumShips())                   * NUM_SHIP_DIFF_ENEMIES_PARAM)
                    / ((pw.Distance(source.PlanetID(), destination.PlanetID()))     * DIST_ENEMIES_PARAM)
                    * (destination.GrowthRate()                                     * DEST_GROW_RATE_PARAM);

            int myFleetsTargetIt = myFleetsToDestPlanet(pw, destination);
            if (myFleetsTargetIt > 0) score /= (myFleetsTargetIt * M_FLEET_TO_E_DEST_PARAM);

            int enemyFleetsTargetIt = enemyFleetsToDestPlanet(pw, destination);
            if (enemyFleetsTargetIt > 0) score *= (enemyFleetsTargetIt * E_FLEET_TO_E_DEST_PARAM);

            score *= mMatchBalance;
        }

        return score;
    }

    private static double strangersScore(PlanetWars pw, Planet source, Planet destination){
        double score = 0;

        if (destination.NumShips() / source.NumShips() <= ATTACKABLE_STRANGER_PARAM){

            score = ((source.NumShips() - destination.NumShips())                   * NUM_SHIP_DIFF_STRANGERS_PARAM)
                    / ((pw.Distance(source.PlanetID(), destination.PlanetID()))     * DIST_STRANGERS_PARAM)
                    * (destination.GrowthRate()                                     * DEST_GROW_RATE_PARAM);

            int myFleetsTargetIt = myFleetsToDestPlanet(pw, destination);
            if (myFleetsTargetIt > 0) score /= (myFleetsTargetIt * M_FLEET_TO_S_DEST_PARAM);

            int enemyFleetsTargetIt = enemyFleetsToDestPlanet(pw, destination);
            if (enemyFleetsTargetIt > 0) score *= (enemyFleetsTargetIt * E_FLEET_TO_S_DEST_PARAM);

            score *= mMatchBalance;
        }

        return score;
    }

    private static double friendsScore(PlanetWars pw, Planet source, Planet destination){
        double score = 0;

        if (destination.NumShips() / source.NumShips() <= HELP_FRIEND_PARAM){

            score = ((source.NumShips() - destination.NumShips())                   * NUM_SHIP_DIFF_FRIENDS_PARAM)
                    / ((pw.Distance(source.PlanetID(), destination.PlanetID()))     * DIST_FRIENDS_PARAM)
                    * (destination.GrowthRate()                                     * DEST_GROW_RATE_PARAM);

            int myFleetsTargetIt = myFleetsToDestPlanet(pw, destination);
            if (myFleetsTargetIt > 0) score /= (myFleetsTargetIt * M_FLEET_TO_M_DEST_PARAM);

            int enemyFleetsTargetIt = enemyFleetsToDestPlanet(pw, destination);
            if (enemyFleetsTargetIt > 0) score *= (enemyFleetsTargetIt * E_FLEET_TO_M_DEST_PARAM);

            score *= (1 - mMatchBalance);
        }

        return score;
    }

    //Calcola il numero totale di flotte dirette ad un pianeta
    private static int myFleetsToDestPlanet(PlanetWars pw, Planet destination){
        int myFleetToDestPlanet = 0;

        for (Fleet f : pw.MyFleets()){
            if (f.DestinationPlanet() == destination.PlanetID()) myFleetToDestPlanet += f.NumShips();
        }

        return myFleetToDestPlanet;
    }

    private static int enemyFleetsToDestPlanet(PlanetWars pw, Planet destination){
        int enemyfleetToDestPlanet = 0;

        for (Fleet f : pw.EnemyFleets()){
            if (f.DestinationPlanet() == destination.PlanetID()) enemyfleetToDestPlanet += f.NumShips();
        }

        return enemyfleetToDestPlanet;
    }

    private static void issueOrder(PlanetWars pw, Rank order){
        if (!checkAlreadyOrder(order)) {
            pw.IssueOrder(order.getmIDSourcePlanet(), order.getmIDDestinationPlanet(), order.getmShipMove());
        }
    }

    /*
    Controlla se in questo turno ho gia effettuato uno spostamento da il pianeta da cui mi si chiede di inviare navi:
    in caso affermativo si nega il comando
    in caso negativo si aggiunge alla lista e si concede il comando
     */
    private static boolean checkAlreadyOrder(Rank order){
        if (mThisTurnSourcesOrder.contains(order.getmIDSourcePlanet())) return true;
        else {
            mThisTurnSourcesOrder.add(order.getmIDSourcePlanet());
            saveSourcePlanetOrder(order);
            return false;
        }
    }

    private static void saveSourcePlanetOrder(Rank Rank){
        mLastSourcesOrder.add(Rank.getmIDSourcePlanet());
        if (mLastSourcesOrder.size() > NUM_ORDER_TO_MEM) mLastSourcesOrder.remove(0);
    }

    private static int myShipsOnPlanets(PlanetWars pw){
        int myShipsOnPlanet = 0;

        for (Planet p : pw.MyPlanets()){
            myShipsOnPlanet += p.NumShips();
        }
        return myShipsOnPlanet;
    }

    private static int myShipsOnFleets(PlanetWars pw){
        int myShipsOnFleets = 0;

        for (Fleet f : pw.MyFleets()){
            myShipsOnFleets += f.NumShips();
        }
        return myShipsOnFleets;
    }

    private static int enemyShipsOnPlanets(PlanetWars pw){
        int enemyShipsOnPlanet = 0;

        for (Planet p : pw.EnemyPlanets()){
            enemyShipsOnPlanet += p.NumShips();
        }
        return enemyShipsOnPlanet;
    }

    private static int enemyShipsOnFleets(PlanetWars pw){
        int enemyShipsOnFleets = 0;

        for (Fleet f : pw.EnemyFleets()){
            enemyShipsOnFleets += f.NumShips();
        }
        return enemyShipsOnFleets;
    }

    //Ordina un HashMap
    public static <K, V extends Comparable<? super V>> Map<K, V> sortHashMapByValues(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private static void sortList(List<Rank> list){
        Collections.sort(list, new Comparator<Rank>() {
            @Override
            public int compare(Rank lhs, Rank rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return lhs.getmScore() > rhs.getmScore() ? -1 : (lhs.getmScore() < rhs.getmScore()) ? 1 : 0;
            }
        });
    }

    //scrive in system err informazioni sulla partita che Play Match può parsificare ed acquisire
    private static void printMatchState(PlanetWars pw) {
        Log(botName, "MatchState", "Number of Planets Player 1 " + pw.MyPlanets().size());
        Log(botName, "MatchState", "Ships on Player 1 Planets " + myShipsOnPlanets(pw));
        Log(botName, "MatchState", "Ships on Player 1 Fleets " + myShipsOnFleets(pw));
        Log(botName, "MatchState", "Number of Planets Player 2 " + pw.EnemyPlanets().size());
        Log(botName, "MatchState", "Ships on Player 2 Planets " + enemyShipsOnPlanets(pw));
        Log(botName, "MatchState", "Ships on Player 2 Fleets " + enemyShipsOnFleets(pw));
    }

    private static void Log(String classCaller, String functionCaller, String message){
        System.err.println("(" + classCaller + ")" + " b:y " + functionCaller + " : " + message);
    }

    private static void randomParams(){
        mRandom.doubles(0,1);

        MAX_RATIO_SHIPS_FL_AND_PLNS = mRandom.nextDouble();
        MATCH_STATE_ORDERS_PARAM = mRandom.nextDouble();
        ATTACKABLE_ENEMY_PARAM = mRandom.nextDouble();
        ATTACKABLE_STRANGER_PARAM = mRandom.nextDouble();
        HELP_FRIEND_PARAM = mRandom.nextDouble();
        NUM_SHIP_DIFF_ENEMIES_PARAM = mRandom.nextDouble();
        NUM_SHIP_DIFF_FRIENDS_PARAM = mRandom.nextDouble();
        NUM_SHIP_DIFF_STRANGERS_PARAM = mRandom.nextDouble();
        DIST_ENEMIES_PARAM = mRandom.nextDouble();
        DIST_STRANGERS_PARAM = mRandom.nextDouble();
        DIST_FRIENDS_PARAM = mRandom.nextDouble();
        DEST_GROW_RATE_PARAM = mRandom.nextDouble();
        E_FLEET_TO_E_DEST_PARAM = mRandom.nextDouble();
        E_FLEET_TO_M_DEST_PARAM = mRandom.nextDouble();
        E_FLEET_TO_S_DEST_PARAM = mRandom.nextDouble();
        M_FLEET_TO_E_DEST_PARAM = mRandom.nextDouble();
        M_FLEET_TO_M_DEST_PARAM = mRandom.nextDouble();
        M_FLEET_TO_S_DEST_PARAM = mRandom.nextDouble();
        BASE_MOVE_ENEMIES_PARAM = mRandom.nextDouble();
        BASE_MOVE_STRANGERS_PARAM = mRandom.nextDouble();
        BASE_MOVE_FRIENDS_PARAM = mRandom.nextDouble();
        GAP_MOVE_ENEMIES_PARAM = mRandom.nextDouble();
        GAP_MOVE_STRANGERS_PARAM = mRandom.nextDouble();
        GAP_MOVE_FRIENDS_PARAM = mRandom.nextDouble();
    }
    
    private static void setParamsByStringArray(String[] botParams){
        double[] doubleBotParams = new double[botParams.length];
        
        for (int i = 0; i < doubleBotParams.length; i++) doubleBotParams[i] = Double.parseDouble(botParams[i]);
        
        MAX_RATIO_SHIPS_FL_AND_PLNS = doubleBotParams[0];
        MATCH_STATE_ORDERS_PARAM = doubleBotParams[1];
        ATTACKABLE_ENEMY_PARAM = doubleBotParams[2];
        ATTACKABLE_STRANGER_PARAM = doubleBotParams[3];
        HELP_FRIEND_PARAM = doubleBotParams[4];
        NUM_SHIP_DIFF_ENEMIES_PARAM = doubleBotParams[5];
        NUM_SHIP_DIFF_FRIENDS_PARAM = doubleBotParams[6];
        NUM_SHIP_DIFF_STRANGERS_PARAM = doubleBotParams[7];
        DIST_ENEMIES_PARAM = doubleBotParams[8];
        DIST_STRANGERS_PARAM = doubleBotParams[9];
        DIST_FRIENDS_PARAM = doubleBotParams[10];
        DEST_GROW_RATE_PARAM = doubleBotParams[11];
        E_FLEET_TO_E_DEST_PARAM = doubleBotParams[12];
        E_FLEET_TO_M_DEST_PARAM = doubleBotParams[13];
        E_FLEET_TO_S_DEST_PARAM = doubleBotParams[14];
        M_FLEET_TO_E_DEST_PARAM = doubleBotParams[15];
        M_FLEET_TO_M_DEST_PARAM = doubleBotParams[16];
        M_FLEET_TO_S_DEST_PARAM = doubleBotParams[17];
        BASE_MOVE_ENEMIES_PARAM = doubleBotParams[18];
        BASE_MOVE_STRANGERS_PARAM = doubleBotParams[19];
        BASE_MOVE_FRIENDS_PARAM = doubleBotParams[20];
        GAP_MOVE_ENEMIES_PARAM = doubleBotParams[21];
        GAP_MOVE_STRANGERS_PARAM = doubleBotParams[22];
        GAP_MOVE_FRIENDS_PARAM = doubleBotParams[23];
    }
    
    private static void setBestParams(){
        MAX_RATIO_SHIPS_FL_AND_PLNS = bestParams[0];
        MATCH_STATE_ORDERS_PARAM = bestParams[1];
        ATTACKABLE_ENEMY_PARAM = bestParams[2];
        ATTACKABLE_STRANGER_PARAM = bestParams[3];
        HELP_FRIEND_PARAM = bestParams[4];
        NUM_SHIP_DIFF_ENEMIES_PARAM = bestParams[5];
        NUM_SHIP_DIFF_FRIENDS_PARAM = bestParams[6];
        NUM_SHIP_DIFF_STRANGERS_PARAM = bestParams[7];
        DIST_ENEMIES_PARAM = bestParams[8];
        DIST_STRANGERS_PARAM = bestParams[9];
        DIST_FRIENDS_PARAM = bestParams[10];
        DEST_GROW_RATE_PARAM = bestParams[11];
        E_FLEET_TO_E_DEST_PARAM = bestParams[12];
        E_FLEET_TO_M_DEST_PARAM = bestParams[13];
        E_FLEET_TO_S_DEST_PARAM = bestParams[14];
        M_FLEET_TO_E_DEST_PARAM = bestParams[15];
        M_FLEET_TO_M_DEST_PARAM = bestParams[16];
        M_FLEET_TO_S_DEST_PARAM = bestParams[17];
        BASE_MOVE_ENEMIES_PARAM = bestParams[18];
        BASE_MOVE_STRANGERS_PARAM = bestParams[19];
        BASE_MOVE_FRIENDS_PARAM = bestParams[20];
        GAP_MOVE_ENEMIES_PARAM = bestParams[21];
        GAP_MOVE_STRANGERS_PARAM = bestParams[22];
        GAP_MOVE_FRIENDS_PARAM = bestParams[23];        
    }
}