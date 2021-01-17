package Service;

public class Location {

    private int x;
    private int y;
    private int numInfect;
    private String distrito;



    public Location(int x,int y,int numInfetados,String loc){
        this.x = x;
        this.y = y;
        this.distrito = loc;
        this.numInfect = numInfetados;
    }

    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    public String getDistrito() {
        return distrito;
    }

    public int getNumInfect() {
        return numInfect;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(distrito).append(" - ").append(x).append(",").append(y).append(" - ").append(numInfect);
        return super.toString();
    }
}
