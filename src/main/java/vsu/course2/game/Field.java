package vsu.course2.game;

import vsu.course2.graph.GraphException;
import vsu.course2.graph.Graph;

import java.util.Iterator;
import java.util.Objects;

public class Field implements Iterable<Field.Cell> {

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

    /**
     *Return black cell from field.
     * @param letter
     *      Position of column.
     * @param number
     *      Position of row.
     * @return
     *      Cell object.
     * @throws GameProcessException
     *      Thrown if this black cell doesn't exist on field;
     */
    public Cell getCell(int letter, int number) throws GameProcessException {
        return getCell(new Cell(letter, number));
    }

    public Cell getCell(Cell cell) throws GameProcessException {
        try {
            return field.getVertex(cell);
        } catch (GraphException e) {
            throw new GameProcessException("Cell with letter " + cell.letter + " and number " + cell.number +
                    " doesn't exist.");
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

    public Iterable<Field.Cell> neighbours(Field.Cell cell) {
        return field.edjacencies(cell);
    }

    @Override
    public Iterator<Cell> iterator() {
        return field.bfs().iterator();
    }
}
