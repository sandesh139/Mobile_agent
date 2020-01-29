package MobileAgent;

import java.util.Random;


/**
 * this class is a child class of Node.
 * this helps us adding the extra features to a node to get a working base station.
 */
public class BaseStation extends Node {

    /**Declaring a String to hold all the messages that are sent to the base station. */
    private String allMessage;

    /**
     *
     * @param x is used to set the x-coordinates of the base station.
     * @param y is used to set the y-coordinates of the base station.
     */
    public BaseStation(int x, int y) {
        super(x, y);
        setBaseStation(true);
        int id  = new Random().nextInt(50)+10220;
        setAgent(new Agent(this,id));
        this.allMessage = "";
    }


    /**
     *
     * @returns the message that are in the base station.
     * This getter is used to show to the information in gui section.
     */
    public String getMessage(){
        return allMessage;
    }


    /**
     * this method appends the message.
     */
    public void processMessage(){
        StringBuilder stringBuilder = new StringBuilder(allMessage);
        try {
            if(getNewMessages().size()!=0) {
                String str = getNewMessages().take();
                getPassedMessages().add(str);
                stringBuilder.append(str+ "\n");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        allMessage = stringBuilder.toString();
    }


    /**
     * this method overrides the run method and is used to call process message if the base station is not in fire.
     */
    @Override
    public void run() {
        while (!isOnFire()){
            processMessage();
        }
        StringBuilder stringBuilder = new StringBuilder(allMessage);
        stringBuilder.append("Base station is on fire. All communication disabled\n");
        allMessage = stringBuilder.toString();
    }
}
