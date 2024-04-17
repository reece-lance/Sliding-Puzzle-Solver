package sliding_puzzle;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class GUI extends JFrame {
    int[][] initialBoard;
    Set<Integer> initialSet;
    int[][] goalBoard;
    Set<Integer> goalSet;
    String[] fileBoard;
    private int rows;
    private int columns;
    private JPanel Background;
    private JPanel BottomLeftPanel;
    private JPanel BottomRightPanel;
    private JButton solveButton;
    private JTextField
            initial00, initial01, initial02, initial03, initial04,
            initial10, initial11, initial12, initial13, initial14,
            initial20, initial21, initial22, initial23, initial24,
            initial30, initial31, initial32, initial33, initial34,
            initial40, initial41, initial42, initial43, initial44;

    JTextField[][] initialTextFields = {
            {initial00, initial01, initial02, initial03, initial04},
            {initial10, initial11, initial12, initial13, initial14},
            {initial20, initial21, initial22, initial23, initial24},
            {initial30, initial31, initial32, initial33, initial34},
            {initial40, initial41, initial42, initial43, initial44}};

    private JTextField
            goal00, goal01, goal02, goal03, goal04,
            goal10, goal11, goal12, goal13, goal14,
            goal20, goal21, goal22, goal23, goal24,
            goal30, goal31, goal32, goal33, goal34,
            goal40, goal41, goal42, goal43, goal44;

    JTextField[][] goalTextFields = {
            {goal00, goal01, goal02, goal03, goal04},
            {goal10, goal11, goal12, goal13, goal14},
            {goal20, goal21, goal22, goal23, goal24},
            {goal30, goal31, goal32, goal33, goal34},
            {goal40, goal41, goal42, goal43, goal44}};

    private JSpinner columnSpinner;
    private JSpinner rowSpinner;
    private JButton chooseFileButton;
    private JCheckBox fileChosenCheckBox;
    private JButton autoFillBoardButton;
    private JTextField fileNameField;
    private JButton chooseLocationButton;
    private JButton emptyInitialBoardButton;
    private JButton emptyGoalBoardButton;
    private JButton randomiseInitialBoardButton;
    String chosenDirectoryName;
    String chosenFileName;
    SolvingPopUp errorPopUp;

    public GUI(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(Background);
        this.pack();
        setLocationRelativeTo(null); // Centers JFrame

        errorPopUp = new SolvingPopUp();
        initialiseSpinners();

        solveButton.addActionListener(e -> {
            try {
                initiateSolve();
                getDimensions(); //Resets Rows and Columns back to match scroller

            } catch (Exception exception) {
                errorPopUp.showText("Error, please try again");
            }
        });

        chooseLocationButton.addActionListener(e -> chooseDir());

        chooseFileButton.addActionListener(e -> chooseFile());

        /*
        Method to automatically fill goal board using input from initial board
         */
        autoFillBoardButton.addActionListener(e -> {
            getDimensions();
            initialBoard = new int[columns][rows];
            goalBoard = new int[columns][rows];
            initialSet = new TreeSet<>();
            goalSet = new TreeSet<>();

            generateBoard(false, true);
            initialSet = checkValidity(initialBoard, initialSet);
            generateGoalBoard();
            fillGoalBoard();
        });

        emptyInitialBoardButton.addActionListener(e -> emptyBoard(initialTextFields));

        emptyGoalBoardButton.addActionListener(e -> emptyBoard(goalTextFields));

        randomiseInitialBoardButton.addActionListener(e -> randomiseBoard(initialTextFields));
    }

    public void initialiseSpinners() {
        rows = 5;
        columns = 5;
        columnSpinner.setModel(new SpinnerNumberModel(columns, 2, 5, 1));
        rowSpinner.setModel(new SpinnerNumberModel(rows, 2, 5, 1));

        ChangeListener spinnerListener = e -> {
            getDimensions();
            hideAllTextFields(initialTextFields);
            hideAllTextFields(goalTextFields);
            showTextFields(initialTextFields);
            showTextFields(goalTextFields);
        };
        columnSpinner.addChangeListener(spinnerListener);
        rowSpinner.addChangeListener(spinnerListener);
    }

    public void initiateSolve() {
        if (fileChosenCheckBox.isSelected()) {
            readFile();
        }

        initialBoard = new int[columns][rows];
        goalBoard = new int[columns][rows];
        initialSet = new TreeSet<>();
        goalSet = new TreeSet<>();

        generateBoard(fileChosenCheckBox.isSelected(), false);
        initialSet = checkValidity(initialBoard, initialSet);

        if (fileChosenCheckBox.isSelected()) {
            generateGoalBoard();
        }

        goalSet = checkValidity(goalBoard, goalSet);
        if (compareSets()) {
            PrintWriter file = createFile();
            if (file != null) {
                Solve problem = new Solve(initialBoard, goalBoard);
                SolvingPopUp popUp = new SolvingPopUp();
                problem.solve(file, popUp);
            }
            else {
                errorPopUp.showText("Error, could not solve");
            }
        }
        else {
            errorPopUp.showText("Error, boards not valid");
        }
    }

    public void chooseDir() {
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setAcceptAllFileFilterUsed(false);
        if (dirChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chosenDirectoryName = dirChooser.getSelectedFile().getAbsolutePath();
        }
    }

    public PrintWriter createFile() {
        try {
            String enteredFileName = fileNameField.getText();

            if (chosenDirectoryName == null) {
                chosenDirectoryName = new File(System.getProperty("user.home")).getAbsolutePath();
            }
            if (enteredFileName.equals("")) {
                enteredFileName = "puzzle_slider_solution.txt";
            }
            else if (!enteredFileName.endsWith(".txt")) {
                enteredFileName += ".txt";
            }

            File outFile = new File(chosenDirectoryName + "/" + enteredFileName); // Create a file as the destination for output
            return new PrintWriter(outFile); // Create a PrintWriter for that file
        } catch (Exception exception) {
            errorPopUp.showText("Error, could not create file");
        }
        return null;
    }


    public void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            chosenFileName = selectedFile.getAbsolutePath();
        }
    }

    public void readFile() {
        try {
            // Reads in text file
            File chosenFile = new File(chosenFileName);
            Scanner myReader = new Scanner(chosenFile);

            if (myReader.hasNextLine()) {
                String data = myReader.nextLine(); // Gets data from file
                splitFileData(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            errorPopUp.showText("Error, please try again");
        }
    }

    public void splitFileData(String data) {
        String[] dimensionsArray = data.substring(data.indexOf("(") + 1, data.indexOf(")")).split(", ");
        rows = Integer.parseInt(dimensionsArray[0]);
        columns = Integer.parseInt(dimensionsArray[1]);
        fileBoard = data.substring(data.indexOf("[") + 1, data.indexOf("]")).replaceAll("[^ 0-9]", "").replace("  ", " ").split(" ");
    }

    public void generateBoard(boolean file, boolean autoGenerate) {
        int counter = 0;
        for (int y = 0; y < columns; y++) {
            for (int x = 0; x < rows; x++) {
                String initialValue = initialTextFields[y][x].getText();
                try {
                    if (file) {
                        initialBoard[y][x] = Integer.parseInt(fileBoard[counter]);
                    } else {
                        initialBoard[y][x] = Integer.parseInt(initialValue);
                    }
                } catch (Exception exception) {
                    initialBoard[y][x] = 0;
                }

                if (!file && !autoGenerate) {
                    String goalValue = goalTextFields[y][x].getText();
                    try {
                        goalBoard[y][x] = Integer.parseInt(goalValue);
                    } catch (Exception exception) {
                        goalBoard[y][x] = 0;
                    }
                }
                counter += 1;
            }
        }
    }

    // Removes duplicate and null values from set
    public Set<Integer> checkValidity(int[][] board, Set<Integer> set) {
        for (int[] row : board) {
            for (int value : row) {
                set.add(value);
            }
        }
        return set;
    }

    // Generates simple goalBoard
    public void generateGoalBoard() {
        try {
            Iterator<Integer> initialSetIterator = initialSet.iterator();
            for (int y = 0; y < columns; y++) {
                for (int x = 0; x < rows; x++) {
                    if (initialSetIterator.hasNext()) {
                        int next = initialSetIterator.next();
                        if (next == 0) {
                            next = initialSetIterator.next();
                        }
                        goalBoard[y][x] = next;
                    }
                }
            }
        } catch (Exception e) {
            errorPopUp.showText("Error, please try again");
        }
    }

    // Checks both boards contain the same amount of values and that that is the correct amount
    public boolean compareSets() {
        if (initialSet.size() == goalSet.size() & initialSet.size() == (rows * columns)) {
            Iterator<Integer> initialSetIterator = initialSet.iterator();
            Iterator<Integer> goalSetIterator = goalSet.iterator();
            while (initialSetIterator.hasNext()) {
                if (!initialSetIterator.next().equals(goalSetIterator.next())) { // Checks if values from both boards are not the same
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void getDimensions() {
        rows = (Integer) rowSpinner.getValue();
        columns = (Integer) columnSpinner.getValue();
    }

    public void hideAllTextFields(JTextField[][] fields) {
        for (JTextField[] row : fields) {
            for (JTextField field : row) {
                field.setVisible(false);
            }
        }
    }

    public void showTextFields(JTextField[][] fields) {
        for (int y = 0; y < columns; y++) {
            for (int x = 0; x < rows; x++) {
                fields[y][x].setVisible(true);
            }
        }
    }

    public void fillGoalBoard() {
        for (int y = 0; y < columns; y++) {
            for (int x = 0; x < rows; x++) {
                String value = String.valueOf(goalBoard[y][x]);
                if (value.equals("0")) {
                    value = "";
                }
                goalTextFields[y][x].setText(value);
            }
        }
    }

    public void emptyBoard(JTextField[][] fields) {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                fields[y][x].setText("");
            }
        }
    }

    public void randomiseBoard(JTextField[][] fields) {
        List<String> values = new ArrayList<>();
        for (int i = 1; i < columns*rows; i++) {
            values.add(String.valueOf(i));
        }
        values.add("");
        Collections.shuffle(values);

        int counter = 0;
        for (int y = 0; y < columns; y++) {
            for (int x = 0; x < rows; x++) {
                fields[y][x].setText(values.get(counter));
                counter += 1;
            }
        }
    }
}