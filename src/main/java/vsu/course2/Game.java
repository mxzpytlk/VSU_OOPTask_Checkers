package vsu.course2;

import exceptions.GameProcessException;

import java.util.ArrayList;
import java.util.List;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Field getField() {
        return field;
    }

    //Сделать проверку на то, что во время хода нельзя съесть чужую шашку
    public void doStep(int prevLetter, int prevNumber, int nextLetter, int nextNumber) throws GameProcessException {
        if (gameOver()) return;
        if (!players[turnOrder].hasCheck(field.getChecker(prevLetter, prevNumber)))
            throw new GameProcessException("Player doesn't have checkers on this position");

        if (!canMakeStep(field.getCell(prevLetter, prevNumber), field.getCell(nextLetter, nextNumber))) {
            throw new GameProcessException("Checker can't go back");
        }

        field.moveChecker(prevLetter, prevNumber, nextLetter, nextNumber);
        if (players[turnOrder].getStartPoint().getLetter() - nextLetter == 0) {
            field.getChecker(nextLetter, nextNumber).becomeKing();
        }

        changeTurnOrder();
    }

    private boolean canMakeStep(Field.Cell curCell, Field.Cell nextCell) {
        if (Math.abs(players[turnOrder].getStartPoint().getNumber() - curCell.getNumber()) >=
                Math.abs(players[turnOrder].getStartPoint().getNumber() - nextCell.getNumber())) {
            return true;
        }

        return curCell.getCheck().isKing() &&
            Math.abs(curCell.getLetter() - nextCell.getLetter()) ==
                    Math.abs(curCell.getNumber() - nextCell.getNumber());
    }

    public ArrayList<Checker> attackCheckers(ArrayList<Field.Cell> way) throws GameProcessException {
        if (field.getCell(way.get(0)).hasCheck() &&
                field.getCell(way.get(0)).getCheck().getPlayerID() != players[turnOrder].id()) {
            throw new GameProcessException("Player doesn't have checkers on this position");
        }
        ArrayList<Checker> eatenChecks =
                way.get(0).hasCheck() ? attackByKing(way) : attackBySimpleCHeck(way);
        field.moveChecker(way.get(0), way.get(way.size() - 1));
        changeTurnOrder();
        players[turnOrder].removeCheck(eatenChecks.toArray(new Checker[0]));
        return eatenChecks;
    }

    private ArrayList<Checker> attackBySimpleCHeck(ArrayList<Field.Cell> way) throws GameProcessException {
        ArrayList<Checker> eatenChecks = new ArrayList<>();

        for (int i = 0; i < way.size() - 1; i++) {
            if (!field.areOnDirectLine(way.get(i), way.get(i + 1))
                    || Math.abs(way.get(i).getLetter() - way.get(i + 1).getLetter()) != 2){
                throw new GameProcessException("Simple check can't make this move");
            } else if (field.getCell(way.get(i + 1)).hasCheck()) {
                throw new GameProcessException("Check can not attack enemy if there is another check behind");
            } else if (!field.getCellBetweenTwoCells(way.get(i), way.get(i + 1)).hasCheck()) {
                throw new GameProcessException("There is not enemy check on attack way");
            }

            eatenChecks.add(field.getCellBetweenTwoCells(way.get(i), way.get(i + 1)).getCheck());
            if (Math.abs(way.get(i + 1).getLetter() - players[turnOrder].getStartPoint().getLetter()) == 8) {
                way.get(0).getCheck().becomeKing();
                eatenChecks.addAll(attackByKing(way.subList(i + 1, way.size() - 1)));
                break;
            }
        }
        return eatenChecks;
    }

    private ArrayList<Checker> attackByKing(List<Field.Cell> way) throws GameProcessException {
        ArrayList<Checker> eatenChecks = new ArrayList<>();

        for (int i = 0; i < way.size() - 1; i++) {
            ArrayList<Field.Cell> directWay = field.getWayBetweenCells(way.get(i), way.get(i + 1));

            if (directWay.get(directWay.size() - 1).hasCheck()) {
                throw new GameProcessException("There are checkers on the way");
            }

            if (!canAttack(directWay)) {
                throw new GameProcessException("Checker can't attack another check " +
                        "if there is check after enemy");
            }
            eatenChecks.addAll(field.checkersOnLine(directWay));
        }
        return eatenChecks;
    }

    private boolean canAttack(ArrayList<Field.Cell> directWay) {
        boolean hasCheckerOnDirection,
                result = false;

        for (int i = 1; i < directWay.size() - 1; i++) {
            hasCheckerOnDirection = directWay.get(i).hasCheck();
            if (hasCheckerOnDirection) {
                result = true;
                if (directWay.get(i + 1).hasCheck()) {
                    return false;
                }
            }
        }
        return result;
    }

    public boolean gameOver() {
        return players[0].getCheckers().length == 0 || players[1].getCheckers().length == 0;
    }

    private void changeTurnOrder() {
        turnOrder = (turnOrder + 1) % players.length;
    }
}
