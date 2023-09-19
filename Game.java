package tetris;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.nio.file.Paths;
import java.util.ArrayList;

public class Game {

    private Rectangle[][] board; //rectangles representing board
    private Pane gamePane;
    private VBox infoPane;
    private ArrayList<Piece> pieceList; //stores the current piece
    private ArrayList<Integer> nextList; //stores integers representing the next pieces
    private Timeline timeline;
    private ImageView[] info;
    private Label scoreLabel;
    private Label highScoreLabel;
    private Boolean running;
    private double duration;
    private Rectangle endGameRect;
    private Rectangle pauseGameRect1;
    private Rectangle pauseGameRect2;
    private Rectangle pauseGameRect3;
    private Label endGameLabel;
    private Label incrementingLevel;
    private Label holdLabel;
    private Label nextLabel;
    private int level;
    private Rectangle incrementingLevelRect;
    private int hold; //stores integer representing the held piece
    private int holdUsed; //detects if hold has already been used for the current piece


    /**
     * The game class controls much of the logic and timeline, and makes the graphical elements of the game run
     * It is associated with gamePane, the score labels and the infoPane
     */
    public Game(Pane myGamePane, Label myScore, Label myHigh, VBox myInfoPane) {
        this.hold = -1;
        this.holdUsed = 1;
        this.running = true;
        this.board = new Rectangle[Constants.BOARD_SQUARE_WIDTH][Constants.BOARD_SQUARE_HEIGHT];
        this.gamePane = myGamePane;
        this.infoPane = myInfoPane;
        this.scoreLabel = myScore;
        this.holdLabel = new Label("Hold:");
        this.holdLabel.setTextFill(Color.WHITESMOKE);
        this.nextLabel = new Label("\n\n\nUp Next:");
        this.nextLabel.setTextFill(Color.WHITESMOKE);
        this.highScoreLabel = myHigh;
        this.pieceList = new ArrayList<>();
        this.nextList = new ArrayList<>();
        this.info = new ImageView[4];
        this.info[0] = generateInfo(-1);
        this.info[0].setPreserveRatio(true);
        this.info[0].setFitHeight(Constants.INFO_PANE_HEIGHT);
        this.gamePane.setOnKeyPressed((KeyEvent e) -> this.checkKey(e));
        this.generateNext();
        this.setUpBoard();
        this.setUpTimeline();
        this.updateInfo();
        this.level = (int) (1 / PaneOrganizer.incrementingKeyFrame - 1);
        if (PaneOrganizer.incrementingKeyFrame != Constants.MIN_KF) {
            this.incrementingLevel = new Label("level: " + level);
        } else {
            this.incrementingLevel = new Label("level: death");
            this.incrementingLevel.setTextFill(Color.DARKRED);
        }
    }
    /**
     *prepares the board, by filling it with black rectangles
 */
    private void setUpBoard() {
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                this.board[i][j] = new Rectangle(Constants.SQUARE_SIZE, Constants.SQUARE_SIZE);
                this.board[i][j].setFill(Color.BLACK);
                this.board[i][j].setX(Constants.SQUARE_SIZE*i + Constants.SQUARE_SPACE*(i+1));
                this.board[i][j].setY(Constants.SQUARE_SIZE*(j-4) + Constants.SQUARE_SPACE*(j-4));
                this.gamePane.getChildren().add(this.board[i][j]);
            }
        }
    }


/**
    *sets up the timeline, with a frame length of duration
 */
    private void setUpTimeline() {
        KeyFrame kf = new KeyFrame(Duration.seconds(this.duration),
                (ActionEvent e) -> this.runKeyframe());
        this.timeline = new Timeline(kf);
        this.timeline.setCycleCount(Animation.INDEFINITE);
        if (this.running) { this.timeline.play(); }
    }
/**
    *all actions performed during each keyFrame
 */
    private void runKeyframe() {
        this.generateNext();
            if (this.pieceList.isEmpty()) {
                this.newPiece(this.nextList.get(0));
                this.holdUsed--;
            }
            for (int i = 0; i < pieceList.size(); i++) {
                this.pieceList.get(i).move();
            }
            if (PaneOrganizer.isIncrementingMode) {
                setDuration(PaneOrganizer.incrementingKeyFrame);
            }
            this.updateLabel();
            this.addGameOver();
            this.updateIncrementingLabel();
    }
/**
    *takes in an integer, and uses it to determine what piece to generate
 */
    private void newPiece(int num) {
        switch (num) {
            case 0:
                this.pieceList.add(new IBlock(this.board, this.pieceList, timeline, gamePane));
                break;
            case 1:
                this.pieceList.add(new ZBlock(this.board, this.pieceList, timeline, gamePane));
                break;
            case 2:
                this.pieceList.add(new TBlock(this.board, this.pieceList, timeline, gamePane));
                break;
            case 3:
                this.pieceList.add(new JBlock(this.board, this.pieceList, timeline, gamePane));
                break;
            case 4:
                this.pieceList.add(new LBlock(this.board, this.pieceList, timeline, gamePane));
                break;
            case 5:
                this.pieceList.add(new SBlock(this.board, this.pieceList, timeline, gamePane));
                break;
            default:
                this.pieceList.add(new OBlock(this.board, this.pieceList, timeline, gamePane));
                break;
        }
        this.nextList.remove(0);
        this.updateInfo();
    }
