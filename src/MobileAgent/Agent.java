package MobileAgent;

import java.util.Random;


/**
 * this class implements the Runnable interface.
 * Here, this class is used to clone and object.
 *
 */
public class Agent{
    private Node currNode;
    private boolean onFireNode;
    private int id;
    private boolean currNodeOnFire;


    /**
     * @param currNode here we set property of new agent with unique id
     * @param id
     */
    public Agent(Node currNode, int id){
        this.currNode = currNode;
        this.id = id;
        if(currNode.isOnFire()){
            onFireNode = true;
        }else {
            onFireNode = false;
        }
        currNodeOnFire = false;
    }

    /**
     *
     * @param node is used to set the current node and as the node that is passed as node.
     */
    public void setCurrNode(Node node){
        this.currNode  = node;
    }

    /**
     * returns the new agent.
     * @param node is passed in which node is created.
     * @return
     */
    public Agent spawnAgent(Node node){
        return new Agent(node, this.id+ new Random().nextInt(50)+10);
    }

    /**
     * @returns the unique homework.
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @returns true if the current node is on fire and false otherwise.
     */
    public boolean isCurrNodeOnFire() {
        if(currNode.isOnFire())
            return true;
        else
            return false;
    }
}
