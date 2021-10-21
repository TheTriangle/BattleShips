import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.*;
import static java.lang.Math.abs;

public class Field {
    int carriersAm; // length 5 cells
    int battleshipsAm; // length 4 cells
    int cruisersAm; // length 3 cells
    int destroyersAm; // length 2 cells
    int submarinesAm; // length 1 cell
    int torpedoesAm;
    boolean recoveryMode;

    int rowsAm;
    int columnsAm;

    int[] resultfield;
    char[] shotsfield;

    void recoverHitShips() {
        for (int row = 0; row < rowsAm; row++) {
            for (int column = 0; column < columnsAm; column++) {
                if (shotsfield[row * columnsAm + column] == '!') {
                    resultfield[row * columnsAm + column] = abs(resultfield[row * columnsAm + column]);
                    shotsfield[row * columnsAm + column] = '~';
                }
            }
        }
    }

    boolean hitSameShip(int row1, int column1, int row2, int column2) {
        if ((row1 != row2) && (column1 != column2)) {
            return false;
        }

        int curship = abs(resultfield[row1 * columnsAm + column1]);
        if (row1 == row2) {
            for (int i = min(column1, column2); i < max(column1, column2); i++) {
                if (abs(resultfield[row1 * columnsAm + i]) != curship) {
                    return false;
                }
            }
        }
        if (column1 == column2) {
            for (int i = min(row1, row2); i < max(row1, row2); i++) {
                if (abs(resultfield[i * columnsAm + column2]) != curship) {
                    return false;
                }
            }
        }
        return true;
    }

    void sinkShipAt(int[] shipsfield, char[] shotsfield, int row, int column) {
        int curship = shipsfield[row * columnsAm + column];
        shotsfield[row * columnsAm + column] = 'X';
        shipsfield[row * columnsAm + column] = -abs(curship);
        if ((row > 0)) {
            if ((shotsfield[(row - 1) * columnsAm + column] != 'X')
                    && (abs(shipsfield[(row - 1) * columnsAm + column]) == abs(curship))) {
                sinkShipAt(shipsfield, shotsfield, row - 1, column);
            }
        }
        if ((column > 0)) {
            if ((shotsfield[row * columnsAm + column - 1] != 'X')
                    && (abs(shipsfield[row * columnsAm + column - 1]) == abs(curship))) {
                sinkShipAt(shipsfield, shotsfield, row, column - 1);
            }
        }
        if ((column < columnsAm - 1)) {
            if ((shotsfield[row * columnsAm + column + 1] != 'X')
                    && (abs(shipsfield[row * columnsAm + column + 1]) == abs(curship))) {
                sinkShipAt(shipsfield, shotsfield, row, column + 1);
            }
        }
        if ((row < rowsAm - 1)) {
            if ((shotsfield[(row + 1) * columnsAm + column] != 'X')
                    && (abs(shipsfield[(row + 1) * columnsAm + column]) == abs(curship))) {
                sinkShipAt(shipsfield, shotsfield, row + 1, column);
            }
        }

    }

    boolean checkShipSunk(int[] shipsfield, int row, int column) {
        int curship = shipsfield[row * columnsAm + column];
        shipsfield[row * columnsAm + column] = 0;
        if ((row > 0)) {
            if ((shipsfield[(row - 1) * columnsAm + column] != 0)
                    && (shipsfield[(row - 1) * columnsAm + column] != curship)) {
                shipsfield[row * columnsAm + column] = curship;
                return false;
            } else if (shipsfield[(row - 1) * columnsAm + column] == curship) {
                if (!checkShipSunk(shipsfield, row - 1, column)) {
                    shipsfield[row * columnsAm + column] = curship;
                    return false;
                }
            }
        }
        if ((column > 0)) {
            if ((shipsfield[row * columnsAm + column - 1] != 0)
                    && (shipsfield[row * columnsAm + column - 1] != curship)) {
                shipsfield[row * columnsAm + column] = curship;
                return false;
            } else if (shipsfield[row * columnsAm + column - 1] == curship) {
                if (!checkShipSunk(shipsfield, row, column - 1)) {
                    shipsfield[row * columnsAm + column] = curship;
                    return false;
                }
            }
        }
        if ((column < columnsAm - 1)) {
            if ((shipsfield[row * columnsAm + column + 1] != 0)
                    && (shipsfield[row * columnsAm + column + 1] != curship)) {
                shipsfield[row * columnsAm + column] = curship;
                return false;
            } else if (shipsfield[row  * columnsAm + column + 1] == curship) {
                if (!checkShipSunk(shipsfield, row, column + 1)) {
                    shipsfield[row * columnsAm + column] = curship;
                    return false;
                }
            }
        }
        if ((row < rowsAm - 1)) {
            if ((shipsfield[(row + 1) * columnsAm + column] != 0)
                    && (shipsfield[(row + 1) * columnsAm + column] != curship)) {
                shipsfield[row * columnsAm + column] = curship;
                return false;
            } else if (shipsfield[(row + 1) * columnsAm + column] == curship) {
                if (!checkShipSunk(shipsfield, row + 1, column)) {
                    shipsfield[row * columnsAm + column] = curship;
                    return false;
                }
            }
        }
        shipsfield[row * columnsAm + column] = curship;
        return true;
    }

