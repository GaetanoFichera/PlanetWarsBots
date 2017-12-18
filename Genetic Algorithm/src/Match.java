import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Match {
    private String opponent;
    private boolean myWin;
    private int nTurns;
    private int myShips;
    private int oppShips;

    public Match(){
        opponent = null;
        myWin = false;
        nTurns = 0;
        myShips = 0;
        oppShips = 0;
    }

    public Match(String opponent, boolean myWin, int nTurns, int myShips, int oppShips) {
        this.opponent = opponent;
        this.myWin = myWin;
        this.nTurns = nTurns;
        this.myShips = myShips;
        this.oppShips = oppShips;
    }
    
    public Match(String filePath){
        Match matchFromFile = fromFile(filePath);
        
        this.opponent = matchFromFile.opponent;
        this.myWin = matchFromFile.myWin;
        this.nTurns = matchFromFile.nTurns;
        this.myShips = matchFromFile.myShips;
        this.oppShips = matchFromFile.oppShips;
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
    
    private Match fromFile(String filePath){
        Match matchFromFile = new Match();

        String read = "";

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            read = everything;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String prefix = "Match{";
        String suffix = "}";
        String split = ", ";

        if (read.length() > 5){
            String noPrefixStr = read.substring(read.indexOf(prefix) + prefix.length());
            String noSuffixPrefixStr = noPrefixStr.substring(0, noPrefixStr.lastIndexOf(suffix));
            String[] tokens = noSuffixPrefixStr.split(split);

            String[] tokenValues = new String[tokens.length];

            int i = 0;
            for (String s : tokens){
                tokenValues[i] = s.substring(s.lastIndexOf("=") + 1, s.length());
                i++;
            }

            matchFromFile.setOpponent(null);
            if (tokenValues[1].equals("false")) matchFromFile.setMyWin(false);
            else matchFromFile.setMyWin(true);
            matchFromFile.setnTurns(Integer.valueOf(tokenValues[2]));
            matchFromFile.setMyShips(Integer.valueOf(tokenValues[3]));
            matchFromFile.setOppShips(Integer.valueOf(tokenValues[4]));
        }

        return matchFromFile;
    } 
}