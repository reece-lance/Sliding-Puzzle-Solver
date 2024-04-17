package sliding_puzzle;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Solve {
    static int[][] initialBoard;
    static int[][] goalBoard;
    ArrayList<Node> unexpanded = new ArrayList<>();
    ArrayList<Node> expanded = new ArrayList<>();
    Node rootNode;

    /*
    Constructor used to create initial game state and it's node in the search tree.
     */
    public Solve(int[][] initialBoard, int[][] goalBoard) {
        Solve.initialBoard = initialBoard;
        Solve.goalBoard = goalBoard;
        State initialState = new State(initialBoard);
        rootNode = new Node(initialState);
    }

    /*
    Implements search strategy
    Searches through all required nodes until a solution is found
     */
    public void solve(PrintWriter output, SolvingPopUp popUp) {
        unexpanded.add(rootNode); // Initialise the unexpanded node list with the root node.
        while (unexpanded.size() > 0) {
            Node n = unexpanded.get(0);
            for (Node q : unexpanded) {
                if (q.getCost() < n.getCost()) {
                    n = q;
                }
            }
            expanded.add(n); // Add node to the expanded node list
            unexpanded.remove(n); // Remove node from the unexpanded node list
            if (n.state.sameBoard(goalBoard)) { // If the node equals goal state then:
                reportSolution(n, output, popUp); // Write solution to a file and end search
                return;
            } else {
                ArrayList<State> moveList = n.state.possibleMoves(); // Get list of permitted moves
                for (State gs : moveList) {
                    if ((Node.findNodeWithState(unexpanded, gs) == null) &&   // If node not in expanded
                            (Node.findNodeWithState(expanded, gs) == null)) { // or unexpanded lists:
                        int newDepth = n.getDepth()+ 1; // Set new depth as current node depth +1
                        if (newDepth == 100) {
                            popUp.setVisible(true);
                            popUp.showText("No solution within 100 moves");
                            return;
                        }
                        Node newNode = new Node(gs, n, newDepth, newDepth); // Create new Node setting parent as the current node. Cost is also the depth.
                        newNode.applyHeuristic();
                        unexpanded.add(newNode); // Add new node to unexpanded list
                    }
                }
            }
        }
        popUp.setVisible(true);
        popUp.showText("No solution");
    }
    /*
    Runs through full solution and outputs each game state to the text file in correct format using recursion
     */
    public int[] printSolution(Node n, int[] solution) {
        if (n.parent != null) { // If current node has a parent node:
            solution = printSolution(n.parent, solution);
            for (int x = 0; x < State.rowSize; x++) {
                for (int y = 0; y < State.columnSize; y++) {
                    if (n.parent.state.board[x][y] == 0) {
                        solution[n.parent.getDepth()] = n.state.board[x][y];
                    }
                }
            }
        }
        return solution;
    }

    /*
    Outputs search statistics to the user, only if a solution is found.
    States the solution, the number of moves and how many nodes were expanded and unexpanded
     */
    public void reportSolution(Node n, PrintWriter output, SolvingPopUp popUp) {
        String outputString = "";
        outputString += "Solved in " + n.getDepth() + " moves!\n\n";
        int[] solution = new int[n.getDepth()];
        outputString += Arrays.toString(printSolution(n, solution)) + "\n\n";
        outputString += "Nodes expanded: " + this.expanded.size() + "\n";
        outputString += "Nodes unexpanded: " + this.unexpanded.size() + "\n";
        output.println(outputString);
        output.close(); // Close the PrintWriter to ensure output is produced
        popUp.setVisible(true);
        popUp.showText(outputString);
    }
}
