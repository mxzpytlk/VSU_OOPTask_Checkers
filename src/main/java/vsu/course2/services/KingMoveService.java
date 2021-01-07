package vsu.course2.services;

import vsu.course2.models.game.Checker;
import vsu.course2.models.game.Game;
import vsu.course2.models.game.TwoDimensionalDirection;
import vsu.course2.models.game.exceptions.CellNotExistException;
import vsu.course2.models.game.exceptions.GameProcessException;
import vsu.course2.models.game.field.Cell;
import vsu.course2.models.game.field.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class KingMoveService {
    private final FieldService fs = new FieldService();
    private final GameService gs = new GameService();
    /**
     * Make one player attack another by king check. Find enemy checks which should be deleted. Remove attacked checks
     * from enemy players list and field. Move check on first position in list to last position in list.
     * @param game Current game.
     * @param way List with cells where player should have check which make attack. First position is players check
     *            which make attack other is cells where players check stay after each enemy check attack.
     * @throws GameProcessException Thrown if check try to attack enemy check which is far from, try to go back or try
     * to attack empty cell.
     */
    public ArrayList<Checker> attackByKing(Game game, List<Cell> way) throws GameProcessException {
        Field field = game.getField();
        ArrayList<Checker> eatenChecks = new ArrayList<>();

        for (int i = 0; i < way.size() - 1; i++) {
            ArrayList<Cell> directWay = fs.getWayBetweenCells(field, way.get(i), way.get(i + 1));

            if (way.get(i + 1).hasCheck()) {
                throw new GameProcessException("There are checkers on the way");
            }

            eatenChecks.addAll(fs.checkersOnLine(directWay));
            for (Cell cell : directWay) {
                cell.removeCheck();
            }
        }

        game.getEnemyPlayer().removeCheck(eatenChecks.toArray(new Checker[0]));
        fs.moveChecker(field, way.get(0), way.get(way.size() - 1));
        return eatenChecks;
    }


    public List<List<Cell>> getPossibleWaysToKing(Cell cell, Game game) {
        List<List<Cell>> ways = new LinkedList<>();
        TwoDimensionalDirection[] directions = {TwoDimensionalDirection.DOWN_LEFT,
                TwoDimensionalDirection.DOWN_RIGHT,
                TwoDimensionalDirection.UP_RIGHT,
                TwoDimensionalDirection.UP_LEFT};

        for (TwoDimensionalDirection direction : directions) {
            List<Cell> way = fs.getWayToBoard(cell, direction, game.getField());
            for (Cell nextPos : way) {
                if (nextPos.hasCheck()) {
                    break;
                }
                ways.add(Arrays.asList(cell, nextPos));
            }
        }
        return ways;
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
                if (wayToBoard.get(i).getCheck() != null
                        && (game.getCurrentPlayer().hasCheck(wayToBoard.get(i).getCheck())
                        || wayToBoard.get(i + 1).getCheck() != null)) {
                    break;
                }

                try {
                    if (wayToBoard.get(i).hasCheck() && wayToBoard.get(i + 1).getCheck() == null
                            && game.getEnemyPlayer().hasCheck(wayToBoard.get(i).getCheck())) {
                        ways.add(Arrays.asList(cell, gs.getNextCell(game, wayToBoard.get(i), direction)));
                        i++;
                        shouldAddCell = true;
                    } else if (shouldAddCell) {
                        ways.add(Arrays.asList(cell, gs.getNextCell(game, wayToBoard.get(i), direction)));
                    }
                } catch (CellNotExistException e) {
                    e.printStackTrace();
                }
            }
        }

        return ways;
    }
}
