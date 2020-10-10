package vsu.course2;

import exceptions.GameProcessException;

import java.util.ArrayList;
import java.util.Stack;

public class Game {
    private final Player[] players = new Player[2];
    private final Field field = new Field(8, 8);
    private int turnOrder = 0;

    public Game() {
        try {
            players[0] = new Player(field.getCell(0, 0));
            players[1] = new Player(field.getCell(7, 7));

            int letter = 0;
            int number = 0;

            for (int i = 0; i < 12; i++) {
                field.setChecker(players[0].getCheckers()[i], letter, number);
                field.setChecker(players[1].getCheckers()[i], 7 - letter, 7 - number);
                if (letter != 6 && letter != 7) {
                    letter += 2;
                } else {
                    number++;
                    letter = number % 2 == 0 ? 0 : 1;
                }
            }
        } catch (Exception ignored) {}
    }

    public void doStep(int prevLetter, int prevNumber, int nextLetter, int nextNumber) throws GameProcessException {
        if (gameOver()) return;
        if (!players[turnOrder].hasCheack(field.getChecker(prevLetter, prevNumber)))
            throw new GameProcessException("Player doesn't have checkers on this position");

        ArrayList<Checker> attackedChecks = attackedCheckers(new Field.Cell(prevLetter, prevNumber),
                new Field.Cell(nextLetter, nextNumber));

        if (attackedChecks.size() != 0) {
            players[(turnOrder + 1) % players.length].removeCheck((Checker[]) attackedChecks.toArray());
        } else if (!canMakeStep(field.getCell(prevLetter, prevNumber), field.getCell(nextLetter, nextNumber))) {
            throw new GameProcessException("Checker can't go back");
        }

        field.moveChecker(prevLetter, prevNumber, nextLetter, nextNumber);

        turnOrder = (turnOrder + 1) % players.length;
    }

    private boolean canMakeStep(Field.Cell curCell, Field.Cell nextCell) {
        return (Math.abs(players[turnOrder].getStartPoint().getNumber() - curCell.getNumber()) >=
                Math.abs(players[turnOrder].getStartPoint().getNumber() - nextCell.getNumber())) ||
                curCell.getCheck().isKing();
    }

    private ArrayList<Checker> attackedCheckers(Field.Cell curCell, Field.Cell nextCell)
            throws GameProcessException {

        Stack<Field.Cell> attackedCells = new Stack<>();
        if (nextCell.getCheck() != null) {
            throw new GameProcessException("Check can't be attacked if another check stay behind it");
        }

        ArrayList<Checker> result = attackedCheckers(curCell, nextCell, new Stack<Field.Cell>());

        if (result.isEmpty()) {
            throw new GameProcessException("Check can't attack on this way");
        }

        return result;
    }

    private ArrayList<Checker> attackedCheckers(Field.Cell curCell, Field.Cell nextCell,
                            Stack<Field.Cell> attackedCells) {

        for (Field.Cell cell : field.neighboringCells(curCell)) {
            if (cell.getCheck() != null && cell.getCheck().getPlayerID() != players[turnOrder].id()
                    && !attackedCells.contains(cell)) {
                attackedCells.push(cell);
                for (Field.Cell visitedCell : field.neighboringCells(cell)) {
                    if (visitedCell != null) {
                        continue;
                    }

                    if (visitedCell.equals(nextCell)) {
                        ArrayList<Checker> result = new ArrayList<>();

                        for (Field.Cell attackedCell : attackedCells) {
                            result.add(attackedCell.getCheck());
                        }

                        return result;
                    } else {
                        ArrayList<Checker> result = attackedCheckers(visitedCell,
                                nextCell, attackedCells);
                        if (!result.isEmpty()) {
                            return result;
                        }
                    }
                }
                attackedCells.pop();
            }
        }

        return new ArrayList<>();
    }

    public boolean gameOver() {
        return players[0].getCheckers().length == 0 || players[1].getCheckers().length == 0;
    }
}
