package vsu.course2.models.game.field;

import vsu.course2.graph.*;
import vsu.course2.models.game.Checker;
import vsu.course2.models.game.exceptions.CellNotExistException;

import java.util.Iterator;

public class Field implements Iterable<Cell> {

    private final Graph<Cell> field = new Graph<>();
    private final int width, height;

    public Field(int w, int h) {
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Cell getCell(int letter, int number) throws CellNotExistException {
        return getCell(new Cell(letter, number));
    }

    public Cell getCell(Cell cell) throws CellNotExistException {
        try {
            return field.getVertex(cell);
        } catch (GraphException e) {
            throw new CellNotExistException("Cell with letter " + cell.getLetter() + " and number " + cell.getNumber() +
                    " doesn't exist.");
        }
    }

    public Checker getChecker(int letter, int number) throws CellNotExistException {
        if (getCell(letter, number).getCheck() == null)
            throw new CellNotExistException("There isn't checker on this cell");
        return getCell(letter, number).getCheck();
    }

    public void setChecker(Checker checker, int letter, int number) throws Exception {
        getCell(letter, number).setCheck(checker);
    }

    public Iterable<Cell> neighbours(Cell cell) {
        return field.edjacencies(cell);
    }

    @Override
    public Iterator<Cell> iterator() {
        return field.bfs().iterator();
    }
}
