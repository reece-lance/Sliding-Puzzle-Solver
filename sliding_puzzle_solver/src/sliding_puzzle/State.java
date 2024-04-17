package sliding_puzzle;

/*
This class represents each state the board can be in.
For every possible move, a State object is created.
 */

import java.util.ArrayList;
import java.util.Arrays;

public class State {
    final int[][] board; // Current board
    private int[] spacePos = new int[2]; // The coordinates of the space in board
    static int rowSize; // X-dimension of board
    static int columnSize;// Y-dimension of board

    /*
    Constructor used to create initial state
     */
    public State(int[][] board) {
        this.board = board;
        rowSize = board.length;
        columnSize = board[0].length;
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                if (board[i][j] == 0) { // Checks if element is a space
                    this.spacePos[0] = i; // Set x coordinate of space
                    this.spacePos[1] = j; // Set y coordinate of space
                    break;
                }
            }
        }
    }

    /*
    Constructor used to create a new game state from a previous game state and set new values corresponding to the move made
    */
    public State(State state, int i, int j) {
        this.board = Arrays.copyOf(state.board, rowSize); // Deep copy creation of the array board initialised
        for (int k = 0; k < rowSize; k++) {
            board[k] = Arrays.copyOf(state.board[k], columnSize); // Deep copy creation of each row in board
        }
        this.spacePos = Arrays.copyOf(state.spacePos, spacePos.length); // Deep copy of spacePos array created and initialised

        board[spacePos[0]][spacePos[1]] = board[i][j]; // Fill space
        board[i][j] = 0; // New space position
        spacePos[0] = i;
        spacePos[1] = j;
    }

    /*
    Compares 2 game states and checks if they are the same
     */
    public boolean sameBoard(int[][] newBoard) {
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                if (board[i][j] != newBoard[i][j]) { // If any positions are not the same
                    return false;
                }
            }
        }
        return true; // If both boards are the same
    }

    /*
    Calculates Manhatten Distance between 2 sets of coordinates
     */
    static public int calculateDistance(int tile1x, int tile1y, int tile2x, int tile2y) {
        return (Math.abs(tile1x - tile2x) + Math.abs(tile1y - tile2y));
    }

    /*
    Calculates all next possible moves for the current state
    Creates new state for every move and adds them to a move list
    */
    public ArrayList<State> possibleMoves() {
        ArrayList<State> moves = new ArrayList<>();
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                int distance = calculateDistance(spacePos[0], spacePos[1], i, j); // Call method to calculate distance between 2 spaces
                if (distance == 1) { // If move is legal
                    moves.add(new State(this, i, j)); //add new state for the found possible move
                }
            }
        }
        return moves;
    }
}