/**
    *Clears the info pane, then updates it
 */
    private void updateInfo() {
        this.infoPane.getChildren().removeAll(this.infoPane.getChildren());
        this.infoPane.getChildren().addAll(this.info[0], this.holdLabel, this.nextLabel);
        this.info[0].setPreserveRatio(true);
        this.info[0].setFitHeight(Constants.INFO_PANE_HEIGHT);
        //info[0] stores the int value of the hold piece
        for (int i = 1; i < 4; i++) {
            //runs through the first 3 upcoming pieces, and sets them depending on values of numList
            this.info[i] = generateInfo(this.nextList.get(i-1));
            this.info[i].setPreserveRatio(true);
            this.info[i].setFitHeight(Constants.INFO_PANE_HEIGHT);
            this.infoPane.getChildren().add(this.info[i]);
        }
        this.nextLabel.toBack();
        this.info[0].toBack();
        this.holdLabel.toBack();
        //re-orders pane
    }
/**
   * returns the image associated with a given integer
 */
    private ImageView generateInfo(int num) {
        switch (num) {
            case 0:
                return new ImageView(new Image(this.getClass().getResourceAsStream("I piece.png")));
            case 1:
                return new ImageView(new Image(this.getClass().getResourceAsStream("Z piece.png")));
            case 2:
                return new ImageView(new Image(this.getClass().getResourceAsStream("T piece.png")));
            case 3:
                return new ImageView(new Image(this.getClass().getResourceAsStream("J piece.png")));
            case 4:
                return new ImageView(new Image(this.getClass().getResourceAsStream("L piece.png")));
            case 5:
                return new ImageView(new Image(this.getClass().getResourceAsStream("S piece.png")));
            case 6:
                return new ImageView(new Image(this.getClass().getResourceAsStream("O piece.png")));
            default:
                return new ImageView(new Image(this.getClass().getResourceAsStream("Black.png")));
        }
    }
/**
    *if the game over condition is true, pull up the end game labels and stop the timeline
 */
    private void addGameOver() {
        if (Piece.gameOver) {
            this.timeline.stop();
            this.endGameRect = new Rectangle(0, 0, Constants.END_GAME_RECT_WIDTH,
                    Constants.END_GAME_RECT_HEIGHT);
            this.endGameRect.setFill(Color.BLACK);
            this.endGameRect.setOpacity(Constants.END_GAME_RECT_OPACITY);
            this.endGameLabel = new Label("Game Over");
            this.endGameLabel.setTextFill(Color.WHITE);
            this.endGameLabel.setScaleX(Constants.END_GAME_LABEL_SCALE);
            this.endGameLabel.setScaleY(Constants.END_GAME_LABEL_SCALE);
            this.endGameLabel.setLayoutX(Constants.END_GAME_LABEL_LAYOUT_X);
            this.endGameLabel.setLayoutY(Constants.END_GAME_LABEL_LAYOUT_Y);
            this.gamePane.getChildren().addAll(this.endGameRect, this.endGameLabel);
        }
    }
/**
    *updates the score label as score changes
 */
    public void updateLabel() {
        this.scoreLabel.setText("Score:\n" + Piece.score);
        this.scoreLabel.setFont(Font.font("Helvetica"));
        this.highScoreLabel.setFont(Font.font("Helvetica"));
        if (Piece.score > Piece.high) {
            Piece.high = Piece.score;
            this.highScoreLabel.setText("High Score:\n"+Piece.high);
        }
    }
/**
 *updates the incrementing difficulty label as difficulty increases (incrementing mode)
 */
    public void updateIncrementingLabel() {
        if (PaneOrganizer.isIncrementingMode) {
            switch (this.level) {
                case -1:
                    this.incrementingLevel.setOpacity(0);
                case 1: this.incrementingLevel.setTextFill(Color.WHITE);
                    break;
                case 2:
                    this.incrementingLevel.setTextFill(Color.LIGHTBLUE);
                    break;
                case 3:
                    this.incrementingLevel.setTextFill(Color.LIGHTGREEN);
                    break;
                case 4:
                    this.incrementingLevel.setTextFill(Color.MEDIUMPURPLE);
                    break;
                case 5:
                    this.incrementingLevel.setTextFill(Color.ORANGE);
                    break;
                case 6:
                    this.incrementingLevel.setTextFill(Color.RED);
                    break;
                default:
                    this.incrementingLevel.setTextFill(Color.DARKRED);
                    break;
            }
            this.incrementingLevelRect = new Rectangle(0, 0, Constants.INCREMENTING_LEVEL_RECT_WIDTH,
                    Constants.INCREMENTING_LEVEL_RECT_HEIGHT);
            //backdrop for difficulty label
            this.incrementingLevelRect.setFill(Color.BLACK);
            if (!this.gamePane.getChildren().contains(this.incrementingLevelRect) && !this.gamePane.getChildren().contains(this.incrementingLevel)){
            this.gamePane.getChildren().addAll(this.incrementingLevelRect, this.incrementingLevel); }
        } else {
            this.gamePane.getChildren().removeAll(this.incrementingLevelRect, this.incrementingLevel);
        }
    }
