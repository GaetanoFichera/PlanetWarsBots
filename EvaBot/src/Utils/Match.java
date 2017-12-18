package Utils;

public class Match {
    private String opponent;
    private boolean myWin;
    private int nTurns;
    private int myShips;
    private int oppShips;

    public Match(String opponent, boolean myWin, int nTurns, int myShips, int oppShips) {
        this.opponent = opponent;
        this.myWin = myWin;
        this.nTurns = nTurns;
        this.myShips = myShips;
        this.oppShips = oppShips;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public boolean isMyWin() {
        return myWin;
    }

    public void setMyWin(boolean myWin) {
        this.myWin = myWin;
    }

    public int getnTurns() {
        return nTurns;
    }

    public void setnTurns(int nTurns) {
        this.nTurns = nTurns;
    }

    public int getMyShips() {
        return myShips;
    }

    public void setMyShips(int myShips) {
        this.myShips = myShips;
    }

    public int getOppShips() {
        return oppShips;
    }

    public void setOppShips(int oppShips) {
        this.oppShips = oppShips;
    }

    @Override
    public String toString() {
        return "Match{" +
                "opponent='" + opponent + '\'' +
                ", myWin=" + myWin +
                ", nTurns=" + nTurns +
                ", myShips=" + myShips +
                ", oppShips=" + oppShips +
                '}';
    }
}