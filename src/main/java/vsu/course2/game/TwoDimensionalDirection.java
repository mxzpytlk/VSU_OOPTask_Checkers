package vsu.course2.game;

public enum TwoDimensionalDirection {
    UP(1, 0),
    UP_RIGHT(1,1),
    DOWN_RIGHT(-1,1),
    DOWN(-1, 0),
    DOWN_LEFT(-1,-1),
    UP_LEFT(1,-1);


    private final int verticalCoef, horizontalCoef;

    TwoDimensionalDirection(int verticalCoef, int horizontalCoef) {
        this.verticalCoef = verticalCoef;
        this.horizontalCoef = horizontalCoef;
    }

    public int getVerticalCoef() {
        return verticalCoef;
    }

    public int getHorizontalCoef() {
        return horizontalCoef;
    }
}