/**
 *handles all keyEvents
 */
    private void checkKey(KeyEvent e) {
        if (e.getCode() == KeyCode.P && this.running) { //pauses the game
            this.timeline.stop();
            this.pauseRectangles();
            this.running = false;
        }
        else if (e.getCode() == KeyCode.P) { //un-pauses the game
            if (this.running) {
                this.timeline.play();
                this.gamePane.getChildren().removeAll(this.pauseGameRect1, this.pauseGameRect2, this.pauseGameRect3);
                this.running = true;
            }
        }
        else if (e.getCode() == KeyCode.C && this.running) { //triggers hold
            if (this.hold != -1 && this.holdUsed < 1) {
                this.nextList.add(0, this.hold);
                this.orderInfo();
                this.holdUsed = 2;
            }
            else if (this.hold == -1) { //hold condition for initial piece
                this.infoPane.getChildren().remove(info[0]);
                this.orderInfo();
                this.holdUsed = 2;
            }
        }
        //checks keyEvent in Piece
        if (!this.pieceList.isEmpty()) { this.pieceList.get(0).checkKey(e, this.running); }
        e.consume();
    }
/**
    *puts info pane in order
 */
    private void orderInfo() {
        this.hold = this.pieceList.get(0).getCase();
        this.pieceList.get(0).delete();
        this.pieceList.remove(0);
        this.info[0] = generateInfo(this.hold);
        this.info[0].setPreserveRatio(true);
        this.info[0].setFitHeight(125);
        this.updateInfo();
    }
/**
 *sets the duration of the keyFrame
 */
    public void setDuration(double dur) {
        this.duration = dur;
        if (!Piece.gameOver) {
            this.timeline.stop();
            this.setUpTimeline();
        }
    }
/**
*bag random algorithm: generates new pieces, 7 at a time, one of each type, and randomizes them
 */
    private void generateNext() {
        if (this.nextList.size() < 5) {
            int[] tempList = {0, 1, 2, 3, 4, 5, 6};
            for (int i = 0; i < Constants.SHUFFLER; i++) {
                int index1 = (int)(Math.random() * 7);
                int index2 = (int)(Math.random() * 7);
                int temp = tempList[index1];
                tempList[index1] = tempList[index2];
                tempList[index2] = temp;
            }
            for (int i = 0; i < tempList.length; i++) {
                this.nextList.add(tempList[i]);
            }
        }
    }
/**
*renders the pause icon when called
 */
    private void pauseRectangles() {
        this.pauseGameRect1 = new Rectangle(Constants.PAUSE_BUTTON1_X, Constants.PAUSE_BUTTON1_Y,
                Constants.PAUSE_BUTTON_RECTS_WIDTH, Constants.PAUSE_BUTTON_RECTS_HEIGHT);
        this.pauseGameRect2 = new Rectangle(Constants.PAUSE_BUTTON2_X, Constants.PAUSE_BUTTON2_Y,
                Constants.PAUSE_BUTTON_RECTS_WIDTH, Constants.PAUSE_BUTTON_RECTS_HEIGHT);
        this.pauseGameRect3 = new Rectangle(0, 0,
                Constants.PAUSE_GAME_RECT3_WIDTH, Constants.PAUSE_GAME_RECT3_HEIGHT);
        this.pauseGameRect1.setOpacity(Constants.PAUSE_BUTTONS_OPACITY);
        this.pauseGameRect1.setFill(Color.WHITESMOKE);
        this.pauseGameRect2.setOpacity(Constants.PAUSE_BUTTONS_OPACITY);
        this.pauseGameRect2.setFill(Color.WHITESMOKE);
        this.pauseGameRect3.setFill(Color.BLACK);
        this.pauseGameRect3.setOpacity(Constants.PAUSE_HIDER_OPACITY);
        this.gamePane.getChildren().addAll(this.pauseGameRect1, this.pauseGameRect2, this.pauseGameRect3);
    }


/**
    *resets the game
 */
    public void reset() {
        this.timeline.stop();
        this.timeline = null;
        this.board = null;
        Piece.gameOver = false;
        this.nextList = new ArrayList<>();
        this.pieceList = new ArrayList<>();
        this.generateNext();
    }
}