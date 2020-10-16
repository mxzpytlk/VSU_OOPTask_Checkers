package vsu.course2;

import exceptions.GameProcessException;

import java.util.ArrayList;

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
        changeTurnOrder();
        return eatenChecks;
    }

    private ArrayList<Checker> attackByKing(ArrayList<Field.Cell> way) throws GameProcessException {
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

//    private ArrayList<Checker> attackedCheckers(Field.Cell curCell, Field.Cell nextCell)
//            throws GameProcessException {
//
//        if (nextCell.getCheck() != null) {
//            throw new GameProcessException("Check can't be attacked if another check stay behind it");
//        }
//
//        ArrayList<Checker> result = curCell.getCheck().isKing() ?
//                attackedCheckersByKing(curCell, nextCell, new Stack<>()) :
//                attackedCheckers(curCell, nextCell, new Stack<>());
//
//        if (result.isEmpty()) {
//            throw new GameProcessException("Check can't attack on this way");
//        }
//
//        return result;
//    }

//    private ArrayList<Checker> attackedCheckers(Field.Cell curCell, Field.Cell nextCell,
//                            Stack<Field.Cell> attackedCells) {
//
//        for (Field.Cell neighbour : field.neighboringCells(curCell)) {
//            try {
//                if (canAttack(curCell, neighbour, attackedCells)) {
//
//                    Field.Cell emptyCell = null;
//                    try {
//                        emptyCell = field.skip(curCell, neighbour);
//                        if (emptyCell.hasCheck()) {
//                            break;
//                        }
//                    } catch (GameProcessException e) {
//                        e.printStackTrace();
//                    }
//
//                    attackedCells.push(neighbour);
//                    for (Field.Cell visitedCell : field.neighboringCells(emptyCell)) {
//                        if (visitedCell.getCheck() != null || visitedCell.equals(curCell)) {
//                            continue;
//                        }
//
//                        ArrayList<Checker> result =
//                                getAttackWay(nextCell, attackedCells, visitedCell);
//                        if (!result.isEmpty()) {
//                            return result;
//                        }
//                    }
//                    attackedCells.pop();
//                }
//            } catch (GameProcessException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return new ArrayList<>();
//    }

//    private ArrayList<Checker> getAttackWay(Field.Cell nextCell, Stack<Field.Cell> attackedCells,
//                                                   Field.Cell visitedCell) {
//        if (visitedCell.equals(nextCell)) {
//            return attackedCellsToEatenCheckersList(attackedCells);
//        } else {
//            return attackedCheckers(visitedCell, nextCell, attackedCells);
//        }
//    }

//    private ArrayList<Checker> attackedCellsToEatenCheckersList(Stack<Field.Cell> attackedCells) {
//        ArrayList<Checker> result = new ArrayList<>();
//
//        for (Field.Cell attackedCell : attackedCells) {
//            result.add(attackedCell.getCheck());
//        }
//        return result;
//    }

//    private boolean canAttack(Field.Cell from, Field.Cell nextCell, Stack<Field.Cell> attackedCells)
//            throws GameProcessException {
//        return nextCell.getCheck() != null && nextCell.getCheck().getPlayerID() != players[turnOrder].id()
//                && !attackedCells.contains(nextCell) && field.skip(from, nextCell).getCheck() == null;
//    }


    //Доделать
//    private ArrayList<Checker> attackedCheckersByKing (Field.Cell curCell, Field.Cell nextCell,
//                                                Stack<Field.Cell> attackedCells) {
//
//        for (Field.Cell neighbour : field.neighboringCells(curCell)) {
//            boolean hasCheckerOnDirection = false;
//
//            try {
//                //Метод обрабатывает только ту ситуацию, когда дамка бьет по пути проодящему из данной клетки
//                for (Field.Cell cell : field.getDirection(curCell, neighbour)) {
//                    //На линии находится шашка игрока
//                    if (cell.getCheck().getPlayerID() == players[turnOrder].id()) {
//                        break;
//                    }
//
//                    //Проверяет путь на вражеские шашки
//                    if (cell.getCheck() != null &&
//                            !attackedCells.contains(cell) && !hasCheckerOnDirection) {
//                        hasCheckerOnDirection = true;
//                    } //проверяет следующую клетку на наличе шашки, если за шашкой, стоит другая шашка
//                    //то дамка ее бить не может и атаку в данном направлении стоит прекратить, иначе
//                    //следует с этой клетки
//                    else if (hasCheckerOnDirection) {
//                        if (cell.equals(nextCell)) {
//                            return attackedCellsToEatenCheckersList(attackedCells);
//                        } else if (cell.hasCheck()) {
//
//                        }
//                    }
//                }
//
//            } catch(GameProcessException ignore) {}
//        }
//
//        return new ArrayList<>();
//    }

    public boolean gameOver() {
        return players[0].getCheckers().length == 0 || players[1].getCheckers().length == 0;
    }

    private void changeTurnOrder() {
        turnOrder = (turnOrder + 1) % players.length;
    }
}
