package Service;

public class Location {

    private int x;
    private int y;
    private int numPessoas;
    private String distrito;



    public Location(int x,int y,int numPessoas,String loc){
        this.x = x;
        this.y = y;
        this.distrito = loc;
        this.numPessoas = numPessoas;
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

    public int getNumPessoas() {
        return numPessoas;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(distrito).append(" - ").append(x).append(",").append(y).append(" - ").append(numPessoas);
        return super.toString();
    }
}
