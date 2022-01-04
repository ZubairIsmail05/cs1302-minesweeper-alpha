package cs1302.game;

import java.util.Scanner;
import java.io.File;

import java.io.FileNotFoundException;

/**
 * Creates the Minesweeper Alpha game.
 */
public class MinesweeperGame {

    private final Scanner stdIn;
    private String seedPath;

    private int rows;
    private int cols;
    private int numOfMines;
    private int rounds;
    private boolean isFog;

    private int[][] mineGrid;
    private String[][] playerGrid;

    /**
     * MinesweeperGame constructor that initializes stdIn, seedPath, the instance variables
     * in the readSeedFile method, and isFog..
     *
     * @param stdIn Scanner that will be reading a specified seed file
     * @param seedPath relative path for a desired seed file
     */
    public MinesweeperGame(Scanner stdIn, String seedPath) {
        this.stdIn = stdIn;
        this.seedPath = seedPath;
        readSeedFile();
        this.isFog = false;
    } // MinesweeperGame

    /**
     * This method reads a given seed file. The number of rows, columns, mines, and the mine
     * coordinates are initialized from the seed file using a Scanner. Each mine location
     * will be marked by a 1 within the mineGrid array.
     */
    public void readSeedFile() {
        try {
            File seedFile = new File(seedPath);
            Scanner read = new Scanner(seedFile);
            if (read.hasNextInt() == false) {
                System.err.println();
                System.err.println("Seed File Malformed Error: " +
                        "Cannot create a mine field with that many rows and/or columns!");
                System.exit(3);
            } // if
            while (read.hasNextInt()) {
                this.rows = read.nextInt();
                this.cols = read.nextInt();
                this.numOfMines = read.nextInt();
                mineGrid = new int[rows][cols];
                if (rows < 5 || rows > 10 || cols < 5 || cols > 10 || numOfMines < 1 ||
                    (numOfMines > (rows * cols) - 1)) {
                    System.err.println();
                    System.err.println("Seed File Malformed Error: " +
                        "Cannot create a mine field with that many rows and/or columns!");
                    System.exit(3);
                } // if
                for (int i = 0; i < this.numOfMines; i++) {
                    int x = read.nextInt();
                    int y = read.nextInt();
                    mineGrid[x][y] = 1;
                } // for
            } // while
        } catch (FileNotFoundException fnfe) {
            System.err.println();
            System.err.println("Seed File Not Found Error:" + fnfe.getMessage());
            System.exit(2);
        } // try
        this.playerGrid = new String[this.rows][this.cols];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                playerGrid[i][j] = " ";
            } // for i
        } // for j
    } // readSeedFile

    /**
     * Prints lines using given Scanner object.
     *
     * @param input the given input scanner
     */
    public void printLines(Scanner input) {
        while (input.hasNextLine()) {
            String line = input.nextLine();
            System.out.println(line);
        } // while
    } // printLines

    /**
     * Prints the welcome banner to standard output.
     *
     */
    public void printWelcome() {
        try {
            File welcome = new File("resources/welcome.txt");
            Scanner input = new Scanner(welcome);
            printLines(input);
        } catch (FileNotFoundException fnfe) {
            System.err.println();
            System.err.println(fnfe.getMessage());
        } // try
    } // printWelcome

    /**
     * Prints current contents of the mine field to standard output. This method also adjusts
     * the game grid if the user enters the "nofog" command.
     */
    public void printMineField() {
        for (int i = 0; i < this.rows; i++) {
            System.out.println();
            System.out.print(i);
            System.out.print(" ");
            for (int j = 0; j < this.cols; j++) {
                if (this.isFog && hasMine(i,j)) {
                    System.out.print("|<" + playerGrid[i][j] + ">");
                } else {
                    System.out.print("| ");
                    System.out.print(playerGrid[i][j]);
                    System.out.print(" ");
                } // else
            } // for j
            System.out.print("|");
        } // for i
        System.out.println();
        System.out.print("   ");
        for (int k = 0; k < this.cols;k++) {
            System.out.print(" " + k + "  ");
        } // for k
        isFog = false;
    } // printMineField

    /**
     * Reads user input for the reveal, mark, and guess commands.
     */
    public void readCommand() {
        System.out.print("minesweeper-alpha:");
        String fullCommand = stdIn.nextLine();
        Scanner commandScan = new Scanner(fullCommand);
        String command = commandScan.next();
        int gridX = commandScan.nextInt();
        int gridY = commandScan.nextInt();
    }

