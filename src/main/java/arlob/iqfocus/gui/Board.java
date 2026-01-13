package arlob.iqfocus.gui;

import arlob.iqfocus.FocusGame;
import arlob.iqfocus.Solution;
import arlob.iqfocus.classes.BoardState;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Random;


public class Board extends Application {
    private static final int BOARD_WIDTH = 540; //720;
    private static final int BOARD_HEIGHT = 300; //463;
    private static final int GAME_WIDTH = 933;
    private static final int GAME_HEIGHT = 700;
    private static final int SQUARE_SIZE = 60;



    private final String BASEBOARD_URI = getClass().getClassLoader().getResource("lite-1.png").toString();

    private static final int MARGIN_X = 30;
    private static final int MARGIN_Y = 30;
    private static final int BOARD_Y = MARGIN_Y;
    private static final int BOARD_X = (3 * SQUARE_SIZE);//+ SQUARE_SIZE; + MARGIN_X;
    private static final int PLAY_AREA_Y = BOARD_Y;//BOARD_MARGIN;
    private static final int PLAY_AREA_X = BOARD_X;//BOARD_MARGIN;

    private static final long ROTATION_THRESHOLD = 75;

    private static final char OFF_BOARD = 32;

    private PlaceString ps = new PlaceString();

    private BoardState state = new BoardState();

    String objective;
    String solution;

    HashMap<Character, DraggablePiece> pieceMap = new HashMap<>();

    Text completionText = new Text("You win");
    Text warning = new Text("INVALID PLACEMENT");

    private final Group root = new Group();
    private final Group board = new Group();
    private final Group pieces = new Group();
    private final Group controls = new Group();
    private final Group challengeDisplay = new Group();

    class PlaceString {
        /* Stores an ordered placement string, has methods to alter individual parts */
        private String a, b, c, d, e, f, g, h, i, j;

        PlaceString() {
            clear();
        }

        PlaceString(PlaceString p) {
            this.a = p.a;
            this.b = p.b;
            this.c = p.c;
            this.d = p.d;
            this.e = p.e;
            this.f = p.f;
            this.g = p.g;
            this.h = p.h;
            this.i = p.i;
            this.j = p.j;
        }

        public void updateString(String s) {
            switch(s.charAt(0)) {
                case 'a': a = s;
                    break;
                case 'b': b = s;
                    break;
                case 'c': c = s;
                    break;
                case 'd': d = s;
                    break;
                case 'e': e = s;
                    break;
                case 'f': f = s;
                    break;
                case 'g': g = s;
                    break;
                case 'h': h = s;
                    break;
                case 'i': i = s;
                    break;
                case 'j': j = s;
            }
        }

        public void remove(char z) {
            switch(z) {
                case 'a': a = "";
                    break;
                case 'b': b = "";
                    break;
                case 'c': c = "";
                    break;
                case 'd': d = "";
                    break;
                case 'e': e = "";
                    break;
                case 'f': f = "";
                    break;
                case 'g': g = "";
                    break;
                case 'h': h = "";
                    break;
                case 'i': i = "";
                    break;
                case 'j': j = "";
            }
        }

        public void clear() {
            a = "";
            b = "";
            c = "";
            d = "";
            e = "";
            f = "";
            g = "";
            h = "";
            i = "";
            j = "";
        }

        @Override
        public String toString() {
            return a + b + c + d + e + f + g + h + i + j;
        }
    }

    class Piece extends ImageView {
        int tileID;

        Piece(char type) {
            if(type > 'j' || type < 'a') { throw new IllegalArgumentException("Bad tile: \"" + type + "\""); }

            this.tileID = type - 'a';
            Image img = new Image(getClass().getClassLoader().getResource(type + ".png").toString());
            setImage(img);

            setFitHeight(img.getHeight() * 0.6);
            setFitWidth(img.getWidth() * 0.6);
        }
    }

