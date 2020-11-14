package vsu.course2.services;

import vsu.course2.game.*;

import java.util.ArrayList;

public class FieldService {
    FieldService() {}

    public void moveChecker(Field field, Field.Cell start, Field.Cell end) throws GameProcessException {
        moveChecker(field, start.getLetter(), start.getNumber(), end.getLetter(), end.getNumber());
    }

    public void moveChecker(Field field, int prevLetter, int prevNumber, int newLetter, int newNumber)
            throws GameProcessException {

        if (field.getCell(prevLetter, prevNumber).getCheck() == null) {
            throw new GameProcessException("This cell doesn't have checker");
        }

        if (field.getCell(newLetter, newNumber).getCheck() != null) {
            throw new GameProcessException("This cell isn't free");
        }

        field.getCell(newLetter, newNumber).setCheck(field.getCell(prevLetter, prevNumber).getCheck());
        field.getCell(prevLetter, prevNumber).removeCheck();
    }

    public ArrayList<Field.Cell> getWayBetweenCells(Field field, Field.Cell start, Field.Cell end) throws GameProcessException {
        ArrayList<Field.Cell> way = new ArrayList<>();

        if (areOnDirectLine(start, end))
            throw new GameProcessException("Check can move only on direct line");

        int verticalDirection = getVerticalDirection(start, end);
        int horizontalDirection = getHorizontalDirection(start, end);
        for (int i = start.getLetter() + verticalDirection; i != end.getLetter() ; i += verticalDirection) {
            way.add(field
                    .getCell(i, start.getNumber() + horizontalDirection * (Math.abs(start.getLetter() - i))));
        }

        return way;
    }

    public boolean areOnDirectLine(Field.Cell start, Field.Cell end) {
        return Math.abs(start.getLetter() - end.getLetter()) !=
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
}
