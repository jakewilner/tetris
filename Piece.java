package tetris;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

public abstract class Piece {

    private ArrayList<Piece> pieceList;
    private int[][] coordinates;
    private Rectangle[][] board;
    private Color color;
    public static int score = 0;
    public static int high = 0;
    public static boolean gameOver = false;
    private Timeline animationTimeLine;
    private int counter;
    private Pane gamePane;
    private ArrayList<Rectangle> blockerList;




    /**
     * This abstract class represents the base for each piece
     * It controls the movement, collision checking, and scoring for the pieces
     * It is abstract, because it should never be instantiated, and leaves a method for its children to fill in
     * It is associated with the board and pieceList
     * Coordinates [i][0] stores the x-value and [i][1] stores the y-value
    */
/**
    *associates with board and pieceList
 */
    public Piece(Rectangle[][] myBoard, ArrayList<Piece> myPieceList, Timeline timeline, Pane pane) {
        this.pieceList = myPieceList;
        this.board = myBoard;
        this.gamePane = pane;
        this.blockerList = new ArrayList<>();
    }

    private void runAnimation(int line) {
            KeyFrame kf = new KeyFrame(Duration.seconds(.016),
                    (ActionEvent e) -> this.animation(line));
            this.animationTimeLine = new Timeline(kf);
            this.animationTimeLine.setCycleCount(36);
            this.animationTimeLine.play();
            this.counter = 0;
    }

    private void animation(int y) {
        switch (counter) {
            case 0:
                makeBlocker(y, 0);
                counter++;
                break;
            case 3:
                makeBlocker(y, 1);
                counter++;
                break;
            case 6:
                makeBlocker(y, 2);
                counter++;
                break;
            case 9:
                makeBlocker(y, 3);
                counter++;
                break;
            case 12:
                makeBlocker(y, 4);
                counter++;
                break;
            case 15:
                makeBlocker(y, 5);
                counter++;
                break;
            case 18:
                makeBlocker(y, 6);
                counter++;
                break;
            case 21:
                makeBlocker(y, 7);
                counter++;
                break;
            case 24:
                makeBlocker(y, 8);
                counter++;
                break;
            case 27:
                makeBlocker(y, 9);
                counter++;
                break;
            case 30:
                makeBlocker(y, 10);
                counter++;
                break;
            case 35:
                this.removeBlocker();
            default:
                counter++;
                break;
        }
    }


    /**
    *this method moves the piece down one square
     */
    public void move() {


        for (int i = 0; i < this.coordinates.length; i++) {
            this.clearBoard(this.coordinates[i]);
            this.coordinates[i][1]++; //setting coordinates one lower
        }
        if (shouldMove()) { //checks if space is free
            for (int i = 0; i < this.coordinates.length; i++) {
                this.setBoard(this.coordinates[i]); //sets piece to new coordinates
            }
        } else { //if space isn't free, move back and check for gameOver, remove from pieceList
            for (int i = 0; i < this.coordinates.length; i++) {
                this.coordinates[i][1]--;
                this.setBoard(this.coordinates[i]);
                this.pieceList.remove(this);
                if (this.coordinates[i][1] < Constants.PIECE_START_COORD) {
                    this.gameOver = true;
                    if (Piece.score > Piece.high) {
                        Piece.high = Piece.score;
                    }
                }
            }
            Piece.score += Constants.PIECE_POINTS; //increments score and checks lines
            this.checkLines();
        }
    }


