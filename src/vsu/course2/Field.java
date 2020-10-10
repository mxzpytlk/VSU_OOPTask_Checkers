package vsu.course2;

import exceptions.GameProcessException;
import exceptions.GraphException;
import graph.Graph;

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
    }

    private Graph<Cell> field = new Graph<>();

    Field(int w, int h) {
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
        try {
            return field.getVertex(new Cell(letter, number));
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
}
