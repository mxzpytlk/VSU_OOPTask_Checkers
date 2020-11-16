package vsu.course2.services;

import vsu.course2.game.*;
import vsu.course2.game.exceptions.*;

import java.util.ArrayList;

public class FieldService {
    FieldService() {}

    /**
     * Move check from start cell to and cell.
     * @param field
     *      Field where checks are situated.
     * @param start
     *      Cell where check is.
     * @param end
     *      Cell where check must be.
     * @throws CellNotHaveChecksException
     *      Thrown if start cell doesn't have check.
     * @throws CellIsNotFreeException
     *      Thrown if end cell isn't free.
     * @throws CellNotExistException
     *      Thrown if start or end cell doesn't exist.
     */
    public void moveChecker(Field field, Field.Cell start, Field.Cell end)
            throws CellNotHaveChecksException, CellIsNotFreeException, CellNotExistException {
        moveChecker(field, start.getLetter(), start.getNumber(), end.getLetter(), end.getNumber());
    }

    public void moveChecker(Field field, int prevLetter, int prevNumber, int newLetter, int newNumber)
            throws CellNotHaveChecksException, CellIsNotFreeException, CellNotExistException {

        if (field.getCell(prevLetter, prevNumber).getCheck() == null) {
            throw new CellNotHaveChecksException("This cell doesn't have checker");
        }

        if (field.getCell(newLetter, newNumber).getCheck() != null) {
            throw new CellIsNotFreeException("This cell isn't free");
        }

        field.getCell(newLetter, newNumber).setCheck(field.getCell(prevLetter, prevNumber).getCheck());
        field.getCell(prevLetter, prevNumber).removeCheck();
    }

    /**
     * Find direct line on field between two cells.
     * @param field
     *      Field where cells are situated.
     * @param start
     *      First cell.
     * @param end
     *      Second cell.
     * @return
     *      List with cells which are situated on direct line on field.
     * @throws CellsAreNotOnDirectLineException
     *      Thrown if cells are not on direct line.
     */
    public ArrayList<Field.Cell> getWayBetweenCells(Field field, Field.Cell start, Field.Cell end) throws
            CellsAreNotOnDirectLineException {
        ArrayList<Field.Cell> way = new ArrayList<>();

        if (!areOnDirectLine(start, end))
            throw new CellsAreNotOnDirectLineException("Check can move only on direct line");

        int verticalDirection = getVerticalDirection(start, end);
        int horizontalDirection = getHorizontalDirection(start, end);
        for (int i = start.getLetter() + verticalDirection; i != end.getLetter() ; i += verticalDirection) {
            try {
                way.add(field
                        .getCell(i, start.getNumber() + horizontalDirection * (Math.abs(start.getLetter() - i))));
            } catch (CellNotExistException e) {
                e.printStackTrace();
            }
        }

        return way;
    }

    /**
     * Check if forward way between cells exists.
     * @param start
     *      First cell.
     * @param end
     *      Second cell.
     * @return
     *      True, if forward way between cells exists.
     */
    public boolean areOnDirectLine(Field.Cell start, Field.Cell end) {
        return Math.abs(start.getLetter() - end.getLetter()) ==
                Math.abs(start.getNumber() - end.getNumber())
                && Math.abs(start.getLetter() - end.getLetter()) != 0;
    }

    private int getVerticalDirection(Field.Cell start, Field.Cell end) {
        return start.getLetter() < end.getLetter() ? 1 : -1;
    }

    private int getHorizontalDirection(Field.Cell start, Field.Cell end) {
        return start.getNumber() < end.getNumber() ? 1 : -1;
    }

    public Field.Cell getCellBetweenTwoCells(Field field, Field.Cell first, Field.Cell second) throws GameProcessException {
        for (Field.Cell firstNeighbour : field.neighbours(first)) {
            for (Field.Cell secondNeighbour : field.neighbours(second)) {
                if (firstNeighbour.equals(secondNeighbour)) {
                    return firstNeighbour;
                }
            }
        }
        throw new GameProcessException("Cells do not have neighbour");
    }