    /**
  *This method checks all key presses
     */
    public void checkKey(KeyEvent e, Boolean running) { //more key events, called by Game class
        if (running) { //ensures game is running before checking key input
            if (e.getCode() == KeyCode.UP) { //rotation
                this.rotate();
            } else if (e.getCode() == KeyCode.LEFT) { //shift left
                this.shift(-1);
            } else if (e.getCode() == KeyCode.RIGHT) { //shift right
                this.shift(1);
            } else if (e.getCode() == KeyCode.DOWN) { //shift down
                this.move();
            } else if (e.getCode() == KeyCode.SPACE) { //drop all the way down
                this.drop();
            }
        }
    }
/**
    *depending on direction inputted, move either right of left one square
 */
    private void shift(int dir) {
        if (this.pieceList.contains(this)) { //sets coordinates
            for (int i = 0; i < this.coordinates.length; i++) {
                this.clearBoard(this.coordinates[i]);
                this.coordinates[i][0] += dir;
            }
            if (shouldMove()) { //checks if new space is clear
                for (int i = 0; i < this.coordinates.length; i++) {
                    this.setBoard(this.coordinates[i]); //sets board
                }
            } else { //otherwise, moves back and resets board
                for (int i = 0; i < this.coordinates.length; i++) {
                    this.coordinates[i][0] -= dir;
                    this.setBoard(this.coordinates[i]);
                }
            }
        }
    }

/**
    *rotates the piece
 */
    public void rotate() {
        if (this.pieceList.contains(this)) {
            for (int i = 0; i < this.coordinates.length; i++) { //clears the board
                this.clearBoard(this.coordinates[i]);
            }
            for (int i = 1; i < this.coordinates.length; i++) { //sets new coordinates
                int tempX = this.coordinates[i][0];
                int tempY = this.coordinates[i][1];
                this.coordinates[i][0] = this.coordinates[0][0] - this.coordinates[0][1] + tempY;
                this.coordinates[i][1] = this.coordinates[0][0] + this.coordinates[0][1] - tempX;
            }
            if (shouldMove()) { //checks if new space is clear
                for (int i = 0; i < this.coordinates.length; i++) {
                    this.setBoard(this.coordinates[i]); //moves piece
                }
            } else { //unrotates piece, and sets board
                for (int i = 1; i < this.coordinates.length; i++) {
                    int tempX = this.coordinates[i][0];
                    int tempY = this.coordinates[i][1];
                    this.coordinates[i][1] = this.coordinates[0][1] - this.coordinates[0][0] + tempX;
                    this.coordinates[i][0] = this.coordinates[0][1] + this.coordinates[0][0] - tempY;
                    this.setBoard(this.coordinates[i]);
                }
                this.setBoard(this.coordinates[0]);
            }
        }
    }

    /**
    *checks if movement space is clear with current coordinates
     */
    private Boolean shouldMove() {
        for (int i = 0; i < this.coordinates.length; i++) {
            if (this.coordinates[i][1] > 23 || this.coordinates[i][0] < 0 || this.coordinates[i][0] > 9 ||
                    this.board[this.coordinates[i][0]][this.coordinates[i][1]].getFill() != Color.BLACK) {
                //uses color to check if the area is clear
                return false;
            }
        }
        return true;
    }

/**
    *checks to see if lines are full, clears them and updates score
 */
    private void checkLines() {
        ArrayList<Integer> clearList = new ArrayList<>();
        int multiplier = 0;
        for (int i = 23; i > 0; i--) {
            Boolean flag = false;
            for (int j = 0; j < 10; j++) {
                if (this.board[j][i].getFill() == Color.BLACK) {
                    flag = true; //if line is not full, flag is true
                }
            }
            if (!flag) { //if line is full, clear it and move all above down
                multiplier++;
                this.updateIncrementingKeyFrame(); //updates increment keyframe
         //       clearList.add(i);
              //  this.runAnimation(i);
                updateClear(i);
                i++;
            }
        }
        this.updateScore(multiplier); //updates score depending on rows cleared
        for (Integer integer: clearList){
            this.runAnimation(integer);
        }
        for (Integer integer: clearList){
            this.updateClear(integer);
        }
    }

    /**
     *this method occurs when a row is cleared
    */
    public void updateClear(int i) {
        for (int k = i; k > 0; k--) {
            for (int j = 0; j < 10; j++) {
                this.board[j][k].setFill(this.board[j][k-1].getFill());
            }
        }
    }

