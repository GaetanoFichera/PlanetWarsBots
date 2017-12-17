import Utils.*;
import java.util.*;

public class Eva {
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

    private static double mMatchState = 0.5;
    private static int mTurn = 0;
    private static ArrayList<Integer> mLastSourcesOrder = new ArrayList<>(); //memorizza gli ultimi X pianeti che hanno ceduto navi
    private static Map<Integer, Integer> myPlanetShips = new HashMap<>(); //to save number of ships on each planet each turn
    private static ArrayList<Integer> mThisTurnSourcesOrder = new ArrayList<>();
    private static int NUM_ORDER_TO_MEM = 20;

    private static final Random mRandom = new Random();

    public static void DoTurn(PlanetWars pw) {
        mThisTurnSourcesOrder = new ArrayList<>();
        mTurn++;
        matchState(pw);

        if (grantPermission(pw))
            if (!executeOrder(pw)) return;
    }

    public static void main(String[] args) {

        randomParams();

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
        }
    }

    //Grant permission to attack if the ratio beetwen Ships on my Fleets and Ships on my Planets are less than a factor
    private static boolean grantPermission(PlanetWars pw){
        if (myShipsOnPlanets(pw) != 0)
            if ((myShipsOnFleets(pw) / myShipsOnPlanets(pw)) <= MAX_RATIO_SHIPS_FL_AND_PLNS) return true;
            else return false;
        else return false;
    }

    private static boolean executeOrder(PlanetWars pw){
        List<Rank> globalRank = globalRankingPlanets(pw);
        if (globalRank.size() != 0) {
            int orders = 1;
            //Numbers of orders depends on match state and a factor
            orders = (int) (orders + (orders * mMatchState / MATCH_STATE_ORDERS_PARAM));

            if (orders > globalRank.size()) orders = globalRank.size();

            for (int i = 0; i < orders; i++){
                issueOrder(pw, globalRank.get(i));
            }
            return true;
        } else return false;
    }

    //Calcola lo stato della partita, ed il double in uscita andrà da 0 a 1, dove 0 si è in stato di sconfitta e 1 in stato di vittoria
    private static void matchState(PlanetWars pw){


        mMatchState = 0.5; //per ora imposta lo stato della partita sempre su draw
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

    //Calcola la classifica locale per ogni pianeta non mio
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
        if (score > 0 && shipsToMove != 0){ //Creo ed Aggiungo il nuovo Rank solo se soddisfa la condizione
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
            if (myFleetsTargetIt != 0) score /= (myFleetsTargetIt * M_FLEET_TO_E_DEST_PARAM);

            int enemyFleetsTargetIt = enemyFleetsToDestPlanet(pw, destination);
            if (enemyFleetsTargetIt != 0) score *= (enemyFleetsTargetIt * E_FLEET_TO_E_DEST_PARAM);

            score *= mMatchState;

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
            if (myFleetsTargetIt != 0) score /= (myFleetsTargetIt * M_FLEET_TO_S_DEST_PARAM);

            int enemyFleetsTargetIt = enemyFleetsToDestPlanet(pw, destination);
            if (enemyFleetsTargetIt != 0) score *= (enemyFleetsTargetIt * E_FLEET_TO_S_DEST_PARAM);

            score *= mMatchState;

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
            if (myFleetsTargetIt != 0) score /= (myFleetsTargetIt * M_FLEET_TO_M_DEST_PARAM);

            int enemyFleetsTargetIt = enemyFleetsToDestPlanet(pw, destination);
            if (enemyFleetsTargetIt != 0) score *= (enemyFleetsTargetIt * E_FLEET_TO_M_DEST_PARAM);

            score *= (1 - mMatchState);
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
        if (checkAlreadyOrder(order)) {
            pw.IssueOrder(order.getmIDSourcePlanet(), order.getmIDDestinationPlanet(), order.getmShipMove());
            saveSourcePlanetOrder(order);
        }
    }

    /*
    Controlla se in questo turno ho gia effettuato uno spostamento da il pianeta da cui mi si chiede di inviare navi:
    in caso affermativo si nega il comando
    in caso negativo si aggiunge alla lista e si concede il comando
     */
    private static boolean checkAlreadyOrder(Rank order){
        if (mThisTurnSourcesOrder.contains(order.getmIDSourcePlanet())) return false;
        else {
            mThisTurnSourcesOrder.add(order.getmIDSourcePlanet());
            return true;
        }
    }

    private static void saveSourcePlanetOrder(Rank Rank){
        mLastSourcesOrder.add(Rank.getmIDSourcePlanet());
        if (mLastSourcesOrder.size() > NUM_ORDER_TO_MEM) mLastSourcesOrder.remove(0);
    }

    private static int myShipsOnFleets(PlanetWars pw){
        int myShipsOnFleets = 0;

        for (Fleet f : pw.MyFleets()){
            myShipsOnFleets += f.NumShips();
        }
        return myShipsOnFleets;
    }

    private static int myShipsOnPlanets(PlanetWars pw){
        int myShipsOnPlanet = 0;

        for (Planet p : pw.MyPlanets()){
            myShipsOnPlanet += p.NumShips();
        }
        return myShipsOnPlanet;
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

    private static void Log(String classCaller, String functionCaller, String message){
        System.err.println(classCaller + " inside " + functionCaller + ": " + message);
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
}