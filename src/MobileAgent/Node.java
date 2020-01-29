package MobileAgent;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * this class implements the Runnable interface.
 *this is used to represent the each nodes in the forest map
 * we set the properties like setting node on fire, setting agent on it, setting its neghbors
 * and also has getters to receive those information.
 */
public class Node implements Runnable{

    /**declaring the integer which is used to store x-coordinate of nodes */
    private int x;

    /**declaring the integer which is used to store y-coordinate of nodes */
    private int y;

    /**initiating the arraylist of Node to store the neighbors of the node */
    private List<Node> neighbors = new ArrayList<>();

    /**declaring a boolean which is set to true if the node is on fire and false otherwise.*/
    private boolean isOnFire;

    /**declaring a boolean which is set to true if its neigbor is on fire */
    private boolean isNeighborOnFire;

    /**declaring Agent object which will be used to have an agent in the node during the simulation process. */
    private Agent agent;

    /**declaring the boolean and is set true if it has agent in it and false otherwise*/
    private boolean hasAgent;

    /**declaring the boolean which is set to true if the node is base station. */
    private boolean isBaseStation;

    private boolean isNodeRunning;


    /**declaring BlockingQueue object to store the new messages in the node and provide those message whenever
     *available in the queue */
    private BlockingQueue<String> newMessages;

    /**declaring linked List to store the message transferring and manage the redundancy
     of the message getting transferred. */
    private LinkedList<String> passedMessages;


    /**
     * At the beginning no node is on fire. If they are on fire from the config file or  if their neighbor is on fire
     * from the config file, use the setters to set them of fire from the fileReader object
     * @param x the x index of the node
     * @param y the y index of the node
     */
    public Node(int x, int y){
        this.x = x;
        this.y = y;
        this.isOnFire = false;
        this.isNeighborOnFire = false;
        this.agent = null;
        this.hasAgent = false;
        this.isBaseStation = false;
        this.newMessages = new LinkedBlockingDeque<>();
        this.passedMessages = new LinkedList<>();
        this.isNodeRunning = false;
    }

    /**
     *
     * @returns true if the given node is the Base Station and false otherwise.
     */
    public boolean isBaseStation() {
        return isBaseStation;
    }


    /**
     *
     * @param baseStation boolean is passed and set to true if the node is base station
     */
    public void setBaseStation(boolean baseStation) {
        isBaseStation = baseStation;
    }




    /**
     *
     * @returns true if the node is on fire and returns false if the fire is not in the fire.
     */
    public boolean isOnFire() {
        return isOnFire;
    }

    /**
     *
     * @returns the x-coordinates of the node
     */
    public int getX(){
        return x;
    }

    /**
     *
     * @returns the y-coordinate of the node
     */
    public int  getY(){
        return y;
    }

    /**
     * here we are adding unique neighbors to set.
     * @param neighbor
     */

    public void addNeighbors(Node neighbor) {
        neighbors.add(neighbor);
    }

    /**
     *
     * @param neighborOnFire boolean is passed to this method so, we can set if the node has any neighbor on fire.
     */
    public void setIsNeighborOnFire(boolean neighborOnFire) {
        isNeighborOnFire = neighborOnFire;
    }

    /**
     *
     * @returns boolean true if the node's neighbor is on fire and returns false if the neighbors node is not in fire.
     */
    public boolean isNeighborOnFire() {
        return isNeighborOnFire;
    }

    /**
     *
     * @param onFire boolean is passed to set if the node is on fire or not.
     */
    public void setOnFire(boolean onFire) {
        isOnFire = onFire;
    }

    /**
     *
     * @param agent is passed to set an Agent to the node.
     * here we also set boolean true for the node's agent.
     */
    public void setAgent(Agent agent) {
        this.hasAgent = true;
        this.agent = agent;
        agent.setCurrNode(this);
    }



    /**
     *
     * @returns true if the node has agent in it.
     */
    public boolean hasAgent() {
        return hasAgent;
    }

    /**
     *
     * @returns the number of neighbors that the node has.
     */
    public int totalNeighbors(){
        return neighbors.size();
    }

    /**
     *
     * @returns the List of neighbors node of the node.
     */
    public List<Node> getNeighbors() {
        return neighbors;
    }

    /**
     * this method is used to remove the agent from the node.
     * and the boolean hasAgent is set to false, since the agent is removed.
     */
    public void removeAgent(){
        this.agent = null;
        this.hasAgent = false;
    }

    /**
     *
     * @returns the BlockingQueue containing the new message
     */
    public BlockingQueue<String> getNewMessages() {
        return newMessages;
    }

    /**
     *
     * @returns the passedMessage
     */
    public LinkedList<String> getPassedMessages() {
        return passedMessages;
    }

    /**
     *
     * @param message is passed to this method
     * if the message is not already contained in the newMessage BlockingQueue, we add the message to it
     *
     */
    public void addMessage(String message){
        if(!newMessages.contains(message) && !passedMessages.contains(message)){
            try {
                synchronized (this) {
                    newMessages.put(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *this method is used to pass the message to all its neighbors.
     */
    public void passMessage(){
        try {
            if(newMessages.size()!=0) {
                String message = newMessages.take();
                passedMessages.add(message);
                for (Node n : neighbors) {
                    n.addMessage(message);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @returns random neighbor of the node to set the agent in the next node.
     */
    public Node nextAgentNode(){
        Object lock = new Object();
        List<Node> list = neighbors;
        Node[] nodeList = neighbors.toArray(new Node[0]);
        synchronized (lock) {
            for(int i = 0; i<nodeList.length; i++){
                Node n = nodeList[i];
                if(n.hasAgent()){
                    list.remove(n);
                }
            }
        }
        if (list.size() == 0) {           //we return null, if there is no eligible neighbor to set agent on it
            return null;
        }
        Random random = new Random();
        int x = random.nextInt(totalNeighbors());
        Node node = list.get(x);
        return node;
    }


    /**
     * here we set clone the agent to the neighbor of the node if we meet the required conditions
     */
    public void cloneAgentToNeighbors(){
        if(isNeighborOnFire && hasAgent){
            for (Node node: neighbors){
                if(!node.hasAgent && !node.isOnFire()){
                    Agent newAgent = this.agent.spawnAgent(node);  //here we ge the new agent
                    node.setAgent(newAgent);                  //setting new agent to the node.
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //we provide message to addMessage to be transferred and processed by the base station
                    node.addMessage("Agent cloned - New agent "+newAgent.getId()+" has been created at the node at" +
                            " coordinate ("+ node.getX()+", "+node.getY()+")");
                }
            }
        }
    }

    /**
     *
     * @returns the Agent of the node.
     */
    public Agent getAgent() {
        return agent;
    }

    public boolean isNodeRunning() {
        return isNodeRunning;
    }

    public void setNodeRunning(boolean nodeRunning) {
        isNodeRunning = nodeRunning;
    }

    @Override
    /**
     * here we override the run method from runnable interface.
     */
    public void run() {

        //if node is not fire then we keep passing the message from the node
        while (!isOnFire){
            passMessage();
        }

        // we send the message to its neighbors.
        for (Node n: neighbors){
            n.addMessage("Node at the coordinate ("+this.getX()+", "+this.getY()+") is on fire. All communication with the node is lost");
        }
    }
}
