package MobileAgent;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * this class extends the Canvas to get the graphics for showing the message information.
 */
public class MessageBoard extends Canvas {
    private GraphicsContext gc;
    private String message;

    /**
     * constructor of the class to set the width and the message.
     * @param message
     */
    public MessageBoard(String message){
        gc = this.getGraphicsContext2D();
        this.message = message;
        setWidth(650);
    }

    /**
     * this method fills the GUI with the message.
     */
    public void draw(){
        int h = 0;
        for(int i = 0; i< message.length(); i++){
            if(message.charAt(i)=='\n'){
                h++;
            }
        }
        setHeight(h*17);
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0, getWidth(), getHeight());
        gc.setFill(Color.BLUE);
        gc.setFont(new Font("Arial", 15));
        gc.fillText(message,0,0);
    }

    /**
     * this is setter which sets the message to be displayed on the canvas.
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
