package MobileAgent;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * this class reads the file and creates the nodes and edges.
 */
public class FileReader {

    /**initiating an arraylist to hold all nodes that are found in file reading. */
    private  List<Node> nodeList = new ArrayList<>();

    /**initiating an arraylist to hold all the lines as string */
    private List<String> fileLines = new LinkedList<>();

    /**initiating an arraylist to hold all edges. */
    private List<Edge> edgeList = new ArrayList<>();

    /**Declaring the base station object which will be added to list of nodes later on */
    private BaseStation baseStation;

    /**declaring the LinkedBlockingQueue object to store the nodes that are near by the fire */
    private LinkedBlockingQueue<Node> nodesNearFire;

    /**declaring the linkedlist to store the nodes that are on fire. */
    private LinkedList<Node> onFireNodes;

    /**
     * We read the file once the FileReader object is created
     * to create the fileReader object, a fileName parameter must be passed
     * @param fileName the name of the file
     */
    public FileReader(String fileName){
        nodesNearFire = new LinkedBlockingQueue<>();
        onFireNodes = new LinkedList<>();
        Path filePath = Paths.get(fileName);
        try {
            InputStream inputStream = Files.newInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            LinkedList<String> lines = new LinkedList<>();
            try {
                while ((line=reader.readLine()) !=null){
                    fileLines.add(line);
                }
            }catch (Exception e){
                System.out.println("Wrong filename");
            }
        }catch (Exception e){
            System.out.println("Something went wrong. Please try the following:\n" +
                    "--Check if the file path for input file is okay. Path name must be absolute.\n" +
                    "--Try putting the input file in the same directory as .jar file (Unlikely cause\n"+
                    "\nContinuing Simulation with the demo file...");
            InputStream inputStream = getClass().getResourceAsStream("sample.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            LinkedList<String> lines = new LinkedList<>();
            try {
                while ((line=reader.readLine()) !=null){
                    fileLines.add(line);
                }
            }catch (Exception ev){
                System.out.println("Wrong filename");
            }
        }
    }

    /**
     *first we need to set  the node so we can match each nodes for their neighbours.
     */
    public void createNodeObject(){
        for(int i=0; i<fileLines.size();i++){
            String temp = fileLines.get(i);
            String[] str = temp.split(" ");
            if(str[0].toLowerCase().contains("node")){
                nodeList.add(new Node(Integer.parseInt(str[1]),Integer.parseInt(str[2])));
            }
        }

    }

    /**
     *first for loop goes through the each line from file
     * second for loop goes through each node to check the first point of edge
     * third for loop goes through each node to check the second point of edge
     * And also, setting the fire for the node and replacing the a node with base station node.
     */
    public void settingEdge(){
        for(int i=0; i<fileLines.size();i++){
            String temp = fileLines.get(i);
            String[] str = temp.split(" ");
            if(str[0].toLowerCase().contains("edge")){
                for(int j=0;j< nodeList.size();j++){
                    if(Integer.parseInt(str[1])==nodeList.get(j).getX() &&
                            Integer.parseInt(str[2])==nodeList.get(j).getY()){
                        for (int k =0;k<nodeList.size();k++) {
                            if (Integer.parseInt(str[3]) == nodeList.get(k).getX() &&
                                    Integer.parseInt(str[4]) == nodeList.get(k).getY()) {
                                edgeList.add(new Edge(Integer.parseInt(str[1]),Integer.parseInt(str[3]),
                                        Integer.parseInt(str[2]),Integer.parseInt(str[4])));
                                nodeList.get(j).addNeighbors(nodeList.get(k));
                                nodeList.get(k).addNeighbors(nodeList.get(j));
                                break;
                            }
                        }
                    }
                }
            }
        }
        setBaseStation();
        setFire();
    }


    /**
     * this method is used to set the basestation.
     */
    public void setBaseStation(){
        for(int i=0; i<fileLines.size();i++) {
            String temp = fileLines.get(i);
            String[] str = temp.split(" ");
            if (str[0].toLowerCase().contains("station")) {
                for (int j = 0; j < nodeList.size(); j++) {
                    if (Integer.parseInt(str[1]) == nodeList.get(j).getX() &&
                            Integer.parseInt(str[2]) == nodeList.get(j).getY()) {
                        BaseStation baseStation = new BaseStation(Integer.parseInt(str[1]), Integer.parseInt(str[2]));
                        nodeList.add(baseStation);
                        this.baseStation = baseStation;
                        for (Node node : nodeList.get(j).getNeighbors()) {
                            node.getNeighbors().remove(nodeList.get(j));
                            node.addNeighbors(baseStation);
                            baseStation.addNeighbors(node);
                        }
                        baseStation.setIsNeighborOnFire(nodeList.get(j).isOnFire());
                        nodeList.remove(nodeList.get(j));
                        break;
                    }
                }
            }
        }
    }

    /**
     *this method sets the node that has fire in it.
     * and also stores the node's neighbor that has fire in it.
     */
    public void setFire(){
        for(int i=0; i<fileLines.size();i++) {
            String temp = fileLines.get(i);
            String[] str = temp.split(" ");
            if (str[0].toLowerCase().contains("fire")) {
                for (int j = 0; j < nodeList.size(); j++) {
                    if (Integer.parseInt(str[1]) == nodeList.get(j).getX() &&
                            Integer.parseInt(str[2]) == nodeList.get(j).getY()) {
                        nodeList.get(j).setOnFire(true);
                        onFireNodes.add(nodeList.get(j));
                        for (int k = 0; k < nodeList.get(j).getNeighbors().size(); k++) {
                            nodeList.get(j).getNeighbors().get(k).setIsNeighborOnFire(true);
                            this.nodesNearFire.add(nodeList.get(j).getNeighbors().get(k));
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @returns the base station node
     */
    public BaseStation getBaseStation() {
        return baseStation;
    }

    /**
     *
     * @returns the List of nodes.
     */
    public List getNodeList(){
        return nodeList;
    }

    /**
     *
     * @returns the list of edges
     */
    public List getEdgeList(){
        return edgeList;
    }

    /**
     *
     * @return the nodes that are near by fire.
     */
    public LinkedBlockingQueue<Node> getNodesNearFire() {
        return nodesNearFire;
    }

    /**
     *
     * @returns the node that are on fire.
     */
    public LinkedList<Node> getOnFireNodes() {
        return onFireNodes;
    }

}
