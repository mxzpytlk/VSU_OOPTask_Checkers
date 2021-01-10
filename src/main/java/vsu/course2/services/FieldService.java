package vsu.course2.services;

import vsu.course2.graph.Graph;
import vsu.course2.graph.GraphException;
import vsu.course2.models.game.Checker;
import vsu.course2.models.game.field.Cell;
import vsu.course2.models.game.field.Field;
import vsu.course2.models.game.TwoDimensionalDirection;
import vsu.course2.models.game.exceptions.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class FieldService {

    public Field createField(int w, int h) {
        Graph<Cell> field = new Graph<>();
        for (int i = 0; i < h - 1; i++) {
            for (int j = i % 2 == 0 ? 0 : 1; j < w; j += 2) {
                try {
                    Cell first = field.hasVertex(new Cell(j, i)) ? field.getVertex(new Cell(j, i)) : new Cell(j, i);
                    if (j == 0) {
                        Cell second = field.hasVertex(new Cell(j + 1, i + 1)) ?
                                field.getVertex(new Cell(j + 1, i + 1))
                                    : new Cell(j + 1, i + 1);
                        field.addEdge(first, second);
                    } else if (j == w - 1) {
                        Cell second = field.hasVertex(new Cell(j - 1, i + 1)) ?
                                field.getVertex(new Cell(j - 1, i + 1))
                                : new Cell(j +-1, i + 1);
                        field.addEdge(first, second);
                    } else {
                        Cell second = field.hasVertex(new Cell(j + 1, i + 1)) ?
                                field.getVertex(new Cell(j + 1, i + 1))
                                : new Cell(j + 1, i + 1);

                        Cell third = field.hasVertex(new Cell(j - 1, i + 1)) ?
                                field.getVertex(new Cell(j - 1, i + 1))
                                : new Cell(j +-1, i + 1);
                        field.addEdge(first, second);
                        field.addEdge(first, third);
                    }
                } catch (GraphException e) {
                    e.printStackTrace();
                }
            }
        }
        return new Field(field, w, h);
    }

    public Cell getCell(int letter, int number, Field field) throws CellNotExistException {
        return getCell(new Cell(letter, number), field);
    }

    public Cell getCell(Cell cell, Field field) throws CellNotExistException {
        try {
            return field.getField().getVertex(cell);
        } catch (GraphException e) {
            throw new CellNotExistException("Cell with letter " + cell.getLetter() + " and number " + cell.getNumber() +
                    " doesn't exist.");
        }
    }

    public Checker getChecker(int letter, int number, Field field) throws CellNotExistException {
        if (getCell(letter, number, field).getCheck() == null)
            throw new CellNotExistException("There isn't checker on this cell");
        return getCell(letter, number, field).getCheck();
    }

    public void setChecker(Checker checker, int letter, int number, Field field) throws Exception {
        getCell(letter, number, field).setCheck(checker);
    }

    public Iterable<Cell> neighbours(Cell cell, Field field) {
        return field.getField().edjacencies(cell);
    }

    /**
     * Move check from start cell to and cell.
     * @param field Field where checks are situated.
     * @param start Cell where check is.
     * @param end Cell where check must be.
     * @throws CellNotHaveChecksException Thrown if start cell doesn't have check.
     * @throws CellIsNotFreeException Thrown if end cell isn't free.
     * @throws CellNotExistException Thrown if start or end cell doesn't exist.
     */
    public void moveChecker(Field field, Cell start, Cell end)
            throws CellNotHaveChecksException, CellIsNotFreeException, CellNotExistException {
        moveChecker(field, start.getLetter(), start.getNumber(), end.getLetter(), end.getNumber());
    }

    public void moveChecker(Field field, int prevLetter, int prevNumber, int newLetter, int newNumber)
            throws CellNotHaveChecksException, CellIsNotFreeException, CellNotExistException {

        if (getCell(prevLetter, prevNumber, field).getCheck() == null) {
            throw new CellNotHaveChecksException("Cell" + getCell(prevLetter, prevNumber, field).toString()
                    + " doesn't have checker");
        }

        if (getCell(newLetter, newNumber, field).hasCheck()) {
            throw new CellIsNotFreeException("This cell isn't free");
        }

        getCell(newLetter, newNumber, field).setCheck(getCell(prevLetter, prevNumber, field).getCheck());
        getCell(prevLetter, prevNumber, field).removeCheck();
    }

    /**
     * Find direct line on field between two cells.
     * @param field Field where cells are situated.
     * @param start First cell.
     * @param end Second cell.
     * @return List with cells which are situated on direct line between start and end cells.
     * @throws CellsAreNotOnDirectLineException Thrown if cells are not on direct line.
     */
    public ArrayList<Cell> getWayBetweenCells(Field field, Cell start, Cell end) throws
            CellsAreNotOnDirectLineException, CellsAreEqualsException {
        ArrayList<Cell> way = new ArrayList<>();

        if (!areOnDirectLine(start, end))
            throw new CellsAreNotOnDirectLineException("Check can move only on direct line");

        TwoDimensionalDirection direction = getDirectionFromStartToEnd(start, end);
        for (int i = 1; i < abs(start.getNumber() - end.getNumber()) ;
             i++) {
            try {
                way.add(getCell(start.getLetter() + i * direction.getHorizontalCoef(),
                        start.getNumber() + i * direction.getVerticalCoef(), field));
            } catch (CellNotExistException e) {
                e.printStackTrace();
            }
        }

        return way;
    }

    public boolean isOnBoard(Cell cell, Field field) {
        return cell.getLetter() == 0 || cell.getLetter() == field.getWidth() - 1
                || cell.getNumber() == 0 || cell.getNumber() == field.getHeight() - 1;
    }

    /**
     * Check if forward way between cells exists.
     * @param start First cell.
     * @param end Second cell.
     * @return True, if forward way between cells exists.
     */
    public boolean areOnDirectLine(Cell start, Cell end) {
        return abs(start.getLetter() - end.getLetter()) ==
                abs(start.getNumber() - end.getNumber())
                && abs(start.getLetter() - end.getLetter()) != 0;
    }

    /**
     * Find one cell between two cells.
     * @param field Game field.
     * @param first First cell.
     * @param second Second cell.
     * @return Cell which is between first and second cells.
     * @throws GameProcessException Throws if cells situated far from each other.
     */
    public Cell getCellBetweenTwoCells(Field field, Cell first, Cell second) throws GameProcessException {
        return getCell((first.getLetter() + second.getLetter()) / 2,
                (first.getNumber() + second.getNumber()) / 2, field);
    }

    /**
     * Find all checkers on line.
     * @param line Direct way on field.
     * @return List which contains checkers which are on line.
     */
    public ArrayList<Checker> checkersOnLine(ArrayList<Cell> line) {
        ArrayList<Checker> result = new ArrayList<>();
        for (Cell cell : line) {
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
    public TwoDimensionalDirection getDirectionFromStartToEnd(Cell start, Cell end)
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

    public List<Cell> getWayToBoard(Cell cell, TwoDimensionalDirection direction, Field field) {
        ArrayList<Cell> way = new ArrayList<>();

        for (int i = 1; cell.getLetter() + i * direction.getHorizontalCoef() >= 0
                && cell.getLetter() + i * direction.getHorizontalCoef() < field.getWidth()
                && cell.getNumber() + i * direction.getVerticalCoef() >= 0
                && cell.getNumber() + i * direction.getVerticalCoef() < field.getHeight(); i++) {
                try {
                    way.add(getCell(cell.getLetter() + i * direction.getHorizontalCoef(),
                            cell.getNumber() + i * direction.getVerticalCoef(), field));
                } catch (CellNotExistException e) {
                    e.printStackTrace();
                }
        }

        return way;
    }
}
