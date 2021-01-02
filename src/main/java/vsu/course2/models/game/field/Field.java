package vsu.course2.models.game.field;

import vsu.course2.graph.Graph;

import java.util.Iterator;

public class Field implements Iterable<Cell> {

    private final Graph<Cell> field;
    private final int width, height;

    public Field(Graph<Cell> field, int width, int height) {
        this.field = field;
        this.width = width;
        this.height = height;
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