    private void makeBlocker(int row, int i) {
        Rectangle blockerTile = new Rectangle(Constants.SQUARE_SIZE*i + Constants.SQUARE_SPACE*(i+1),
                Constants.SQUARE_SIZE*(row-4) + Constants.SQUARE_SPACE*(row-4),
                Constants.SQUARE_SIZE, Constants.SQUARE_SIZE);
            blockerTile.setFill(Color.WHITE);
            blockerList.add(blockerTile);
            this.gamePane.getChildren().add(blockerTile);
    }

    private void removeBlocker() {
        for (Rectangle block: blockerList) {
            this.gamePane.getChildren().removeAll(block);
        }
    }

    /**
    *updates the incrementing key frame depending on how many rows cleared
     */
    public void updateIncrementingKeyFrame(){
        if (PaneOrganizer.incrementingKeyFrame > Constants.INCREMENTING_START_KF) {
            PaneOrganizer.incrementingKeyFrame = PaneOrganizer.incrementingKeyFrame - Constants.INCREMENTING_START_PACE;
        } else if
        (Constants.INCREMENTING_SPEED2_KF < PaneOrganizer.incrementingKeyFrame &&
                        PaneOrganizer.incrementingKeyFrame < Constants.INCREMENTING_START_KF ) {
            PaneOrganizer.incrementingKeyFrame =
                    PaneOrganizer.incrementingKeyFrame - Constants.INCREMENTING_SPEED2_PACE;
        } else if
        (Constants.INCREMENTING_SPEED3_KF < PaneOrganizer.incrementingKeyFrame &&
                        PaneOrganizer.incrementingKeyFrame < Constants.INCREMENTING_SPEED2_KF ) {
            PaneOrganizer.incrementingKeyFrame =
                    PaneOrganizer.incrementingKeyFrame - Constants.INCREMENTING_SPEED3_PACE;
        } else if
            (Constants.MIN_KF < PaneOrganizer.incrementingKeyFrame &&
                        PaneOrganizer.incrementingKeyFrame < Constants.INCREMENTING_SPEED3_KF ) {
            PaneOrganizer.incrementingKeyFrame =
                    PaneOrganizer.incrementingKeyFrame - Constants.INCREMENTING_SPEED3_PACE;
        } else {
            PaneOrganizer.incrementingKeyFrame = Constants.MIN_KF;
        }
    }

/**
    //updates the score depending on the number of lines cleared at once
 */
    public void updateScore(int mult) {
        switch (mult) {
            case 1:
                Piece.score += Constants.ONE_ROW_POINTS;
                break;
            case 2:
                Piece.score += Constants.TWO_ROW_POINTS;
                break;
            case 3:
                Piece.score += Constants.THREE_ROW_POINTS;
                break;
            case 4:
                Piece.score += Constants.FOUR_ROW_POINTS;
                break;
            default:
                break;
        }
    }

/**
    *clears the board
 */
    public void delete() {
        for (int i = 0; i < this.coordinates.length; i++) {
            this.clearBoard(this.coordinates[i]);
        }
    }

/**
    *moves piece all the way to the bottom
 */
    private void drop() {
        while (this.pieceList.contains(this)) { this.move(); }
    }

/**
    *sets the coordinates, used by children
 */
    public void setCoordinates(int[][] myCoordinates) {
        this.coordinates = myCoordinates; }

/**
    *sets color, used by children
 */
    public void setColor(Color myColor) {
        this.color = myColor;
    }

/**
    *sets the board to color at a given x,y
 */
    public void setBoard(int[] pos) {
        this.board[pos[0]][pos[1]].setFill(this.color);
    }

/**
    *clears the board at a given x,y
 */
    private void clearBoard(int[] pos) {
        this.board[pos[0]][pos[1]].setFill(Color.BLACK);
    }
/**
    *returns an integer representing the piece, used by child classes
 */
    public abstract int getCase();
}