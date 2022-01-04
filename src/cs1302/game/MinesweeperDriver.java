package cs1302.game;

import java.util.Scanner;

/**
 * A driver program to play the game {@code cs1302.game.MinesweeperGame}.
 */
public class MinesweeperDriver {

    /**
     * The main method instantiates the one and only Scanner object, as well as a MinesweeperGame
     * object.
     * @param args command-line argument to the program
     */
    public static void main(String[] args) {
        Scanner stdIn = new Scanner(System.in);
        if (args.length != 1) {
            System.err.println();
            System.err.println("Usage: MinesweeperDriver SEED_FILE_PATH");
            System.exit(1);
        }

        MinesweeperGame game1 = new MinesweeperGame(stdIn, args[0]);
        game1.play();

    } // main

} // MinesweeperDriver