    void fillField(int[] field, int carriers, int battleships,
                          int cruisers, int destroyers, int submarines) {
        shotsfield = new char[rowsAm * columnsAm];
        for (int i = 0; i < rowsAm * columnsAm; i++) {
            shotsfield[i] = '~';
        }


        if (carriers + battleships + cruisers + destroyers + submarines == 0) {
            return;
        }
        int fittingPlaces = 0;
        int puttingLength = 0;
        int placedCarrier = 0, placedBattleship = 0, placedCruiser = 0, placedDestroyer = 0, placedSubmarine = 0;
        if (carriers > 0) {
            puttingLength = 5;
            placedCarrier = 1;
        } else if (battleships > 0) {
            puttingLength = 4;
            placedBattleship = 1;
        } else if (cruisers > 0) {
            puttingLength = 3;
            placedCruiser = 1;
        } else if (destroyers > 0) {
            puttingLength = 2;
            placedDestroyer = 1;
        } else {
            puttingLength = 1;
            placedSubmarine = 1;
        }
        int viableposam = 0;
        for (int column = 0; column < columnsAm; column++) {
            for (int row = 0; row < rowsAm; row++) {
                if (canPlaceShip(field, row, column, puttingLength, false)) {
                    placeShip(field, row, column, puttingLength, false);
                    if (checkPlacementExists(field.clone(), carriers - placedCarrier,
                            battleships - placedBattleship, cruisers - placedCruiser,
                            destroyers - placedDestroyer, submarines - placedSubmarine)) {
                        viableposam++;
                    }
                    removeShip(field, row, column, puttingLength, false);
                }
                if (canPlaceShip(field, row, column, puttingLength, true)) {
                    placeShip(field, row, column, puttingLength, true);
                    if (checkPlacementExists(field.clone(), carriers - placedCarrier,
                            battleships - placedBattleship, cruisers - placedCruiser,
                            destroyers - placedDestroyer, submarines - placedSubmarine)) {
                        viableposam++;
                    }
                    removeShip(field, row, column, puttingLength, true);
                }
            }
        }
        int randomPos = ThreadLocalRandom.current().nextInt(0, viableposam);
        // System.out.println("randomPos: " + randomPos + " viableposam: " + viableposam);
        int currentpos = 0;
        for (int column = 0; column < columnsAm; column++) {
            for (int row = 0; row < rowsAm; row++) {
                if (canPlaceShip(field, row, column, puttingLength, false)) {
                    placeShip(field, row, column, puttingLength, false);
                    if (checkPlacementExists(field.clone(), carriers - placedCarrier,
                            battleships - placedBattleship, cruisers - placedCruiser,
                            destroyers - placedDestroyer, submarines - placedSubmarine)) {
                        if (currentpos == randomPos) {
                            fillField(field, carriers - placedCarrier,
                                    battleships - placedBattleship, cruisers - placedCruiser,
                                    destroyers - placedDestroyer, submarines - placedSubmarine);
                            return;
                        }
                        currentpos++;
                    }
                    removeShip(field, row, column, puttingLength, false);
                }
                if (canPlaceShip(field, row, column, puttingLength, true)) {
                    placeShip(field, row, column, puttingLength, true);
                    if (checkPlacementExists(field.clone(), carriers - placedCarrier,
                            battleships - placedBattleship, cruisers - placedCruiser,
                            destroyers - placedDestroyer, submarines - placedSubmarine)) {
                        if (currentpos == randomPos) {
                            fillField(field, carriers - placedCarrier,
                                    battleships - placedBattleship, cruisers - placedCruiser,
                                    destroyers - placedDestroyer, submarines - placedSubmarine);
                            return;
                        }
                        currentpos++;
                    }
                    removeShip(field, row, column, puttingLength, true);
                }
            }
        }

    }

