package vsu.course2.models.game.field;

import vsu.course2.graph.Graph;

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

    public Graph<Cell> getField() {
        return field;
    }

    @Override
    public Iterator<Cell> iterator() {
        return field.bfs().iterator();
    }
}
