package vsu.course2.services;

import vsu.course2.models.game.Checker;
import vsu.course2.models.game.Game;
import vsu.course2.models.game.Player;
import vsu.course2.models.game.TwoDimensionalDirection;
import vsu.course2.models.game.exceptions.*;
import vsu.course2.models.game.field.Cell;
import vsu.course2.models.game.field.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.abs;

public class SimpleCheckMoveService {
    private final GameService gs = new GameService();
    private final FieldService fs = new FieldService();
    private final KingMoveService kms = new KingMoveService();

    /**
     * Check if current player have simple check, which could attack enemy check.
     * @param game Current game.
     * @return True if current player have simple check, which could attack enemy check.
     */
    public boolean playerCanHitEnemyBySimpleCheck(Game game) {
        try {
            if (checkPlayerCanHitBySimple(game)) return true;
        } catch (GameProcessException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Check if current player have simple check, which could attack enemy check.
     * @param game Current game.
     * @return True if current player have simple check, which could attack enemy check.
     * @throws CellNotExistException Thrown if method implementation had logic mistakes.
     */
    private boolean checkPlayerCanHitBySimple(Game game) throws CellNotExistException {
        Field field = game.getField();
        Player curPlayer = game.getCurrentPlayer();
        Cell playerStartPoint = game.getCurrentPlayer().getStartPoint();
        TwoDimensionalDirection leftDirection = game.getCurrentPlayer().getStartPoint()
                .equals(fs.getCell(0, 0, field)) ?
                TwoDimensionalDirection.UP_LEFT : TwoDimensionalDirection.DOWN_RIGHT;
        TwoDimensionalDirection rightDirection = game.getCurrentPlayer().getStartPoint()
                .equals(fs.getCell(0, 0, field)) ?
                TwoDimensionalDirection.UP_RIGHT : TwoDimensionalDirection.DOWN_LEFT;

        for (Cell cell : field) {
            if (cell.hasCheck() && curPlayer.hasCheck(cell.getCheck())
                    && abs(cell.getNumber() - playerStartPoint.getNumber()) < field.getHeight() - 2) {
                if (abs(playerStartPoint.getLetter() - cell.getLetter()) > 1 &&
                        gs.checkOnNextCellExist(game, cell, leftDirection) &&
                        game.getEnemyPlayer().hasCheck(gs.getNextCell(game, cell, leftDirection).getCheck()) &&
                        !fs.getCell(cell.getLetter() + 2 * leftDirection.getHorizontalCoef(),
                                cell.getNumber() + 2 * leftDirection.getVerticalCoef(), field).hasCheck()) {
                    return true;
                } else if (abs(cell.getLetter() - playerStartPoint.getLetter()) < field.getWidth() - 2 &&
                        gs.checkOnNextCellExist(game, cell, rightDirection) &&
                        game.getEnemyPlayer().hasCheck(gs.getNextCell(game, cell, rightDirection).getCheck()) &&
                        !fs.getCell(cell.getLetter() + 2 * rightDirection.getHorizontalCoef(),
                                cell.getNumber() + 2 * rightDirection.getVerticalCoef(), field).hasCheck()) {
                    return true;
                }

            }
        }
        return false;
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
    public void attackBySimpleCHeck(Game game, List<Cell> way) throws GameProcessException {
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
                    eatenChecks.addAll(kms.attackByKing(game, way.subList(i + 1, way.size() - 1)));
                }
                break;
            }
        }
        game.getEnemyPlayer().removeCheck(eatenChecks.toArray(new Checker[0]));
        fs.moveChecker(field, way.get(0), way.get(way.size() - 1));
    }

    public List<List<Cell>> getPossibleWaysToSimpleCheck(Cell cell, Game game) {
        List<List<Cell>> ways = new ArrayList<>();
        Field field = game.getField();

        Cell playerStartPoint =  game.getCurrentPlayer().getStartPoint();

        if (abs(cell.getNumber() - playerStartPoint.getNumber()) != field.getHeight() - 1) {
            try {
                TwoDimensionalDirection direction = playerStartPoint.equals(fs.getCell(0, 0, field)) ?
                        TwoDimensionalDirection.UP : TwoDimensionalDirection.DOWN;
                if (cell.getLetter() - playerStartPoint.getLetter() != 0 &&
                        !fs.getCell(cell.getLetter() - direction.getVerticalCoef(),
                                cell.getNumber() + direction.getVerticalCoef(), field).hasCheck()) {

                    ways.add(Arrays.asList(cell, fs.getCell(cell.getLetter() - direction.getVerticalCoef(),
                            cell.getNumber() + direction.getVerticalCoef(), field)));
                } else if(abs(playerStartPoint.getLetter() - cell.getLetter()) != field.getHeight() -  1 &&
                        !fs.getCell(cell.getLetter() + direction.getVerticalCoef(), cell.getNumber()
                                + direction.getVerticalCoef(), field).hasCheck()) {

                    ways.add(Arrays.asList(cell, fs.getCell(cell.getLetter() + direction.getVerticalCoef(),
                            cell.getNumber() + direction.getVerticalCoef(), field)));
                }
            } catch (CellNotExistException e) {
                e.printStackTrace();
            }
        }

        return ways;
    }

    public List<List<Cell>> getPossibleAttacksToSimpleCheck(Cell cell, Game game) {
        List<List<Cell>> ways = new ArrayList<>();
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
        return ways;
    }

}