/**
 * Prints game to standard output and interprets user input from standard input.
 * This method will call other methods to read user input.
 */
    public void promptUser() {
        String fullCommand = stdIn.nextLine();
        Scanner commandScan = new Scanner(fullCommand);
        String command = commandScan.next();
        if (command.equals("help") || command.equals("h")) {
            help();
        } else if ( command.equals("quit") || command.equals("q")) {
            quit();
        } else if (command.equals("nofog")) {
            System.out.println("Rounds Completed: " + this.rounds);
            noFog();
            printMineField();
            System.out.println();
            System.out.print("minesweeper-alpha:");
        } else if (command.equals("reveal") || command.equals("r")) {
            int gridX = commandScan.nextInt();
            int gridY = commandScan.nextInt();
            reveal(gridX, gridY);
        } else if (command.equals("mark") || command.equals("m")) {
            int gridX = commandScan.nextInt();
            int gridY = commandScan.nextInt();
            mark(gridX, gridY);
        } else if (command.equals("guess") || command.equals("g")) {
            int gridX = commandScan.nextInt();
            int gridY = commandScan.nextInt();
            guess(gridX, gridY);
        } else {
            System.err.println();
            System.err.println("Invalid Command: Please try again.");
            printMineField();
            System.out.println();
            System.out.print("minesweeper-alpha:");
        } // else
    } // promptUser

/**
 * Places number of nearby mines of locationif the user inputs the "reveal" command.
 * The game will end if the user selects a mine.
 *  A round will also be used if the "reveal" or "r" command is inputted.
 *
 * @param gridX x coordinate of where the user wants to reveal the grid
 * @param gridY y coordinate of where the user wants to revesl the grid
 */
    public void reveal(int gridX, int gridY) {
        if (isInBounds(gridX, gridY)) {
            if (mineGrid[gridX][gridY] != 1) {
                this.rounds++;
                playerGrid[gridX][gridY] = Integer.toString(getNumAdjMines(gridX, gridY));
                System.out.println();
                System.out.println("Rounds Completed: " + this.rounds);
                printMineField();
                System.out.println();
                System.out.println();
                System.out.print("minesweeper-alpha:");
                //System.out.print(playerGrid[gridX][gridY]);
            } else {
                printLoss();
                System.exit(0);
            } // else
        } // if isInBounds
    } // reveal

    /**
     * Places an "F" on desired location if the user inputs the "mark" command. The game will end
     * if the user selects a mine. A round will also be used if the "mark" or "m" command is
     * inputted.
     *
     * @param gridX x coordinate of where the user wants to mark the grid
     * @param gridY y coordinate of where the user wants to mark the grid
     */
    public void mark(int gridX, int gridY) {
        if (isInBounds(gridX, gridY)) {
            this.rounds++;
            playerGrid[gridX][gridY] = "F";
            System.out.println();
            System.out.println("Rounds Completed: " + this.rounds);
            printMineField();
            System.out.println();
            System.out.println();
            System.out.print("minesweeper-alpha:");
        } // if isInBounds
    } // mark

    /**
     * Places an "?" on desired location if the user inputs the "mark" command. The game will end
     * if the user selects a mine. A round will also be used if the "guess" or "g" command is
     * inputted.
     *
     * @param gridX x coordinate of where the user wants to guess on the grid
     * @param gridY y coordinate of where the user wants to guess on the grid
     */
    public void guess(int gridX, int gridY) {
        if (isInBounds(gridX, gridY)) {
            this.rounds++;
            playerGrid[gridX][gridY] = "?";
            System.out.println("Rounds Completed: " + this.rounds);
            printMineField();
            System.out.println();
            System.out.println();
            System.out.print("minesweeper-alpha:");
        } // if isInBounds
    } // guess

