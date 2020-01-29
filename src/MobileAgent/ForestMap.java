package MobileAgent;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;


/**
 * this class is used to draw the nodes and edges.
 * here we do the coloring of the nodes based on its status
 */
public class ForestMap extends Canvas {

    /**declaring the GraphicsContext to have 2d-canvas to draw on it */
    private GraphicsContext gc;

    /**Declaring the list of Node object to store the nodes to be drawn on canvas */
    private List<Node> nodes;

    /**Declaring the list of Edge object to store the edges to be drawn on canvas */
    private List<Edge> edges;
    int multiplierX;
    int multiplierY;

    /**
     * constructor to set the nodes and edges to be drawn on the canvas.
     * @param nodes
     * @param edges
     */

    private Image forest;
    public ForestMap(List<Node> nodes, List<Edge> edges){
        this.nodes = nodes;
        this.edges = edges;
        this.gc = getGraphicsContext2D();
        this.forest =  new Image(getClass().getResourceAsStream("forest.png"));
        int maxX = 0;
        int maxY = 0;


        //here we work on scaling the graph based on the max x-coordinates and max y-coordinates
        for(Node n: nodes){
            if(n.getX()>maxX){
                maxX = n.getX();
            }
            if(n.getY()>maxY){
                maxY = n.getY();
            }
        }

        multiplierX = 75;
        multiplierY = 75;
        if(maxX*multiplierX>1000){
            multiplierX = 1000/maxX;
        }
        if(maxY*multiplierY>1000){
            multiplierY = 1000/maxY;
        }
        if(maxX*multiplierX>2000){
            multiplierX = 2000/maxX;
        }
        if(maxY*multiplierY>2000){
            multiplierY = 2000/maxY;
        }
        this.setHeight((maxY*multiplierY)+75);
        this.setWidth((maxX*multiplierX)+50);
    }


    /**
     *this method is called to draw on canvas
     */
    public void draw(){
        gc.drawImage(forest,0,0,getWidth(),getHeight());

        gc.setStroke(Color.BLUE);

        //we draw the line for each edge.
        for(Edge edge: edges){
            gc.strokeLine(25+edge.getX1()*multiplierX,25+edge.getY1()*multiplierY,25+edge.getX2()*multiplierX,25+edge.getY2()*multiplierY);
        }


        //here we draw each nodes having offset of 17 and scale of 75.
        int i = 0;
        for(Node node: nodes){
            i++;
            int x = 17+node.getX()*multiplierX;
            int y = 17+node.getY()*multiplierY;

            //if node is on fire, node color is red
            if(node.isOnFire()){
                gc.setFill(Color.RED);
                gc.fillOval(x,y,20,20);
            }else if(node.isNeighborOnFire()){      //if node has its neighbor on fire, node is set to Yellow
                gc.setFill(Color.YELLOW);
                gc.fillOval(x,y,20,20);
            } else {
                gc.setFill(Color.AQUA);      //if node is not on fire nor has its neighbor on fire, node is set to blue.
                gc.fillOval(x,y,20,20);
            }

            //if node is Basestation we provide the purple color.
            if(node.isBaseStation()){
                if(node.isOnFire()) {
                    gc.setFill(Color.RED);
                    gc.fillOval(x - 5, y - 5, 26, 26);
                }else {
                    gc.setFill(Color.MEDIUMORCHID);
                    gc.fillOval(x - 5, y - 5, 26, 26);
                }
            }

            //if the node has agent on it, then we draw rectangle surrounding the node
            if(node.hasAgent()){
                if(node.getAgent().isCurrNodeOnFire())
                    gc.setStroke(Color.RED);
                else
                    gc.setStroke(Color.MAGENTA);
                if(node.isBaseStation()){
                    gc.strokeRect((node.getX()*multiplierX)+6,(node.getY()*multiplierY)+8,36,36);
                }else {
                    gc.strokeRect((node.getX() * multiplierX) + 12, (node.getY() * multiplierY) + 12, 30, 30);
                }
                gc.strokeText("Agt-"+node.getAgent().getId(),(node.getX()*multiplierX),(node.getY()*multiplierY)+55);
            }
        }

    }

    /**
     *
     * @param nodes is passed to this method to provide all the nodes to be drawn on canvas
     */
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

}
