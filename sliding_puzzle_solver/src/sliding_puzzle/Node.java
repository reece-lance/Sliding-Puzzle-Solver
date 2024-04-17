package sliding_puzzle;

import java.util.ArrayList;

/*
The class Node represents nodes.
Its essential features are a GameState and a reference to the node's parent node.
The cost and depth are used in the Solver class when deciding which node should next be expanded.
This class was taken from my own assignment in AI.
 */

public class Node {
    State state; // The state associated with the node
    Node parent; // The node from which this node was reached
    private int cost; // The cost of reaching this node from the initial node
    final private int DEPTH; // How many nodes are before this

    /*
      Constructor used to create new nodes.
     */
    public Node(State state, Node parent, int cost, int DEPTH) {
        this.state = state;
        this.parent = parent;
        this.cost = cost;
        this.DEPTH = DEPTH;
    }

    /*
      Constructor used to create initial node.
     */
    public Node(State state) {
        this(state, null, 0, 0);
    }

    /*
    Uses the Manhattan distance as a heuristic to calculate the total cost of expanding the node
     */
    public void applyHeuristic() {
        for (int x = 0; x < State.rowSize; x++) {
            for (int y = 0; y < State.columnSize; y++) {
                for (int i = 0; i < State.rowSize; i++) {
                    for (int j = 0; j < State.columnSize; j++) {
                        if (Solve.goalBoard[i][j] == state.board[x][y]) {
                            cost += State.calculateDistance(i, j, x, y); // Add the distance from the correct position to the cost
                        }
                    }
                }
            }
        }
    }

    public int getCost() {
        return cost;
    }

    public int getDepth() {
        return DEPTH;
    }

    /*
    Checks if any of the nodes in the list has the sent game state and returns the node
     */
    public static Node findNodeWithState(ArrayList<Node> nodeList, State gs) {
        for (int i = 0; i < nodeList.size(); i++) {
            if (gs.sameBoard(nodeList.get(i).state.board)) {
                return nodeList.get(i);
            }
        }
        return null;
    }
}