package vsu.course2.services.game;

import vsu.course2.models.game.Checker;
import vsu.course2.models.game.Game;
import vsu.course2.models.game.TwoDimensionalDirection;
import vsu.course2.models.game.exceptions.*;
import vsu.course2.models.game.field.Cell;
import vsu.course2.models.game.field.Field;
import vsu.course2.services.FieldService;
import vsu.course2.utills.CloneService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.abs;

public class AttackService {
    private final FieldService fs = new FieldService();

    public void attackCheckers(Game game, List<Cell> way) throws GameProcessException {
        Field field = game.getField();

        if (fs.getCell(way.get(0), field).hasCheck() &&
                game.getEnemyPlayer().hasCheck(fs.getCell(way.get(0), field).getCheck())) {
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
    private void attackBySimpleCHeck(Game game, List<Cell> way) throws GameProcessException {
        Field field = game.getField();

        ArrayList<Checker> eatenChecks = new ArrayList<>();

        for (int i = 0; i < way.size() - 1; i++) {
            if (!fs.areOnDirectLine(way.get(i), way.get(i + 1))
                    || abs(way.get(i).getLetter() - way.get(i + 1).getLetter()) != 2) {
                throw new CellsAreNotOnDirectLineException(way.get(i).toString() + way.get(i + 1).toString() +
                        " are mot on direct line.");
            } else if (fs.getCell(way.get(i + 1), field).hasCheck()) {
                throw new CellIsNotFreeException(way.get(i).toString() + way.get(i + 1).toString() +
                        "\nCheck can not attack enemy if there is another check behind");
            } else if (!fs.getCellBetweenTwoCells(field, way.get(i), way.get(i + 1)).hasCheck()) {
                throw new CellIsEmptyException(way.get(i).toString() + way.get(i + 1).toString() +
                        "There is not enemy check on attack way");
            }
            eatenChecks.add(fs.getCellBetweenTwoCells(field ,way.get(i), way.get(i + 1)).getCheck());
            fs.getCellBetweenTwoCells(field, way.get(i), way.get(i + 1)).removeCheck();
            if (abs(way.get(i + 1).getNumber() - game.getCurrentPlayer().getStartPoint().getNumber()) ==
                    field.getHeight() - 1) {
                way.get(0).getCheck().becomeKing();
                if (i < way.size() - 2) {
                    eatenChecks.addAll(attackByKing(game, way.subList(i + 1, way.size() - 1)));
                }
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
    private ArrayList<Checker> attackByKing(Game game, List<Cell> way) throws GameProcessException {
        Field field = game.getField();
        ArrayList<Checker> eatenChecks = new ArrayList<>();

        for (int i = 0; i < way.size() - 1; i++) {
            ArrayList<Cell> directWay = fs.getWayBetweenCells(field, way.get(i), way.get(i + 1));

            eatenChecks.addAll(fs.checkersOnLine(directWay));
            for (Cell cell : directWay) {
                cell.removeCheck();
            }
        }

        game.getEnemyPlayer().removeCheck(eatenChecks.toArray(new Checker[0]));
        fs.moveChecker(field, way.get(0), way.get(way.size() - 1));
        return eatenChecks;
    }

    public List<List<Cell>> getPossibleAttacksToSimpleCheck(Cell cell, Game game) {
        List<List<Cell>> ways = new LinkedList<>();
        Field field = game.getField();

        for (Cell neighbour : fs.neighbours(cell, game.getField())) {
            try {
                TwoDimensionalDirection direction = fs.getDirectionFromStartToEnd(cell, neighbour);
                if (fs.cellExist(field, cell.getLetter() + direction.getHorizontalCoef() * 2,
                        cell.getNumber() + direction.getVerticalCoef() * 2) &&
                        fs.getCell(cell.getLetter() + direction.getHorizontalCoef(),
                                cell.getNumber() + direction.getVerticalCoef(), field).hasCheck()
                        && game.getEnemyPlayer().hasCheck(fs.getCell(cell.getLetter() + direction.getHorizontalCoef(),
                        cell.getNumber() + direction.getVerticalCoef(), field).getCheck()) &&
                        !fs.getCell(cell.getLetter() + direction.getHorizontalCoef() * 2,
                                cell.getNumber() + direction.getVerticalCoef() * 2, field).hasCheck()
                ) {
                    ways.add(Arrays.asList(cell, fs.getCell(cell.getLetter() + direction.getHorizontalCoef() * 2,
                            cell.getNumber() + direction.getVerticalCoef() * 2, field)));
                }

            } catch (GameProcessException e) {
                e.printStackTrace();
            }
        }

        return getAttackContinuation(game, ways);
    }

    public List<List<Cell>> getPossibleAttacksToKing(Cell cell, Game game) {
        List<List<Cell>> ways = new ArrayList<>();
        Field field = game.getField();
        TwoDimensionalDirection[] directions = {TwoDimensionalDirection.DOWN_LEFT,
                TwoDimensionalDirection.DOWN_RIGHT,
                TwoDimensionalDirection.UP_RIGHT,
                TwoDimensionalDirection.UP_LEFT};

        for (TwoDimensionalDirection direction : directions) {
            List<Cell> wayToBoard = fs.getWayToBoard(cell, direction, field);
            boolean shouldAddCell = false;
            for (int i = 0; i < wayToBoard.size() - 1; i++) {
                if (wayToBoard.get(i).hasCheck()
                        && (game.getCurrentPlayer().hasCheck(wayToBoard.get(i).getCheck())
                        || wayToBoard.get(i + 1).hasCheck())) {
                    break;
                }

                try {
                    if (wayToBoard.get(i).hasCheck() && wayToBoard.get(i + 1).getCheck() == null
                            && game.getEnemyPlayer().hasCheck(wayToBoard.get(i).getCheck())) {
                        ways.add(Arrays.asList(cell, getNextCell(game, wayToBoard.get(i), direction)));
                        i++;
                        shouldAddCell = true;
                    } else if (shouldAddCell) {
                        ways.add(Arrays.asList(cell, fs.getCell(wayToBoard.get(i), game.getField())));
                    }
                } catch (CellNotExistException e) {
                    e.printStackTrace();
                }
            }
        }

        return getAttackContinuation(game, ways);
    }

    private List<List<Cell>> getAttackContinuation(Game game, List<List<Cell>> ways) {
        List<List<Cell>> finalWays = new LinkedList<>();

        for (List<Cell> way : ways) {
            Game newGame = new CloneService<Game>().makeClone(game);

            try {
                attackCheckers(newGame, way);
                newGame.changeTurnOrder();
                List<List<Cell>> newWays =
                        !fs.getCell(fs.getCell(way.get(way.size() - 1), newGame.getField()), newGame.getField()).
                                getCheck().isKing() ?
                                getPossibleAttacksToSimpleCheck(fs.getCell(way.get(way.size() - 1),
                                        newGame.getField()), newGame) :
                                getPossibleAttacksToKing(fs.getCell(way.get(way.size() - 1),
                                        newGame.getField()), newGame);
                if (newWays.size() == 0) {
                    finalWays.add(way);
                }

                for (List<Cell> newWay : newWays) {
                    List<Cell> addedWay = new LinkedList<>(way);
                    addedWay.addAll(newWay.subList(1, newWay.size()));
                    finalWays.add(addedWay);
                }
            } catch (GameProcessException e) {
                e.printStackTrace();
            }
        }

        return finalWays;
    }

    /**
     * Find next cell on direction.
     * @param game Current game.
     * @param curCell Cell, from which next cells is found,
     * @param direction Direction in which next cell is found.
     * @return Next cell on direction.
     * @throws CellNotExistException Thrown if cell is not exist.
     */
    public Cell getNextCell(Game game, Cell curCell, TwoDimensionalDirection direction)
            throws CellNotExistException {
        Field field = game.getField();
        return fs.getCell(curCell.getLetter() + direction.getHorizontalCoef(),
                curCell.getNumber() + direction.getVerticalCoef(), field);
    }
}
