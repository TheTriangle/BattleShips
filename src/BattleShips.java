import java.io.Console;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.*;

public class BattleShips {
    public static void main(String[] args) {
        Field playingField = new Field();
        Scanner scanner = new Scanner(System.in);
        if (args.length != 0) {
            if (!parseParameters(args, playingField)) {
                return;
            }
            if (!playingField.checkPlacementExists(playingField.resultfield.clone(), playingField.carriersAm, playingField.battleshipsAm,
                    playingField.cruisersAm, playingField.destroyersAm, playingField.submarinesAm)) {
                System.out.println("Placement of provided ships combination is not possible. Exiting program. ");
            }
        } else {
            while (true) {
                inputParameters(playingField);

                if (!playingField.checkPlacementExists(playingField.resultfield.clone(), playingField.carriersAm, playingField.battleshipsAm,
                        playingField.cruisersAm, playingField.destroyersAm, playingField.submarinesAm)) {
                    System.out.println("Placement of provided ships combination is not possible. " +
                            "Input 'Q' if you want to exit" +
                            "the program, anything else to start again.");
                    String command = scanner.nextLine();
                    if (command == "Q") {
                        return;
                    }
                } else {
                    break;
                }
            }
        }

        playingField.fillField(playingField.resultfield, playingField.carriersAm, playingField.battleshipsAm,
                                       playingField.cruisersAm, playingField.destroyersAm, playingField.submarinesAm);

        /* for (int column = 0; column < columnsAm; column++) {
            for (int row = 0; row < rowsAm; row++) {
                System.out.print(resultfield[row * columnsAm + column]);
            }
            System.out.println("");
        } // */
        playingField.runGame();
    }

    static boolean parseParameters(String[] args, Field field) {
        if((args.length != 7) && (args.length != 8) && (args.length != 9)) {
            System.out.println("Please enter following command arguments: rows amount, columns amount, amounts of" +
                    "carriers, battleships, cruisers, destroyers, submarines, " +
                    "(optionally) amount of torpedoes, (optionally) recovery mode (1 or 0) - 7, 8 or 9 numbers");
            return false;
        }
        field.rowsAm = Integer.parseInt(args[0]);
        field.columnsAm = Integer.parseInt(args[1]);
        field.carriersAm = Integer.parseInt(args[2]);
        field.battleshipsAm = Integer.parseInt(args[3]);
        field.cruisersAm = Integer.parseInt(args[4]);
        field.destroyersAm = Integer.parseInt(args[5]);
        field.submarinesAm = Integer.parseInt(args[6]);

        if (args.length >= 8) {
            field.torpedoesAm = Integer.parseInt(args[7]);
        } else {
            field.torpedoesAm = 0;
        }
        if (args.length == 9) {
            field.recoveryMode = args[8].equals("1");
        }
        field.resultfield = new int[field.rowsAm * field.columnsAm];

        return true;
    }

    static void inputParameters(Field field) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please input field sizes separated by a space: ");
        String mapsize = scanner.nextLine();
        String[] sizesstrs = mapsize.split(" ");
        field.rowsAm = Integer.parseInt(sizesstrs[0]);
        field.columnsAm = Integer.parseInt(sizesstrs[1]);
        field.resultfield = new int[field.rowsAm * field.columnsAm];


        System.out.print("Please input different ship amounts (5 numbers separated by a space)\n" +
                "input amount of carriers, battleships, cruisers, destroyers and submarines respectively: ");
        String ships = scanner.nextLine();
        String[] shipsstrs = ships.split(" ");

        field.carriersAm = Integer.parseInt(shipsstrs[0]);
        field.battleshipsAm = Integer.parseInt(shipsstrs[1]);
        field.cruisersAm = Integer.parseInt(shipsstrs[2]);
        field.destroyersAm = Integer.parseInt(shipsstrs[3]);
        field.submarinesAm = Integer.parseInt(shipsstrs[4]);

        System.out.print("Select number of torpedoes (0 for none): ");
        String torpsline = scanner.nextLine();
        field.torpedoesAm = Integer.parseInt(torpsline);

        System.out.print("If you want to play in recovery mode, type '1', otherwise - anything else: ");
        String recline = scanner.nextLine();
        field.recoveryMode = recline.equals("1");
    }
}
