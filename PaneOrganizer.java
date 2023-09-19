package tetris;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;

/**
 * PaneOrganizer class sets up the panes involved in the project as well as the start screen. It also includes
 * the actions for each button press.
 */

public class PaneOrganizer {

    private BorderPane root;
    private Game game;
    private Pane gamePane;
    private VBox buttonPane;
    private VBox infoPane;
    private Label scoreLabel;
    private Label highScore;
    private Button quit;
    private Button easy;
    private Button med;
    private Button hard;
    private Button incrementing;
    private Button restart;
    private Button mute;
    public static double incrementingKeyFrame = Constants.INCREMENTING_STARTING_KF;
    public static boolean isIncrementingMode = false;
    private Rectangle startScreenBase;
    private ImageView startScreenImageView;
    private ImageView clickImageView;
    private boolean starting;
    private boolean muted;
    private Media song;
    private MediaPlayer songPlayer;

    public PaneOrganizer() {
        String path = "src/tetris/tetristheme.mp3";
        this.song = new Media(new File(path).toURI().toString());
        this.songPlayer = new MediaPlayer(song);
        songPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        this.starting = true;
        this.root = new BorderPane();
        this.startScreenBase = new Rectangle(Constants.START_SCREEN_BASE_WIDTH, Constants.START_SCREEN_BASE_HEIGHT);
        this.startScreenBase.setFill(Color.BLACK);
        this.startScreenImageView = new ImageView(
                new Image(this.getClass().getResourceAsStream("start screen.png")));
        this.clickImageView = new ImageView(
                new Image(this.getClass().getResourceAsStream("T starter.png")));
        this.startScreenImageView.setPreserveRatio(true);
        this.clickImageView.setPreserveRatio(true);
        this.startScreenImageView.setFitWidth(Constants.START_IMAGE_WIDTH);
        this.clickImageView.setFitWidth(Constants.START_IMAGE_WIDTH);
        this.startScreenImageView.setY(Constants.START_IMAGE_Y);
        this.clickImageView.setY(Constants.CLICKIMAGE_Y);
        this.root.getChildren().addAll(this.startScreenBase, this.startScreenImageView, this.clickImageView);
        this.root.setOnMouseClicked((MouseEvent e) -> this.playGame());
    }
    /**
     *This method contains all the graphical elements for when the game is started (when the mouse is pressed)
     */
    private void playGame() {
        if (this.starting) {
            songPlayer.play();
            this.starting = false;
            this.root.getChildren().removeAll(this.startScreenBase, this.startScreenImageView);
            this.gamePane = new Pane();
            this.infoPane = new VBox();
            this.scoreLabel = new Label();
            this.scoreLabel.setTextFill(Color.WHITESMOKE);
            this.scoreLabel.setText("Score:\n0");
            this.highScore = new Label();
            this.highScore.setTextFill(Color.WHITESMOKE);
            this.highScore.setText("High Score:\n0");
            this.game = new Game(this.gamePane, this.scoreLabel, this.highScore, this.infoPane);
            this.setUpPanes();
        }
    }


    /**
     *This method sets up all the panes within the game
     */
    private void setUpPanes() {
        this.gamePane.setStyle(Constants.GAME_PANE_COLOR);
        this.buttonPane = new VBox();
        this.buttonPane.setStyle(Constants.BUTTON_PANE_COLOR);
        this.infoPane.setStyle(Constants.INFO_PANE_COLOR);
        this.infoPane.setMinWidth(Constants.INFO_PANE_WIDTH);
        this.infoPane.setAlignment(Pos.CENTER);
        this.root.setLeft(this.infoPane);
        this.root.setCenter(this.gamePane);
        this.root.setRight(this.buttonPane);
        this.addButtons();
        this.gamePane.setFocusTraversable(true);
    }

    /**
     *This adds each of the buttons within the button pane on the right
     */
    private void addButtons() {
        this.quit = new Button("Quit");
        this.quit.setOnAction((ActionEvent e) -> System.exit(0));
        this.quit.setStyle(Constants.QUIT_BUTTON_COLOR);
        this.restart = new Button("Restart");
        this.restart.setOnAction((ActionEvent e) -> this.reset());
        this.restart.setStyle(Constants.RESTART_BUTTON_COLOR);
        this.easy = new Button("Easy");
        this.easy.setOnAction((ActionEvent e) -> this.easyAction());
        this.med = new Button("Medium");
        this.med.setOnAction((ActionEvent e) -> this.medAction());
        this.hard = new Button("Hard");
        this.hard.setOnAction((ActionEvent e) -> this.hardAction());
        this.incrementing = new Button("Incrementing");
        this.incrementing.setOnAction((ActionEvent e) -> this.incrementingAction());
        this.mute = new Button("Mute \nTheme");
        this.mute.setAlignment(Pos.BASELINE_CENTER);
        this.mute.setStyle(Constants.PASSIVE_BUTTON_STYLE);
        this.mute.setOnAction((ActionEvent e) -> this.muteAction());
        this.muted = false;
        this.easyAction();
        this.setUpGraphically();
    }

