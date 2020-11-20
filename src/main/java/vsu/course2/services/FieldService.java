package vsu.course2.services;

import vsu.course2.game.*;
import vsu.course2.game.exceptions.*;

import java.util.ArrayList;

public class FieldService {
    FieldService() {}

    /**
     * Move check from start cell to and cell.
     * @param field Field where checks are situated.
     * @param start Cell where check is.
     * @param end Cell where check must be.
     * @throws CellNotHaveChecksException Thrown if start cell doesn't have check.
     * @throws CellIsNotFreeException Thrown if end cell isn't free.
     * @throws CellNotExistException Thrown if start or end cell doesn't exist.
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
     * @param field Field where cells are situated.
     * @param start First cell.
     * @param end Second cell.
     * @return List with cells which are situated on direct line between start and end cells.
     * @throws CellsAreNotOnDirectLineException Thrown if cells are not on direct line.
     */
    public ArrayList<Field.Cell> getWayBetweenCells(Field field, Field.Cell start, Field.Cell end) throws
            CellsAreNotOnDirectLineException, CellsAreEqualsException {
        ArrayList<Field.Cell> way = new ArrayList<>();

        if (!areOnDirectLine(start, end))
            throw new CellsAreNotOnDirectLineException("Check can move only on direct line");

        TwoDimensionalDirection direction = getDirectionFromStartToEnd(start, end);
        for (int i = start.getLetter() + direction.getVerticalCoef(); i < end.getLetter() ;
             i += direction.getVerticalCoef()) {
            try {
                way.add(field
                        .getCell(i, start.getNumber() +
                                direction.getHorizontalCoef() * (Math.abs(start.getLetter() - i))));
            } catch (CellNotExistException e) {
                e.printStackTrace();
            }
        }

        return way;
    }

    /**
     * Check if forward way between cells exists.
     * @param start First cell.
     * @param end Second cell.
     * @return True, if forward way between cells exists.
     */
    public boolean areOnDirectLine(Field.Cell start, Field.Cell end) {
        return Math.abs(start.getLetter() - end.getLetter()) ==
                Math.abs(start.getNumber() - end.getNumber())
                && Math.abs(start.getLetter() - end.getLetter()) != 0;
    }

    /**
     * Find one cell between two cells.
     * @param field Game field.
     * @param first First cell.
     * @param second Second cell.
     * @return Cell which is between first and second cells.
     * @throws GameProcessException Throws if cells situated far from each other.
     */
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

    /**
     * Find all checkers on line.
     * @param line Direct way on field.
     * @return List which contains checkers which are on line.
     */
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
     * Check if cell exist on field.
     * @param field Field where cell is checked.
     * @param letter Letter of checked cell.
     * @param number Number of checked cell.
     * @return True if cell exist on field.
     */
    public boolean cellExist(Field field, int letter, int number) {
        return number >= 0 && number < field.getHeight() && letter >= 0 && letter < field.getWidth();
    }

    /**
     * Find direction from start cell to end cell.
     * @param start First cell.
     * @param end Second cell.
     * @return Twodimensional direction from start to end. For example: UP_RIGHT, DOWN_LEFT.
     * @throws CellsAreEqualsException Thrown if cells have the same letters and numbers.
     */
    public TwoDimensionalDirection getDirectionFromStartToEnd(Field.Cell start, Field.Cell end)
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
