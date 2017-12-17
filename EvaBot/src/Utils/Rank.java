package Utils;

public class Rank implements Comparable<Rank>{
    private Integer mIDSourcePlanet;
    private Integer mIDDestinationPlanet;
    private Integer mShipMove;
    private Double mScore;

    public Rank(Integer mIDSourcePlanet, Integer mIDDestinationPlanet, Integer mShipMove, Double mScore) {
        this.mIDSourcePlanet = mIDSourcePlanet;
        this.mIDDestinationPlanet = mIDDestinationPlanet;
        this.mShipMove = mShipMove;
        this.mScore = mScore;
    }

    public Integer getmIDSourcePlanet() {
        return mIDSourcePlanet;
    }

    public void setmIDSourcePlanet(Integer mIDSourcePlanet) {
        this.mIDSourcePlanet = mIDSourcePlanet;
    }

    public Integer getmIDDestinationPlanet() {
        return mIDDestinationPlanet;
    }

    public void setmIDDestinationPlanet(Integer mIDDestinationPlanet) {
        this.mIDDestinationPlanet = mIDDestinationPlanet;
    }

    public Integer getmShipMove() {
        return mShipMove;
    }

    public void setmShipMove(Integer mShipMove) {
        this.mShipMove = mShipMove;
    }

    public Double getmScore() {
        return mScore;
    }

    public void setmScore(Double mScore) {
        this.mScore = mScore;
    }

    @Override
    public int compareTo(Rank o) {
        return mScore.compareTo(o.mScore);
    }
}