    /**
     *This method dictates what occurs when the mute button is pressed
     */
    private void muteAction() {
        if (!muted) {
            this.mute.setText("Unmute \nTheme");
            this.mute.setStyle(Constants.ACTIVE_BUTTON_STYLE);
            this.muted = true;
            this.songPlayer.setMute(true);
        } else {
            this.mute.setText("Mute \nTheme");
            this.mute.setStyle(Constants.PASSIVE_BUTTON_STYLE);
            this.muted = false;
            this.songPlayer.setMute(false);
        }
    }

    /**
     *This method dictates what occurs when the easy mode button is pressed
     */
    private void easyAction() {
        this.isIncrementingMode = false;
        this.game.setDuration(Constants.EASY_KF);
        this.easy.setStyle(Constants.ACTIVE_BUTTON_STYLE);
        this.med.setStyle(Constants.PASSIVE_BUTTON_STYLE);
        this.hard.setStyle(Constants.PASSIVE_BUTTON_STYLE);
        this.incrementing.setStyle(Constants.PASSIVE_BUTTON_STYLE);
    }

    /**
     *This method dictates what occurs when the medium mode button is pressed
     */
    private void medAction() {
        this.isIncrementingMode = false;
        this.game.setDuration(Constants.MED_KF);
        this.med.setStyle(Constants.ACTIVE_BUTTON_STYLE);
        this.easy.setStyle(Constants.PASSIVE_BUTTON_STYLE);
        this.hard.setStyle(Constants.PASSIVE_BUTTON_STYLE);
        this.incrementing.setStyle(Constants.PASSIVE_BUTTON_STYLE);
    }

    /**
     *This method dictates what occurs when the hard mode button is pressed
     */
    private void hardAction() {
        this.isIncrementingMode = false;
        this.game.setDuration(Constants.HARD_KF);
        this.hard.setStyle(Constants.ACTIVE_BUTTON_STYLE);
        this.med.setStyle(Constants.PASSIVE_BUTTON_STYLE);
        this.easy.setStyle(Constants.PASSIVE_BUTTON_STYLE);
        this.incrementing.setStyle(Constants.PASSIVE_BUTTON_STYLE);
    }

    /**
     *This method dictates what occurs when the incrementing mode button is pressed
     */
    private void incrementingAction() {
        this.game.setDuration(this.incrementingKeyFrame);
        this.isIncrementingMode = true;
        this.incrementing.setStyle(Constants.ACTIVE_BUTTON_STYLE);
        this.hard.setStyle(Constants.PASSIVE_BUTTON_STYLE);
        this.med.setStyle(Constants.PASSIVE_BUTTON_STYLE);
        this.easy.setStyle(Constants.PASSIVE_BUTTON_STYLE);
    }

    /**
     *This sets up the elements of the game graphically.
     */
    private void setUpGraphically() {
        this.buttonPane.getChildren().addAll(
                this.mute, this.restart, this.quit, this.easy, this.med, this.hard, this.incrementing, this.scoreLabel,
                this.highScore);
        this.buttonPane.setAlignment(Pos.CENTER);
        this.buttonPane.setSpacing(Constants.BUTTON_PANE_SPACING);
        this.easy.setFocusTraversable(false);
        this.med.setFocusTraversable(false);
        this.hard.setFocusTraversable(false);
        this.quit.setFocusTraversable(false);
        this.incrementing.setFocusTraversable(false);
        this.buttonPane.setFocusTraversable(false);
    }

    /**
     *This method returns the root
     */
    public BorderPane getRoot() {
        return this.root;
    }

    /**
     *This resets the graphical elements when the game is restarted
     */
    private void reset() {
        Piece.score = 0;
        this.scoreLabel.setTextFill(Color.WHITESMOKE);
        this.scoreLabel.setText("Score:\n0");
        this.incrementingKeyFrame = Constants.INCREMENTING_STARTING_KF;
        this.game.reset();
        this.gamePane.getChildren().removeAll(this.gamePane.getChildren());
        this.infoPane.getChildren().removeAll(this.infoPane.getChildren());
        this.root.getChildren().removeAll(this.root.getChildren());
        this.game = new Game(this.gamePane, this.scoreLabel, this.highScore, this.infoPane);
        this.setUpPanes();
    }
}
