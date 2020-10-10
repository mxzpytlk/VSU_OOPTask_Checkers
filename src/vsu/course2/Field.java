package vsu.course2;

import exceptions.GameProcessException;
import exceptions.GraphException;
import graph.Graph;

import java.util.ArrayList;
import java.util.Objects;

public class Field {

    public static class Cell {
        private final int letter;
        private final int number;
        private Checker curCheck;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return letter == cell.letter &&
                    number == cell.number;
        }

        @Override
        public int hashCode() {
            return Objects.hash(letter, number);
        }

        public Cell(int letter, int number) {
            this.letter = letter;
            this.number = number;
        }

        public int getLetter() {
            return letter;
        }

        public int getNumber() {
            return number;
        }

        public void setCheck(Checker curCheck) {
            this.curCheck = curCheck;
        }

        public void removeCheck() {
            curCheck = null;
        }

        public Checker getCheck() {
            return curCheck;
        }

        public boolean hasCheck() { return curCheck != null; }
    }

    private final Graph<Cell> field = new Graph<>();
    private final int width;
    private final int height;

    Field(int w, int h) {
        this.width = w;
        this.height = h;

        for (int i = 0; i < h - 1; i++) {
            for (int j = i % 2 == 0 ? 0 : 1; j < w; j += 2) {
                if (j == 0) {
                    field.addEdge(new Cell(j, i), new Cell(j + 1, i + 1));
                } else if (j == w - 1) {
                    field.addEdge(new Cell(j, i), new Cell(j - 1, i + 1));
                } else {
                    field.addEdge(new Cell(j, i), new Cell(j + 1, i + 1));
                    field.addEdge(new Cell(j, i), new Cell(j - 1, i + 1));
                }
            }
        }
    }

    public void moveChecker(int prevLetter, int prevNumber, int newLetter, int newNumber)
                                        throws GameProcessException {

        if (getCell(prevLetter, prevNumber).curCheck == null) {
            throw new GameProcessException("This cell doesn't have checker");
        }

        if (getCell(newLetter, newNumber).curCheck != null) {
            throw new GameProcessException("This cell isn't free");
        }

        getCell(newLetter, newNumber).setCheck(getCell(prevLetter, prevNumber).curCheck);
        getCell(prevLetter, prevNumber).removeCheck();
    }

    public Cell getCell(int letter, int number) throws GameProcessException {
        return getCell(new Cell(letter, number));
    }

    public Cell getCell(Cell cell) throws GameProcessException {
        try {
            return field.getVertex(cell);
        } catch (GraphException e) {
            throw new GameProcessException("Such cell doesn't exist");
        }
    }

    public Checker getChecker(int letter, int number) throws GameProcessException {
        if (getCell(letter, number).curCheck == null)
            throw new GameProcessException("There isn't checker on this cell");
        return getCell(letter, number).curCheck;
    }

    public void setChecker(Checker checker, int letter, int number) throws Exception {
        getCell(letter, number).setCheck(checker);
    }

    public Iterable<Cell> neighboringCells(Cell cell) {
        return field.edjacencies(cell);
    }

    public Cell skip(Cell start, Cell end) throws GameProcessException {
        if (Math.abs(Math.abs(start.getLetter() - end.getLetter()) -
                Math.abs(start.getNumber() - end.getNumber())) != 1)
            throw new GameProcessException("Check can skip only one cell");

        if (start.letter > end.letter) {
            if (start.number > end.number) {
                return getCell(start.letter - 2, start.number - 2);
            } else {
                return getCell(start.letter - 2, start.number + 2);
            }
        } else {
            if (start.number > end.number) {
                return getCell(start.letter + 2, start.number - 2);
            } else {
                return getCell(start.letter + 2, start.number + 2);
            }
        }
    }

    public Iterable<Cell> getDirection(Cell start, Cell end) throws GameProcessException {
        ArrayList<Cell> direction = new ArrayList<>();

        if (Math.abs(start.getLetter() - end.getLetter()) !=
                Math.abs(start.getNumber() - end.getNumber()))
            throw new GameProcessException("Check can move only on direct line");

        int startLetter = start.letter;
        int endLetter = end.letter;
        int endNumber = end.number;

        if (startLetter > endLetter) {
            while (endLetter != 0 || endNumber != 0) {
                endLetter--;
                endNumber--;
            }

            for (int i = startLetter - 1; i >= endLetter; i--) {
                direction.add(getCell(i, i));
            }
        } else {
            while (endLetter != width || endNumber != height) {
                endLetter++;
                endNumber++;
            }

            for (int i = startLetter + 1; i <= endLetter; i++) {
                direction.add(getCell(i, i));
            }
        }

        return direction;
    }
}
