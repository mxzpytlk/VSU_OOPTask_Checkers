package vsu.course2.services.game;

import vsu.course2.models.game.Game;
import vsu.course2.models.game.TwoDimensionalDirection;
import vsu.course2.models.game.exceptions.*;
import vsu.course2.models.game.field.Cell;
import vsu.course2.models.game.field.Field;
import vsu.course2.services.FieldService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.abs;

public class MoveService {
    private final FieldService fs = new FieldService();

    /**
     * Move check from first cell to second.
     * @param game Current game.
     * @param prevLetter Previous cell letter.
     * @param prevNumber Previous cell number.
     * @param nextLetter Next cell letter.
     * @param nextNumber Next cell number.
     * @throws CellNotExistException Thrown if previous or next cell are not exist.
     * @throws CellNotHaveChecksException Thrown if player don't have checks on previous cell.
     * @throws CellIsNotFreeException Thrown if next cell is not free.
     */
    public void doStep(Game game, int prevLetter, int prevNumber, int nextLetter, int nextNumber)
            throws
            CellNotExistException, CellNotHaveChecksException, CellIsNotFreeException {

        fs.moveChecker(game.getField(), prevLetter, prevNumber, nextLetter, nextNumber);
        if (abs(game.getCurrentPlayer().getStartPoint().getNumber() - nextNumber) == game.getField().getHeight() - 1) {
            fs.getChecker(nextLetter, nextNumber, game.getField()).becomeKing();
        }

        game.changeTurnOrder();
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
                }
                if(abs(playerStartPoint.getLetter() - cell.getLetter()) != field.getHeight() -  1 &&
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

    public List<List<Cell>> getPossibleWaysToKing(Cell cell, Game game) {
        List<List<Cell>> ways = new LinkedList<>();
        TwoDimensionalDirection[] directions = {
                TwoDimensionalDirection.DOWN_LEFT,
                TwoDimensionalDirection.DOWN_RIGHT,
                TwoDimensionalDirection.UP_RIGHT,
                TwoDimensionalDirection.UP_LEFT
        };

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
}
