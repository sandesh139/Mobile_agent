package MobileAgent;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * this class gets the file reader object and use the information to
 * here, threads are instantiated to get the fire spread and the agent movement.
 */
public class Controller {

    /**Declaring the fileReader object to get information of nodes, edges, fires, and basestation. */
    private FileReader fileReader;

    /**Declaring the list of nodes to hold all the nodes that we get from the file reader. */
    private List<Node> nodes;

    /**Declaring the list of edges to hold all the edges that we get from the file reader. */
    private List<Edge> edges;

    /**Declaring the BlockingQueue to hold the nodes that have agent in it. */
    private BlockingQueue<Node> agentNode;

    /**Declaring the BlockingQueue to store the nodes that have neighbors on fire. */
    private BlockingQueue<Node> nearFireNodes;

    /**Declaring the LinkedList to store the node that are on fire. */
    private LinkedList<Node> onFireNodes;

    /**Declaring the Basestation to hold the node that has the basestation in it. */
    private BaseStation baseStation;

    /**Declaring the MediaPlayer object to get the audio playing in the background during the simulation. */
    private MediaPlayer mediaPlayer;

    /**
     * constructor of the controller that takes the String name of the file.
     * this sets the edges, nodes, basestation, fire node, neighbors near by fire.
     * @param filePath
     */
    public Controller(String filePath){
        this.fileReader = new FileReader(filePath);
        fileReader.createNodeObject();
        fileReader.settingEdge();
        this.edges = fileReader.getEdgeList();
        this.nodes = fileReader.getNodeList();
        this.baseStation = fileReader.getBaseStation();
        agentNode = new ArrayBlockingQueue<>(1);
        agentNode.add(baseStation);
        nearFireNodes = new LinkedBlockingDeque<>();
        onFireNodes = new LinkedList<>();
        nearFireNodes.addAll(fileReader.getNodesNearFire());
        onFireNodes.addAll(fileReader.getOnFireNodes());
        java.net.URL resource = getClass().getResource("fire.mp3");
        Media fireSound = new Media(resource.toString());
        this.mediaPlayer= new MediaPlayer(fireSound);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mediaPlayer.seek(Duration.ZERO);
            }
        });
    }

    /**
     * this method sets new node on fire and play the audio.
     * we keep the information of which nodes have neighbor in fire.
     */
    public void setNewNodeOnFire(){
        if(nearFireNodes.size() !=0){
            try {
                Thread.sleep(3000);
                Node node = nearFireNodes.take();
                for(Node n: node.getNeighbors()){
                    n.setIsNeighborOnFire(true);
                    mediaPlayer.play();
                    n.cloneAgentToNeighbors();
                    if(!n.isOnFire() && !nearFireNodes.contains(n)){
                        nearFireNodes.put(n);
                    }
                }
                node.setOnFire(true);
                onFireNodes.add(node);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Instantiating the thread to handle the fire spreading in forest.
     */
    Thread fireSpreader = new Thread(new Runnable() {
        @Override
        public void run() {
            while (onFireNodes.size() < nodes.size()) {
                setNewNodeOnFire();
            }
            mediaPlayer.stop();
        }
    });

    /**
     * instantiating the thread to work with the agent mover before the agent gets into node that has its neighbors on
     * fire.
     * we keep  taking and putting the agent from the agentNode BlockingQueue.
     */
    Thread initialAgentMover = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Node currNode = agentNode.take();
                Node nextNode = currNode.nextAgentNode();
                while (!currNode.isNeighborOnFire()) {
                    if (nextNode != null) {
                        Thread.sleep(1000);
                        nextNode.setAgent(currNode.getAgent());
                        nextNode.addMessage("Agent "+currNode.getAgent().getId()+" is at the node at coordinate ("+nextNode.getX()+", "+
                                nextNode.getY()+")");
                        currNode.removeAgent();
                        agentNode.put(nextNode);
                    }
                    if (agentNode.size() != 0) {
                        currNode = agentNode.take();
                        nextNode = currNode.nextAgentNode();
                    }
                }
                currNode.cloneAgentToNeighbors();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });


    /**
     * here a thread is instantiated which later creates threads for each nodes.
     */
    Thread nodeController = new Thread(new Runnable() {
        @Override
        public void run() {
            while (onFireNodes.size()!= nodes.size()) {
                for (Node n : nodes) {
                    Thread thread = new Thread(n);
                    if (n.getNewMessages().size() > 0 && !n.isNodeRunning()) {
                        thread.start();
                        n.setNodeRunning(true);
                    }
                }
            }
        }
    });

    /**
     * this method starts the thread for nodes, agent and fire.
     */
    public void runSimulation(){
        nodeController.start();
        initialAgentMover.start();
        fireSpreader.start();
    }


    /**
     *
     * @returns the edges to give to the gui simulation.
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     *
     * @returns the nodes to give to the gui simulation
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * @returns the message that are in the base station to give to the simulation.
     */
    public String getMessage(){
        return baseStation.getMessage();
    }
}