    class DraggablePiece extends Piece {
        int homeX, homeY;
        double mouseX, mouseY;
        int orientation;
        char type;
        long lastRotationTime = System.currentTimeMillis();
        int x, y;
        int mod, mov;
        boolean onBoard = false;

        DraggablePiece(char type, int hx, int hy) {
            super(type);
            this.type = type;
            orientation = 0;

            this.homeX = hx;
            this.homeY = hy;

            relocate(homeX, homeY);

            setOnScroll(event -> {
                if (System.currentTimeMillis() - lastRotationTime > ROTATION_THRESHOLD){
                    lastRotationTime = System.currentTimeMillis();
                    hideCompletion();
                    rotate();
                    event.consume();
                    checkCompletion();
                }
            });

            setOnMousePressed(event -> {
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
            });

            setOnMouseDragged(event -> {
                hideCompletion();
                toFront();
                double movementX = event.getSceneX() - mouseX;
                double movementY = event.getSceneY() - mouseY;
                setLayoutX(getLayoutX() + movementX);
                setLayoutY(getLayoutY() + movementY);
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
                event.consume();
            });

            setOnMouseReleased(event -> {     // drag is complete
                setCoords();
                update();
                addToGrid();
                checkCompletion();
            });
        }



        private void setCoords(){
            /* Assigns x, y according to closest square */
            double lX = getLayoutX();
            double lY = getLayoutY();

            if(orientation % 2 == 1) {
                lX += mod;
                lY -= mod;
            }

            if ((lX >= (PLAY_AREA_X - (SQUARE_SIZE / 2))) && (lX < (PLAY_AREA_X + (SQUARE_SIZE / 2)))) {
                this.x = 0;
            } if ((lX >= PLAY_AREA_X + (SQUARE_SIZE / 2)) && (lX < PLAY_AREA_X + 1.5 * SQUARE_SIZE)) {
                this.x = 1;
            } else if ((lX >= PLAY_AREA_X + 1.5 * SQUARE_SIZE) && (lX < PLAY_AREA_X + 2.5 * SQUARE_SIZE)) {
                this.x = 2;
            } else if ((lX >= PLAY_AREA_X + 2.5 * SQUARE_SIZE) && (lX < PLAY_AREA_X + 3.5 * SQUARE_SIZE)) {
                this.x = 3;
            } else if ((lX >= PLAY_AREA_X + 3.5 * SQUARE_SIZE) && (lX < PLAY_AREA_X + 4.5 * SQUARE_SIZE)) {
                this.x = 4;
            } else if ((lX >= PLAY_AREA_X + 4.5 * SQUARE_SIZE) && (lX < PLAY_AREA_X + 5.5 * SQUARE_SIZE)) {
                this.x = 5;
            } else if ((lX >= PLAY_AREA_X + 5.5 * SQUARE_SIZE) && (lX < PLAY_AREA_X + 6.5 * SQUARE_SIZE)) {
                this.x = 6;
            } else if ((lX >= PLAY_AREA_X + 6.5 * SQUARE_SIZE) && (lX < PLAY_AREA_X + 7.5 * SQUARE_SIZE)) {
                this.x = 7;
            } else if ((lX >= PLAY_AREA_X + 7.5 * SQUARE_SIZE) && (lX < PLAY_AREA_X + 8.5 * SQUARE_SIZE)) {
                this.x = 8;
            }

            if ((lY >= (PLAY_AREA_Y - (SQUARE_SIZE / 2))) && (lY < (PLAY_AREA_Y + (SQUARE_SIZE / 2)))) {
                this.y = 0;
            } else if ((lY >= PLAY_AREA_Y + (SQUARE_SIZE / 2)) && (lY < PLAY_AREA_Y + 1.5 * SQUARE_SIZE)) {
                this.y = 1;
            } else if ((lY >= PLAY_AREA_Y + 1.5 * SQUARE_SIZE) && (lY < PLAY_AREA_Y + 2.5 * SQUARE_SIZE)) {
                this.y = 2;
            } else if ((lY >= PLAY_AREA_Y + 2.5 * SQUARE_SIZE) && (lY < PLAY_AREA_Y + 3.5 * SQUARE_SIZE)) {
                this.y = 3;
            } else if((lY >= PLAY_AREA_Y + 3.5 * SQUARE_SIZE) && (lY < PLAY_AREA_Y + 4.5 * SQUARE_SIZE)) {
                this.y = 4;
            }
        }

