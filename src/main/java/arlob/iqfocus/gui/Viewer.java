package arlob.iqfocus.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * A very simple viewer for piece placements in the IQ-Focus game.
 * <p>
 * NOTE: This class is separate from your main game class.  This
 * class does not play a game, it just illustrates various piece
 * placements.
 */
public class Viewer extends Application {

    /* board layout */
    private static final int SQUARE_SIZE = 60;
    private static final int VIEWER_WIDTH = 720;
    private static final int VIEWER_HEIGHT = 480;

    private static final String URI_BASE = "assets/";

    private final Group root = new Group();
    private final Group controls = new Group();
    private final Group tiles = new Group();
    private TextField textField;

    /**
     * Draw a placement in the window, removing any previously drawn one
     *
     * @param placement A valid placement string
     */
    void makePlacement(String placement) {
        tiles.getChildren().clear();
        int n = placement.length();

        double pixelSize=0.6;//60/100  view size/image size

        for (int i = 0; i < n; i = i + 4) {
            String piece = placement.substring(i, i + 4);
            int col = Character.getNumericValue(piece.charAt(1));
            int row = Character.getNumericValue(piece.charAt(2));
            int ori = Character.getNumericValue(piece.charAt(3));

            ImageView imgView = new ImageView(); //https://docs.oracle.com/javase/8/javafx/api/index.html?javafx/scene/image/ImageView.html
            Image img = new Image(getClass().getResource(URI_BASE + piece.charAt(0) + ".png").toString());


            double height = img.getHeight() * pixelSize;
            double width = img.getWidth() * pixelSize;
            imgView.setFitHeight(height);
            imgView.setFitWidth(width); // https://stackoverflow.com/questions/27894945/how-do-i-resize-an-imageview-image-in-javafx/27894962
            imgView.setImage(img); // get path of the image to create new image

            double x= col * SQUARE_SIZE,
                    y = row * SQUARE_SIZE;
            imgView.setRotate(ori * 90);


            if (ori % 2 != 0) {
                x = col * SQUARE_SIZE + height/2 - width/2;
                y = row * SQUARE_SIZE + width/2 - height/2;
            }

           imgView.setX(x);
           imgView.setY(y);
            tiles.getChildren().add(imgView);
        }
    }


    /**
     * Create a basic text field for input and a refresh button.
     */
    private void makeControls() {
        Label label1 = new Label("Placement:");
        textField = new TextField();
        textField.setPrefWidth(300);
        Button button = new Button("Refresh");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                makePlacement(textField.getText());
                textField.clear();
            }
        });
        HBox hb = new HBox();
        hb.getChildren().addAll(label1, textField, button);
        hb.setSpacing(10);
        hb.setLayoutX(130);
        hb.setLayoutY(VIEWER_HEIGHT - 50);
        controls.getChildren().add(hb);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("FocusGame Viewer");
        Scene scene = new Scene(root, VIEWER_WIDTH, VIEWER_HEIGHT);

        root.getChildren().add(controls);
        root.getChildren().add(tiles);

        makeControls();

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
