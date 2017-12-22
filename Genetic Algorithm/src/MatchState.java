public class MatchState {
    //Valori dell'attributo result
    public static final int DRAW = 0;
    public static final int PLAYER_1_WINS = 1;
    public static final int PLAYER_2_WINS = 2;

    private static final String DRAW_MSG = "Draw!";
    private static final String PLAYER_1_WINS_MSG = "Player 1 Wins!";
    private static final String PLAYER_2_WINS_MSG = "Player 2 Wins!";

    private String shellCommand;
    private String Player1;
    private String Player2;
    private int result;
    private int nTurns;
    private boolean player1TimedOut;
    private boolean player2TimedOut;
    private int p1Planets;
    private int p2Planets;
    private int p1ShipsOnPlanets;
    private int p2ShipsOnPlanets;
    private int p1ShipsOnFleets;
    private int p2ShipsOnFleets;

    public MatchState(String shellCommand, String player1, String player2, int result, int nTurns, boolean player1TimedOut, boolean player2TimedOut) {
        this.shellCommand = shellCommand;
        Player1 = player1;
        Player2 = player2;
        this.result = result;
        this.nTurns = nTurns;
        this.player1TimedOut = player1TimedOut;
        this.player2TimedOut = player2TimedOut;
        p1Planets = 0;
        p2Planets = 0;
        p1ShipsOnPlanets = 0;
        p2ShipsOnPlanets = 0;
        p1ShipsOnFleets = 0;
        p2ShipsOnFleets = 0;
    }

    public String getShellCommand() {
        return shellCommand;
    }

    public String getPlayer1() {
        return Player1;
    }

    public String getPlayer2() {
        return Player2;
    }

    public boolean isPlayer1TimedOut() {
        return player1TimedOut;
    }

    public boolean isPlayer2TimedOut() {
        return player2TimedOut;
    }

    public int getResult(){
        return this.result;
    }

    public String getResultAsString(){
        if (this.result == PLAYER_1_WINS) return PLAYER_1_WINS_MSG;
        else if (this.result == PLAYER_2_WINS) return PLAYER_2_WINS_MSG;
        else return DRAW_MSG;
    }

    public int getnTurns() {
        return nTurns;
    }

    public void increaseTurn(){
        this.nTurns++;
    }

    public void setPlayer1TimedOut() {
        this.player1TimedOut = true;
    }

    public void setPlayer2TimedOut() {
        this.player2TimedOut = true;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getP1Planets() {
        return p1Planets;
    }

    public void setP1Planets(int p1Planets) {
        this.p1Planets = p1Planets;
    }

    public int getP2Planets() {
        return p2Planets;
    }

    public void setP2Planets(int p2Planets) {
        this.p2Planets = p2Planets;
    }

    public int getP1ShipsOnPlanets() {
        return p1ShipsOnPlanets;
    }

    public void setP1ShipsOnPlanets(int p1ShipsOnPlanets) {
        this.p1ShipsOnPlanets = p1ShipsOnPlanets;
    }

    public int getP2ShipsOnPlanets() {
        return p2ShipsOnPlanets;
    }

    public void setP2ShipsOnPlanets(int p2ShipsOnPlanets) {
        this.p2ShipsOnPlanets = p2ShipsOnPlanets;
    }

    public int getP1ShipsOnFleets() {
        return p1ShipsOnFleets;
    }

    public void setP1ShipsOnFleets(int p1ShipsOnFleets) {
        this.p1ShipsOnFleets = p1ShipsOnFleets;
    }

    public int getP2ShipsOnFleets() {
        return p2ShipsOnFleets;
    }

    public void setP2ShipsOnFleets(int p2ShipsOnFleets) {
        this.p2ShipsOnFleets = p2ShipsOnFleets;
    }
}