    void runGame() {
        Scanner scanner = new Scanner(System.in);
        int turn = 0;
        String shotstr;
        int shotrow;
        int shotcolumn;
        boolean launchingTorpedoe = false;
        int lastshotrow = -1;
        int lastshotcolumn = -1;
        while (true) {
            turn++;

            System.out.print("Turn " + turn + ": please, input your shot: 2 integers separated by space: ");
            while (true) {
                shotstr = scanner.nextLine();
                if (shotstr.split(" ").length == 3) {
                    if (torpedoesAm <= 0) {
                        System.out.print("You are out of torpedoes! Please input your shot: ");
                        continue;
                    }
                    torpedoesAm--;
                    launchingTorpedoe = true;
                    shotrow = Integer.parseInt(shotstr.split(" ")[1]);
                    shotcolumn = Integer.parseInt(shotstr.split(" ")[2]);
                } else {
                    shotrow = Integer.parseInt(shotstr.split(" ")[0]);
                    shotcolumn = Integer.parseInt(shotstr.split(" ")[1]);
                }
                if ((shotrow < 1) || (shotcolumn < 1) || (shotrow > rowsAm) || (shotcolumn > columnsAm)) {
                    System.out.print("Please enter two numbers: row (between 1 and " + rowsAm + ") and column" +
                            "(between 1 and " + columnsAm + "): ");
                } else {
                    if (shotsfield[(shotrow - 1) * columnsAm + shotcolumn - 1] != '~') {
                        System.out.print("You have already shot here or sank a ship on this position. Enter new shot: ");
                        continue;
                    }
                    break;
                }
            }
            shotrow--;
            shotcolumn--;

            if (resultfield[shotrow * columnsAm + shotcolumn] > 0) {
                resultfield[shotrow * columnsAm + shotcolumn] = -resultfield[shotrow * columnsAm + shotcolumn];
                if (launchingTorpedoe) {
                    launchingTorpedoe = false;
                    sinkShipAt(resultfield, shotsfield, shotrow, shotcolumn);
                }
                if (checkShipSunk(resultfield, shotrow, shotcolumn)) {
                    sinkShipAt(resultfield, shotsfield, shotrow, shotcolumn);
                    switch (resultfield[shotrow * columnsAm + shotcolumn]) {
                        case -5 -> {
                            carriersAm--;
                            System.out.println("You just have sunk a carrier.");
                        }
                        case -4 -> {
                            battleshipsAm--;
                            System.out.println("You just have sunk a battleship.");
                        }
                        case -3 -> {
                            cruisersAm--;
                            System.out.println("You just have sunk a cruiser.");
                        }
                        case -2 -> {
                            destroyersAm--;
                            System.out.println("You just have sunk a destroyer.");
                        }
                        default -> {
                            submarinesAm--;
                            System.out.println("You just have sunk a submarine.");
                        }
                    }
                    System.out.println("");
                } else {
                    System.out.println("Hit");
                    shotsfield[shotrow * columnsAm + shotcolumn] = '!';
                    if (recoveryMode) {
                        if (lastshotrow != -1) {
                            if (!hitSameShip(shotrow, shotcolumn, lastshotrow, lastshotcolumn)) {
                                recoverHitShips();
                                resultfield[shotrow * columnsAm + shotcolumn] = -resultfield[shotrow * columnsAm + shotcolumn];
                            }
                        }
                    }
                    shotsfield[shotrow * columnsAm + shotcolumn] = '!';
                }
                if (carriersAm + battleshipsAm + cruisersAm + destroyersAm + submarinesAm == 0) {
                    System.out.println("Congradulations, you've won in just " + turn + " shots!");
                    return;
                }
            } else {
                System.out.println("Miss");
                if (recoveryMode) {
                    recoverHitShips();
                }
                shotsfield[shotrow * columnsAm + shotcolumn] = 'o';
            }
            lastshotrow = shotrow;
            lastshotcolumn = shotcolumn;
            for (int row = 0; row < rowsAm; row++) {
                for (int column = 0; column < columnsAm; column++) {
                    System.out.print(shotsfield[row * columnsAm + column]);
                }
                System.out.println("");
            }
        }
    }