        private void addToGrid() {
            /* Places tiles on grid according to x,y  */
            if(FocusGame.isPlacementStringValid(ps.toString())) {
                setLayoutX(PLAY_AREA_X + this.x * SQUARE_SIZE - (orientation % 2 == 1 ? mod : 0));
                setLayoutY(PLAY_AREA_Y + this.y * SQUARE_SIZE + (orientation % 2 == 1 ? mod : 0));
            }
            onBoard = true;

            checkCompletion();
        }

        private void update() {
            /* Updates global placement string */
            String s = Character.toString(type) + x + "" + y + ((type == 'f' || type == 'g') ? orientation % 2 : orientation);

            PlaceString a = new PlaceString(ps);
            a.updateString(s);
            if(!a.toString().equals("") && !FocusGame.isPlacementStringValid(a.toString())) {
                returnHome();
                showWarning();
            } else {
                ps.updateString(s);
                hideWarning();
            }
        }

        private void returnHome() {
            orientation = 0;
            setRotate(0);
            ps.remove(type);
            relocate(homeX, homeY);
            onBoard = false;
        }

        private void rotate() {
            orientation = (orientation + 1) % 4;
            if(onBoard)
                update();
            toFront();
            setRotate(orientation * 90);

            switch (type) {
                case 'b', 'c', 'j' -> {
                    mod = 60;
                    mov = 60;
                }
                case 'f' -> mod = 60;
                case 'a', 'd', 'e', 'g' -> {
                    mod = 30;
                    mov = 30;
                }
            }

            if (orientation % 2 != 0) {
                setLayoutX(getLayoutX() - mov);
                setLayoutY(getLayoutY() + mov);
            } else {
                setLayoutX(getLayoutX() + mov);
                setLayoutY(getLayoutY() - mov);
            }
        }

        @Override
        public String toString() {
            return type + "" + x + y + "" + orientation;
        }
    }

    private void makeBoard() {
        board.getChildren().clear();

        ImageView baseboard = new ImageView();
        baseboard.setImage(new Image(BASEBOARD_URI));
        baseboard.setFitWidth(BOARD_WIDTH);
        baseboard.setFitHeight(BOARD_HEIGHT);
        baseboard.setLayoutX(BOARD_X);
        baseboard.setLayoutY(BOARD_Y);
        board.getChildren().add(baseboard);

        board.toBack();
    }

