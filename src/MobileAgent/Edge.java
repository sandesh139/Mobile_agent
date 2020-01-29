package MobileAgent;

/**
 * this class represents end points for edge
 */
public class Edge {

    //x-coordinate of first end of edge
    private int x1;

    //x-coordinate of second end of edge
    private int x2;

    //y-coordinate of first end of edge
    private int y1;

    //y-coordinate of second end of edge
    private int y2;


    /**
     * constructor to set the end points of edge
     * @param x1
     * @param x2
     * @param y1
     * @param y2
     */
    public Edge(int x1, int x2, int y1, int y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     *
     * @returns x-coordinate of first end of edge
     */
    public int getX1() {
        return x1;
    }

    /**
     *
     * @returns x-coordinate of second end of edge
     */
    public int getX2() {
        return x2;
    }

    /**
     *
     * @returns y-coordinate of first end of edge
     */
    public int getY1() {
        return y1;
    }

    /**
     *
     * @returns y-coordinate of second end of edge
     */
    public int getY2() {
        return y2;
    }
}
