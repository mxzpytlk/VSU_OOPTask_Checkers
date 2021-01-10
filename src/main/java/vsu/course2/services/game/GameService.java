package vsu.course2.services.game;

import vsu.course2.models.game.Game;
import vsu.course2.models.game.exceptions.*;
import vsu.course2.models.game.field.Cell;
import vsu.course2.models.game.field.Field;
import vsu.course2.services.FieldService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.abs;

public class GameService {
    private final FieldService fs = new FieldService();
    private final MoveService ms = new MoveService();
    private final AttackService as = new AttackService();

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

        if (!game.getPlayers()[game.getTurnOrder()].hasCheck(fs.getChecker(prevLetter, prevNumber, game.getField())))
            throw new CellNotHaveChecksException("Player doesn't have checkers on this position");

        if (!canMakeStep(game, fs.getCell(prevLetter, prevNumber, game.getField()),
                fs.getCell(nextLetter, nextNumber, game.getField()))) {
            throw new SimpleCheckGoBackException("Checker can't go back");
        }

        if (playerCanHitEnemy(game)) {
            throw new MovementWhileAttackCanBeCarriedOutException("Check can't move if another check can attack enemy.");
        }
        ms.doStep(game, prevLetter, prevNumber, nextLetter, nextNumber);
    }

    /**
     * Check if current player can attack enemy.
     * @param game Current game.
     * @return True if current player can attack enemy.
     */
    private boolean playerCanHitEnemy(Game game) {
        var allWaysForCells = new LinkedList<>(getPossibleWays(game).values());
        var possibleWay = allWaysForCells.get(0).get(0);
        try {
            ;
            for (Cell cell : fs.getWayBetweenCells(game.getField(), possibleWay.get(0), possibleWay.get(1))) {
                if (cell.hasCheck()) {
                    return true;
                }
            }
        } catch (GameProcessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if cells situated un direct line and player can make step.
     * @param game Current game.
     * @param curCell Cell from which step possibility is checking.
     * @param nextCell Cell from which step possibility is checking.
     * @return True if cells situated un direct line and player can make step.
     */
    private boolean canMakeStep(Game game, Cell curCell, Cell nextCell) {
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
    public void attackCheckers(Game game, List<Cell> way) throws GameProcessException {
        as.attackCheckers(game, way);
    }

    /**
     * Check if one player does not have check.
     * @param game Current game.
     * @return True if one player does not have check.
     */
    public boolean gameOver(Game game) {
        return game.getPlayers()[0].getCheckers().length == 0 || game.getPlayers()[1].getCheckers().length == 0;
    }

    public Map<Cell, List<List<Cell>>> getPossibleWays(Game game) {
        Field field = game.getField();
        Map<Cell, List<List<Cell>>> attacks = new HashMap<>();
        Map<Cell, List<List<Cell>>> steps = new HashMap<>();
        for (Cell cell : field) {
            if (cell.getCheck() != null && game.getCurrentPlayer().hasCheck(cell.getCheck())
                    && !cell.getCheck().isKing()) {
                List<List<Cell>> possibleWays = as.getPossibleAttacksToSimpleCheck(cell, game);
                if (possibleWays.size() > 0) {
                    attacks.put(cell, possibleWays);
                }
            }

            if (cell.getCheck() != null && game.getCurrentPlayer().hasCheck(cell.getCheck())
                    && cell.getCheck().isKing()) {
                List<List<Cell>> possibleWays = as.getPossibleAttacksToKing(cell, game);
                if (possibleWays.size() > 0) {
                    attacks.put(cell, possibleWays);
                }
            }

            if (cell.hasCheck() && !cell.getCheck().isKing()
                    && game.getCurrentPlayer().hasCheck(cell.getCheck()) &&  attacks.size() == 0) {


                List<List<Cell>> possibleWays = ms.getPossibleWaysToSimpleCheck(cell, game);
                if (possibleWays.size() > 0) {
                    steps.put(cell, possibleWays);
                }
            }

            if (cell.hasCheck() && cell.getCheck().isKing()
                    && game.getCurrentPlayer().hasCheck(cell.getCheck()) &&  attacks.size() == 0) {

                List<List<Cell>> possibleWays = ms.getPossibleWaysToKing(cell, game);
                if (possibleWays.size() > 0) {
                    steps.put(cell, possibleWays);
                }
            }
        }
        return attacks.size() > 0 ? attacks : steps;
    }
}