    private void setUpHandlers(Scene scene) {
        /* create handlers for key press and release events */
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.M) {
                challengeDisplay.setOpacity(1.0);
                pieces.setOpacity(0.0);
                event.consume();
            } else if (event.getCode() == KeyCode.Q) {
                Platform.exit();
                event.consume();
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.M) {
                challengeDisplay.setOpacity(0.0);
                pieces.setOpacity(1.0);
                event.consume();
            }
        });
    }


    private void getChallenge() {
        /* Retrieves challenge from solution file */
        objective = Solution.SOLUTIONS[new Random().nextInt(Solution.SOLUTIONS.length)].objective;
        solution = Solution.SOLUTIONS[new Random().nextInt(Solution.SOLUTIONS.length)].placement;
    }

    private void displayChallenge(String challenge, int x, int y) {
        /* Converts 9 character string to 3x3 representation from squares */
        int a = 0;
        int b = 0;

        challengeDisplay.getChildren().removeAll();

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                ImageView img = new ImageView();
                img.setImage(new Image(getClass().getClassLoader().getResource("sq-" + Character.toLowerCase(challenge.toCharArray()[(i*3) + j]) + ".png").toString()));
                img.setLayoutX(x);
                img.setLayoutY(y);
                img.setFitHeight(img.getImage().getHeight() * 0.6);
                img.setFitWidth(img.getImage().getWidth() * 0.6);
                img.setTranslateX(a);
                img.setTranslateY(b);

                challengeDisplay.getChildren().add(img);

                a+=60;
            }
            a=0;
            b += 60;
        }
    }

    private void makeWarning() {
        warning.setFill(Color.BLACK);
        warning.setCache(true);
        warning.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 80));
        warning.setLayoutX(BOARD_X - SQUARE_SIZE);
        warning.setLayoutY(400);
        warning.setTextAlignment(TextAlignment.CENTER);
        root.getChildren().add(warning);
    }

    private void makeInstructions() {

    }

    private void makeCompletion() {
        completionText.setFill(Color.BLACK);
        completionText.setCache(true);
        completionText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 80));
        completionText.setLayoutX(BOARD_X + 2 * SQUARE_SIZE);
        completionText.setLayoutY(400);
        completionText.setTextAlignment(TextAlignment.CENTER);
        root.getChildren().add(completionText);
    }

    private void showCompletion() {
        completionText.toFront();
        completionText.setOpacity(1);
    }

    private void hideCompletion() {
        completionText.toBack();
        completionText.setOpacity(0);
    }

    private void showWarning() {
        if(pieceMap.size() != 0){
            warning.toFront();
            warning.setOpacity(1);
        }
    }

    private void hideWarning() {
        warning.toBack();
        warning.setOpacity(0);
    }

    private void checkCompletion() {
        if(ps.toString().equals("a000b200")) {
            showCompletion();
        } else {
            hideCompletion();
            System.out.println(ps);
        }
    }

    private void resetGame() {
        pieces.getChildren().clear();
        pieceMap.clear();
        ps.clear();
    }

    private void makeControls() {
        /* Creates hashmap so only one of each tile can be created */
        int x = 0;

        for (char m = 'a'; m <= 'j'; m++) {
            Button bu = new Button(m + "");

            char p = m;

            bu.setLayoutX(BOARD_X + 60 + x);
            bu.setLayoutY(GAME_HEIGHT - 105);
            bu.setOnAction(e1 -> {
                if(pieceMap.containsKey(p)) {
                    pieceMap.get(p).returnHome();
                } else {
                    pieceMap.put(p, new DraggablePiece(p, BOARD_X + BOARD_WIDTH / 3, BOARD_Y + BOARD_HEIGHT + SQUARE_SIZE));
                }

                pieces.getChildren().clear();

                for (char o = 'a'; o <= 'j'; o++) {
                    if(pieceMap.get(o) != null){
                        pieces.getChildren().add(pieceMap.get(o));
                    }
                }
            });
            controls.getChildren().add(bu);
            x+= 40;
        }

        Button button = new Button("Restart");
        button.setLayoutX(BOARD_X + 240);
        button.setLayoutY(GAME_HEIGHT - 55);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                newGame();
            }
        });
        controls.getChildren().add(button);
    }

    private void newGame() {
        try {
            hideCompletion();
            getChallenge();
            displayChallenge(objective, BOARD_X + 3 * SQUARE_SIZE, BOARD_Y + SQUARE_SIZE);
            challengeDisplay.setOpacity(0.0);
        } catch (IllegalArgumentException e) {
            System.err.println("Uh oh. " + e);
            e.printStackTrace();
            Platform.exit();
        }
        resetGame();
    }

    // FIXME Task 10: Implement hints

    // FIXME Task 11: Generate interesting challenges (each challenge may have just one solution)

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("IQ-Focus");
        Scene scene = new Scene(root, GAME_WIDTH, GAME_HEIGHT);

        root.getChildren().add(board);
        root.getChildren().add(pieces);
        root.getChildren().add(controls);
        root.getChildren().add(challengeDisplay);

        makeBoard();
        makeControls();
        makeCompletion();
        makeWarning();

        setUpHandlers(scene);

        newGame();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
