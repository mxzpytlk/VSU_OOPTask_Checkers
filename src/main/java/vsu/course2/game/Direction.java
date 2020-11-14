package vsu.course2.game;

/**
 * Direction of way where player should go.
 */
public enum Direction {
    UP(1),
    DOWN(-1);

    private final int coef;
    Direction(int coef) {
        this.coef = coef;
    }

    public int getCoef() {
        return coef;
    }
}