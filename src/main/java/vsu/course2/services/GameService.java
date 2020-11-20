package vsu.course2.services;

import vsu.course2.game.*;
import vsu.course2.game.exceptions.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class GameService {
    private final FieldService fs = new FieldService();

    public GameService() { }

    /**
     * Move check from first cell to second.
     * @param game Current game.
     * @param prevLetter Previous cell letter.
     * @param prevNumber Previous cell number.
     * @param nextLetter Next cell letter.
     * @param nextNumber Next cell number.
     * @throws SimpleCheckGoBackException Thrown if player try move simple check back.
     * @throws MovementWhileAttackCanBeCarriedOutException Thrown if player try make step while can attack enemy.
     * @throws CellNotExistException Thrown if previous or next cell are not exist.
     * @throws CellNotHaveChecksException Thrown if player don't have checks on previous cell.
     * @throws CellIsNotFreeException Thrown if next cell is not free.
     */
    public void doStep(Game game, int prevLetter, int prevNumber, int nextLetter, int nextNumber)
            throws SimpleCheckGoBackException, MovementWhileAttackCanBeCarriedOutException,
            CellNotExistException, CellNotHaveChecksException, CellIsNotFreeException {
        if (gameOver(game)) return;

        if (!game.getPlayers()[game.getTurnOrder()].hasCheck(game.getField().getChecker(prevLetter, prevNumber)))
            throw new CellNotHaveChecksException("Player doesn't have checkers on this position");

        if (!canMakeStep(game, game.getField().getCell(prevLetter, prevNumber),
                game.getField().getCell(nextLetter, nextNumber))) {
            throw new SimpleCheckGoBackException("Checker can't go back");
        }

        if (playerCanHitEnemy(game)) {
            throw new MovementWhileAttackCanBeCarriedOutException("Check can't move if another check can attack enemy.");
        }

        fs.moveChecker(game.getField(), prevLetter, prevNumber, nextLetter, nextNumber);
        if (abs(game.getCurrentPlayer().getStartPoint().getNumber() - nextNumber) == game.getField().getHeight() - 1) {
            game.getField().getChecker(nextLetter, nextNumber).becomeKing();
        }

        game.changeTurnOrder();
    }

    /**
     * Check if current player can attack enemy.
     * @param game Current game.
     * @return True if current player can attack enemy.
     */
    private boolean playerCanHitEnemy(Game game) {
        return playerCanHitEnemyBySimpleCheck(game) || playerCanHitEnemyByKing(game);
    }

    private boolean playerCanHitEnemyByKing(Game game) {
        //TODO:
        return false;
    }

    /**
     * Check if current player have simple check, which could attack enemy check.
     * @param game Current game.
     * @return True if current player have simple check, which could attack enemy check.
     */
    private boolean playerCanHitEnemyBySimpleCheck(Game game) {
        Field field = game.getField();
        int playerID = game.getCurrentPlayer().getPlayerID();
        Field.Cell playerStartPoint =  game.getCurrentPlayer().getStartPoint();
        TwoDimensionalDirection leftDirection = game.getCurrentPlayer().getStartPoint()
                .equals(new Field.Cell(0, 0)) ?
                TwoDimensionalDirection.UP_LEFT : TwoDimensionalDirection.DOWN_RIGHT;
        TwoDimensionalDirection rightDirection = game.getCurrentPlayer().getStartPoint()
                .equals(new Field.Cell(0, 0)) ?
                TwoDimensionalDirection.UP_RIGHT : TwoDimensionalDirection.DOWN_LEFT;

        for (Field.Cell cell : field) {
            if (cell.hasCheck() && cell.getCheck().getPlayerID() == playerID
                    && abs(cell.getNumber() - playerStartPoint.getNumber()) < field.getHeight() - 2) {
                try {
                    if (abs(playerStartPoint.getLetter() - cell.getLetter()) > 1 &&
                            checkOnNextCellExist(game, cell, leftDirection) &&
                        getNextCell(game, cell, leftDirection).getCheck().getPlayerID() != playerID &&
                        !field.getCell(cell.getLetter() + 2 * leftDirection.getHorizontalCoef(),
                                        cell.getNumber() + 2 * leftDirection.getVerticalCoef()).hasCheck()) {
                        return true;
                    } else if (abs(cell.getLetter() - playerStartPoint.getLetter()) < field.getWidth() - 2 &&
                            checkOnNextCellExist(game, cell, rightDirection) &&
                            getNextCell(game, cell, rightDirection).getCheck().getPlayerID() != playerID &&
                            !field.getCell(cell.getLetter() + 2 * rightDirection.getHorizontalCoef(),
                                    cell.getNumber() + 2 * rightDirection.getVerticalCoef()).hasCheck()) {
                        return true;
                    }
                } catch (GameProcessException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    /**
     * Find next cell on direction.
     * @param game Current game.
     * @param curCell Cell, from which next cells is found,
     * @param direction Direction in which next cell is found.
     * @return Next cell on direction.
     * @throws CellNotExistException Thrown if cell is not exist.
     */
    public Field.Cell getNextCell(Game game, Field.Cell curCell, TwoDimensionalDirection direction)
            throws CellNotExistException {
        Field field = game.getField();
        return field.getCell(curCell.getLetter() + direction.getHorizontalCoef(),
                    curCell.getNumber() + direction.getVerticalCoef());
    }

    /**
     * Check if enemy checks next cell in direction exist.
     * @param game Current game.
     * @param curCell Cell which is checked.
     * @param direction Direction, where function check checker existing.
     * @return True, if check on next cell exist .
     */
    public boolean checkOnNextCellExist(Game game, Field.Cell curCell, TwoDimensionalDirection direction) {
        try {
            return getNextCell(game, curCell, direction).hasCheck();
        } catch (CellNotExistException e) {
            return false;
        }
    }

    /**
     * Check if cells situated un direct line and player can make step.
     * @param game Current game.
     * @param curCell Cell from which step possibility is checking.
     * @param nextCell Cell from which step possibility is checking.
     * @return True if cells situated un direct line and player can make step.
     */
    private boolean canMakeStep(Game game, Field.Cell curCell, Field.Cell nextCell) {
        if (abs(game.getCurrentPlayer().getStartPoint().getNumber() - curCell.getNumber()) >=
                abs(game.getCurrentPlayer().getStartPoint().getNumber() - nextCell.getNumber())) {
            return true;
        }

        return abs(curCell.getLetter() - nextCell.getLetter()) ==
                        abs(curCell.getNumber() - nextCell.getNumber());
    }

    /**
     * Make one player attack another. Find enemy checks which should be deleted. Remove attacked checks from
     * enemy players list and field. Move check on first position in list to last position in list.
     * @param game Current game.
     * @param way
     *      List with cells where player should have check which make attack. First position is players check
     *      which make attack other is cells where players check stay after each enemy check attack.
     * @throws GameProcessException
     *      Thrown if check try to attack enemy check which is far from, try to go back or try to attack empty cell.
     */
    public void attackCheckers(Game game, List<Field.Cell> way) throws GameProcessException {
        Field field = game.getField();
        Player[] players = game.getPlayers();

        if (field.getCell(way.get(0)).hasCheck() &&
                field.getCell(way.get(0)).getCheck().getPlayerID() != players[game.getTurnOrder()].id()) {
            throw new GameProcessException("Player doesn't have checkers on this position");
        }

        if (way.get(0).getCheck().isKing()) {
            attackByKing(game, way);
        } else {
            attackBySimpleCHeck(game, way);
        }
        game.changeTurnOrder();
    }

    /**
     * Make one player attack another by simple check. Find enemy checks which should be deleted. Remove attacked checks
     * from enemy players list and field. Move check on first position in list to last position in list.
     * @param game Current game.
     * @param way List with cells where player should have check which make attack. First position is players check
     *            which make attack other is cells where players check stay after each enemy check attack. Cells must be
     *            separate by only one cell with enemies check.
     * @throws GameProcessException Thrown if check try to attack enemy check which is far from, try to go back or try
     * to attack empty cell.
     */
    private void attackBySimpleCHeck(Game game, List<Field.Cell> way) throws GameProcessException {
        Field field = game.getField();

        ArrayList<Checker> eatenChecks = new ArrayList<>();

        for (int i = 0; i < way.size() - 1; i++) {
            if (!fs.areOnDirectLine(way.get(i), way.get(i + 1))
                    || abs(way.get(i).getLetter() - way.get(i + 1).getLetter()) != 2) {
                throw new CellsAreNotOnDirectLineException(way.get(i).toString() + way.get(i + 1).toString() +
                        " are mot on direct line.");
            } else if (field.getCell(way.get(i + 1)).hasCheck()) {
                throw new CellIsNotFreeException(way.get(i).toString() + way.get(i + 1).toString() +
                        "\nCheck can not attack enemy if there is another check behind");
            } else if (!fs.getCellBetweenTwoCells(field, way.get(i), way.get(i + 1)).hasCheck()) {
                throw new CellIsEmptyException(way.get(i).toString() + way.get(i + 1).toString() +
                        "There is not enemy check on attack way");
            }
            fs.getCellBetweenTwoCells(field, way.get(i), way.get(i + 1)).removeCheck();
            eatenChecks.add(fs.getCellBetweenTwoCells(field ,way.get(i), way.get(i + 1)).getCheck());
            if (abs(way.get(i + 1).getNumber() - game.getCurrentPlayer().getStartPoint().getNumber()) ==
                    field.getHeight() - 1) {
                way.get(0).getCheck().becomeKing();
                eatenChecks.addAll(attackByKing(game, way.subList(i + 1, way.size() - 1)));
                break;
            }
        }
        game.getEnemyPlayer().removeCheck(eatenChecks.toArray(new Checker[0]));
        fs.moveChecker(field, way.get(0), way.get(way.size() - 1));
    }

    /**
     * Make one player attack another by king check. Find enemy checks which should be deleted. Remove attacked checks
     * from enemy players list and field. Move check on first position in list to last position in list.
     * @param game Current game.
     * @param way List with cells where player should have check which make attack. First position is players check
     *            which make attack other is cells where players check stay after each enemy check attack.
     * @throws GameProcessException Thrown if check try to attack enemy check which is far from, try to go back or try
     * to attack empty cell.
     */
    private ArrayList<Checker> attackByKing(Game game, List<Field.Cell> way) throws GameProcessException {
        Field field = game.getField();
        ArrayList<Checker> eatenChecks = new ArrayList<>();

        for (int i = 0; i < way.size() - 1; i++) {
            ArrayList<Field.Cell> directWay = fs.getWayBetweenCells(field, way.get(i), way.get(i + 1));

            if (directWay.get(directWay.size() - 1).hasCheck()) {
                throw new GameProcessException("There are checkers on the way");
            }

            if (!canAttack(directWay)) {
                throw new GameProcessException("Checker can't attack another check " +
                        "if there is check after enemy");
            }
            eatenChecks.addAll(fs.checkersOnLine(directWay));
        }
        return eatenChecks;
    }

    /**
     * Check if simple checker can attack enemy by way.
     * @param directWay List with cells, which is on players attack way.
     * @return True if simple checker can attack enemy by way.
     */
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

    /**
     * Check if one player does not have check.
     * @param game Current game.
     * @return True if one player does not have check.
     */
    public boolean gameOver(Game game) {
        Field field = game.getField();
        int firstPlayerID = game.getCurrentPlayer().getPlayerID();
        int secondPlayerID = game.getEnemyPlayer().getPlayerID();
        boolean firstPlayerHasChecks = false;
        boolean secondPlayerHasChecks = false;
        for (Field.Cell cell : field) {
            firstPlayerHasChecks = firstPlayerHasChecks ||
                    (cell.hasCheck() && cell.getCheck().getPlayerID() == firstPlayerID);
            secondPlayerHasChecks = secondPlayerHasChecks ||
                    (cell.hasCheck() && cell.getCheck().getPlayerID() == secondPlayerID);
            if (firstPlayerHasChecks && secondPlayerHasChecks) {
                return false;
            }
        }
        return true;
    }
}