    public ArrayList<Checker> checkersOnLine(ArrayList<Field.Cell> line) {
        ArrayList<Checker> result = new ArrayList<>();
        for (Field.Cell cell : line) {
            if (cell.hasCheck()) {
                result.add(cell.getCheck());
            }
        }
        return result;
    }

    /**
     * Find direct line on which 2 cells are situated.
     * @param field
     *      Current gamefield.
     * @param start
     *      1st cell.
     * @param end
     *      2nd cell.
     * @return
     *      ArrayList with cells on direct line in order from down to up.
     * @throws CellsAreNotOnDirectLineException
     *      Thrown if cells are not on direct line.
     */
    public ArrayList<Field.Cell> getLineWhere2CellsAreSituated(Field field, Field.Cell start, Field.Cell end)
            throws CellsAreNotOnDirectLineException {
        if (!areOnDirectLine(start, end))
            throw new CellsAreNotOnDirectLineException("Cell [" + start.getLetter() + ", " + start.getNumber() +
                    "] and [" + end.getLetter() + ", " + end.getNumber() + "]" + " are not on direct line.");

        ArrayList<Field.Cell> way = new ArrayList<>();

        Field.Cell down = start.getNumber() > end.getNumber() ? end : start;
        Field.Cell up = start.getNumber() > end.getNumber() ? start : end;

        if (down.getLetter() < up.getLetter()) {
            int leftPos = down.getNumber() >= down.getLetter() ? 0 : down.getLetter() - down.getNumber();
            int rightPos = down.getNumber() <= down.getLetter() ? field.getWidth() - 1 :
                    field.getWidth() - 1 - (down.getLetter() - down.getNumber());
            for (int i = leftPos; i <= rightPos; i++) {
                try {
                    way.add(field.getCell(i, down.getNumber() - down.getLetter() + i));
                } catch (CellNotExistException e) {
                    e.printStackTrace();
                }
            }
        } else {
            int leftPos = up.getNumber() + up.getLetter() <= field.getHeight() ? 0 :
                    up.getLetter() + up.getNumber() - field.getHeight();
            int rightPos = up.getNumber() + up.getLetter() >= field.getHeight() ? field.getWidth() - 1 :
                    up.getLetter() + up.getNumber();
            for (int i = rightPos; i >= leftPos; i--) {
                try {
                    way.add(field.getCell(i, down.getNumber() + down.getLetter() - i));
                } catch (CellNotExistException e) {
                    e.printStackTrace();
                }
            }
        }

        return way;
    }

    /**
     * Check if cell exist on field.
     * @param field
     *      Field where cell is checked.
     * @param letter
     *      Letter of checked cell.
     * @param number
     *      Number of checked cell.
     * @return
     *      True if cell exist on field.
     */
    public boolean cellExist(Field field, int letter, int number) {
        return number >= 0 && number < field.getHeight() && letter >= 0 && letter < field.getWidth();
    }

    /**
     * Find direction from start cell to end cell.
     * @param start
     *      First cell.
     * @param end
     *      Second cell.
     * @return
     *      Twodimensional direction from start to end. For example: UP_RIGHT, DOWN_LEFT.
     * @throws CellsAreEqualsException
     *      Thrown if cells have the same letters and numbers.
     */
    public TwoDimensionalDirection findDirectionFromStartToEnd(Field.Cell start, Field.Cell end)
            throws CellsAreEqualsException {
        if (start.getLetter() > end.getLetter() && start.getNumber() > end.getNumber()) {
            return TwoDimensionalDirection.DOWN_LEFT;
        } else if (start.getLetter() < end.getLetter() && start.getNumber() > end.getNumber()) {
            return TwoDimensionalDirection.DOWN_RIGHT;
        } else if(start.getLetter() > end.getLetter() && start.getNumber() < end.getNumber()) {
            return TwoDimensionalDirection.UP_LEFT;
        } else if(start.getLetter() < end.getLetter() && start.getNumber() < end.getNumber()) {
            return TwoDimensionalDirection.UP_RIGHT;
        }
        throw new CellsAreEqualsException("Cell [" + start.getLetter() + ", " + start.getNumber() +
                "] and cell [" + end.getLetter() + ", " + end.getNumber() + "] are equal");
    }
}