    boolean checkPlacementExists (int[] field, int carriers, int battleships,
                                         int cruisers, int destroyers, int submarines) {
        if (carriers + battleships + cruisers + destroyers + submarines == 0) {
            return true;
        }
        for (int row = 0; row < rowsAm; row++) {
            for (int column = 0; column < columnsAm; column++) {
                if (carriers > 0) {
                    if (canPlaceShip(field, row, column, 5, false)) {
                        placeShip(field, row, column, 5, false);
                        if (checkPlacementExists(field.clone(), carriers - 1,
                                battleships, cruisers, destroyers, submarines)) {
                            return true;
                        } else {
                            removeShip(field, row, column, 5, false);
                        }
                    }
                    if (canPlaceShip(field, row, column, 5, true)) {
                        placeShip(field, row, column, 5, true);
                        if (checkPlacementExists(field.clone(), carriers - 1,
                                battleships, cruisers, destroyers, submarines)) {
                            return true;
                        } else {
                            removeShip(field, row, column, 5, true);
                        }
                    }
                }
                if (battleships > 0) {
                    if (canPlaceShip(field, row, column, 4, false)) {
                        placeShip(field, row, column, 4, false);
                        if (checkPlacementExists(field.clone(), carriers,
                                battleships - 1, cruisers, destroyers, submarines)) {
                            return true;
                        } else {
                            removeShip(field, row, column, 4, false);
                        }
                    }
                    if (canPlaceShip(field, row, column, 4, true)) {
                        placeShip(field, row, column, 4, true);
                        if (checkPlacementExists(field.clone(), carriers,
                                battleships - 1, cruisers, destroyers, submarines)) {
                            return true;
                        } else {
                            removeShip(field, row, column, 4, true);
                        }
                    }
                }
                if (cruisers > 0) {
                    if (canPlaceShip(field, row, column, 3, false)) {
                        placeShip(field, row, column, 3, false);
                        if (checkPlacementExists(field.clone(), carriers,
                                battleships, cruisers - 1, destroyers, submarines)) {
                            return true;
                        } else {
                            removeShip(field, row, column, 3, false);
                        }
                    }
                    if (canPlaceShip(field, row, column, 3, true)) {
                        placeShip(field, row, column, 3, true);
                        if (checkPlacementExists(field.clone(), carriers,
                                battleships, cruisers - 1, destroyers, submarines)) {
                            return true;
                        } else {
                            removeShip(field, row, column, 3, false);
                        }
                    }
                }
                if (destroyers > 0) {
                    if (canPlaceShip(field, row, column, 2, false)) {
                        placeShip(field, row, column, 2, false);
                        if (checkPlacementExists(field.clone(), carriers,
                                battleships, cruisers, destroyers - 1, submarines)) {
                            return true;
                        } else {
                            removeShip(field, row, column, 2, false);
                        }
                    }
                    if (canPlaceShip(field, row, column, 2, true)) {
                        placeShip(field, row, column, 2, true);
                        if (checkPlacementExists(field.clone(), carriers,
                                battleships, cruisers, destroyers - 1, submarines)) {
                            return true;
                        } else {
                            removeShip(field, row, column, 2, true);
                        }
                    }
                }
                if (submarines > 0) {
                    if (canPlaceShip(field, row, column, 1, false)) {
                        placeShip(field, row, column, 1, false);
                        if (checkPlacementExists(field.clone(), carriers,
                                battleships, cruisers, destroyers, submarines - 1)) {
                            return true;
                        } else {
                            removeShip(field, row, column, 1, false);
                        }
                    }
                    if (canPlaceShip(field, row, column, 1, true)) {
                        placeShip(field, row, column, 1, true);
                        if (checkPlacementExists(field.clone(), carriers,
                                battleships, cruisers, destroyers, submarines - 1)) {
                            return true;
                        } else {
                            removeShip(field, row, column, 1, true);
                        }
                    }
                }
            }
        }

        return false;
    }

    boolean canPlaceShip(int[] field, int row, int column, int length, boolean horizontal) {
        if (horizontal && ((column + length) > columnsAm)) {
            return false;
        }
        if (!horizontal && ((row + length) > rowsAm)) {
            return false;
        }
        // System.out.println("placing on " + row + " " + column + " ishorizontal: " + horizontal + " length: "
        //                                  + length + " rows, columnsam: " + rowsAm + " " + columnsAm);
        if (horizontal) {
            for (int crow = row - 1; crow <= row + 1; crow++) {
                for (int ccolumn = column - 1; ccolumn <= column + length; ccolumn++) {
                    if ((crow < 0) || (crow >= rowsAm) || (ccolumn < 0) || (ccolumn >= columnsAm)) {
                        continue;
                    }
                    if (field[crow * columnsAm + ccolumn] != 0) {
                        return false;
                    }
                }
            }
        } else {
            for (int crow = row - 1; crow <= row + length; crow++) {
                for (int ccolumn = column - 1; ccolumn <= column + 1; ccolumn++) {
                    if ((crow < 0) || (crow >= rowsAm) || (ccolumn < 0) || (ccolumn >= columnsAm)) {
                        continue;
                    }
                    if (field[crow * columnsAm + ccolumn] != 0) {
                        return false;
                    }
                }
            }
        }
        // System.out.println("correct");
        return true;
    }

    void placeShip(int[] field, int row, int column, int length, boolean horizontal) {
        if (horizontal) {
            for (int i = column; i < column + length; i++) {
                field[row * columnsAm + i] = length;
            }
        } else {
            for (int i = row; i < row + length; i++) {
                field[i * columnsAm + column] = length;
            }
        }
    }

    void removeShip(int[] field, int row, int column, int length, boolean horizontal) {
        if (horizontal) {
            for (int i = column; i < column + length; i++) {
                field[row * columnsAm + i] = 0;
            }
        } else {
            for (int i = row; i < row + length; i++) {
                field[i * columnsAm + column] = 0;
            }
        }
    }
}
