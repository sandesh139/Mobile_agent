package MobileAgent;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Scanner;


/**
 * this class gets all the information from the controller and provide them to the forestmap
 * and message board to get simulation displayed.
 */
public class SimulationGui extends Application {

    /**instantiating the boolean to know if the simulation has already started. */
    private boolean isSimulationStarted = false;

    /**
     *main method which calls launch method with args parameter.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * start method invoked from the JavaFX Application
     * here we link all the other classes together. The class gets the information from
     * controller and provide them to the forestmap and message board to have those information
     * displayed.
     * @param stage
     */
    @Override
    public void start(Stage stage){
        stage.setTitle("Mobile Agent v1.0");
        System.out.println("Please Type the absolute path of the file " +
                "For example,\nif your file is in the (user) directory of home directory in linux the path is \\home\\user\\(file name)" +
                "\nIf you input wrong file path, the simulation will continue with the demo file");
        Scanner sc = new Scanner(System.in);
        String fileName = sc.nextLine();
        Controller controller = new Controller(fileName);
        ForestMap forestMap = new ForestMap(controller.getNodes(),controller.getEdges());
        MessageBoard messageBoard = new MessageBoard(controller.getMessage());
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #261b1a");
        ScrollPane scrollTree = new ScrollPane();
        scrollTree.setPannable(true);
        if(forestMap.getHeight()<800){
            scrollTree.setPrefViewportHeight(forestMap.getHeight());
        }else {
            scrollTree.setPrefViewportHeight(800);
        }
        if(forestMap.getWidth()<1000){
            scrollTree.setPrefViewportWidth(forestMap.getWidth());
        }else {
            scrollTree.setPrefViewportWidth(1000);
        }
        scrollPane.setPrefViewportWidth(650);
        scrollPane.setContent(messageBoard);
        scrollTree.setContent(forestMap);
        messageBoard.heightProperty().addListener(
                (observable, oldValue, newValue) -> {
                    scrollPane.setVvalue( 1.0d );
                }
        );
        forestMap.draw();
        messageBoard.draw();

        java.net.URL resource = getClass().getResource("back.wav");
        Media backGroundSound = new Media(resource.toString());
        MediaPlayer mediaPlayer = new MediaPlayer(backGroundSound);
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
            }
        });
        Button startButton = new Button("Start Simulation");
        startButton.setStyle("-fx-background-color: #d90007; -fx-text-fill: white; -fx-font-size: 22px; -fx-pref-width: 300");
        startButton.setOnMouseClicked(event -> {
            if(!isSimulationStarted) {
                controller.runSimulation();
                mediaPlayer.play();
                isSimulationStarted = true;
            }
        });


        HBox hBox = new HBox();
        hBox.setStyle("-fx-background-color: #231b38");
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.getChildren().add(startButton);

        BorderPane root  = new BorderPane();
        root.setStyle("-fx-background-color: #000000");
        root.setCenter(forestMap);
        root.setRight(scrollPane);
        root.setLeft(scrollTree);
        root.setBottom(hBox);
        stage.setScene(new Scene(root));
        stage.show();

        //instantiating the AnimationTimer to have the GUI updated rapidly.
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                forestMap.setNodes(controller.getNodes());
                messageBoard.setMessage(controller.getMessage());
                forestMap.draw();
                messageBoard.draw();
            }
        };
        timer.start();
    }
}