/**
 * Displays current grid and available commands. A round will be used if the "help" or h command
 * is inputted.
 */
    public void help() {
        System.out.println("Commands Available...\n" + "- Reveal: r/reveal row col\n" +
            "-   Mark: m/mark   row col\n" + "-  Guess: g/guess  row col\n" +
            "-   Help: h/help\n" + "-   Quit: q/quit");
        this.rounds++;
        System.out.println("Rounds Completed: " + this.rounds);
        printMineField();
        System.out.println();
        System.out.print("minesweeper-alpha:");
    } // help

    /**
     * Displays a quit message and exits the game/program.
     */
    public void quit() {
        System.out.println("Quitting the game...");
        System.out.println("Bye!");
        System.exit(0);
    } // quit

    /**
     * This method sets the instance variable isFog to true. It is called in the promptUser method.
     */
    public void noFog() {
        this.isFog = true;
    } // nofog

/**
 * This method checks if there is a mine in the game grid at a specified location.
 *
 * @param gridX x coordinated of where the user wants to make their move
 * @param gridY y coordinated of where the user wants to make their move
 * @return true if the desired location contains a mine
 */
    public boolean hasMine(int gridX, int gridY) {
        if (mineGrid[gridX][gridY] == 1) {
            return true;
        } else {
            return false;
        } // else
    } // isMine

    /**
     * Determines if the conditions for winning the game are met.
     *
     * @return true if all sqaures containing a mine are marked as definitely containing a mine
     * and all squares not containing a mine are revealed
     */
    public boolean isWon() {
        int counter = 0;
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                if ((playerGrid[i][j] == "?" || playerGrid[i][j] != " ") &&
                    playerGrid[i][j] != "F") {
                    counter++;
                    if (((this.rows * this.cols) - this.numOfMines) == counter) {
                        return true;
                    } // if
                } // if
            } // for j
        } // for i
        return false;
    } // isWon

    /**
     *  Prints win message to standard output.
     */
    public void printWin() {
        try {
            File win = new File("resources/gamewon.txt");
            Scanner input = new Scanner(win);
            printLines(input);
        } catch (FileNotFoundException fnfe) {
            System.err.println();
            System.err.println(fnfe.getMessage());
        } // try
    } // printWin

    /**
     * Prints loss message to standard output.
     */
    public void printLoss() {
        try {
            File loss = new File("resources/gameover.txt");
            Scanner input = new Scanner(loss);
            printLines(input);
        } catch (FileNotFoundException fnfe) {
            System.err.println();
            System.err.println(fnfe.getMessage());
        } // try
    } // printLoss

    /**
     * Creates the game grid and allows the user to play Minesweeper Alpha. The game takes user
     * input from standard input and allows them to reveal, mark, guess, get help, or quit.
     * After the user inputs an appropriate command, the desired move will be made on the grid
     * and the next round will begin, assuming the player has not lost or quit.
     */
    public void play() {
        printWelcome();
        System.out.println();
        System.out.println("Rounds Completed: 0");
        printMineField();
        System.out.println();
        System.out.println();
        System.out.print("minesweeper-alpha:");
        while (true) {
            promptUser();
            if (isWon() == true) {
                double score;
                score = 100.0 * rows * cols / rounds;
                System.out.println();
                printWin();
                System.out.printf("%.2f", score);
                System.out.println();
                System.exit(0);

            } // if
        } // while
    } // play

    /**
     * Returns the number of mines adjacent to the specified
     * square in the grid.
     *
     * @param row the row index of the square
     * @param col the column index of the square
     * @return the number of adjacent mines
     */
    public int getNumAdjMines(int row, int col) {
        int count = 0;
        if (isInBounds(row, col)) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (isInBounds(row + i, col + j)) {
                        if (!(i == 0 && j == 0)) {
                            if (mineGrid[row + i][col + j] != 0) {
                                count++;
                            } // if mineGrid
                        } // if
                    } // if isInBounds
                } // for j
            } // for i
        } // if
        return count;
    } // getNumAdjMines

    /**
     * Indicates whether or not the square is in the game grid.
     *
     * @param row the row index of the square
     * @param col the column index of the square
     * @return true if the square is in the game grid; false otherwise
     */
    public boolean isInBounds(int row, int col) {
        if (row < 0 || row >= this.rows || col < 0 || col >= this.cols) {
            return false;
        }
        return true;
    } // isInBounds
} // MinesweeperGame
