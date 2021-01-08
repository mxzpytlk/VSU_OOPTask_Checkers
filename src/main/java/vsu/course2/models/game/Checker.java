package vsu.course2.models.game;

import java.util.Objects;

public class Checker {

    private final int ID;
    private boolean isKing = false;

    public void becomeKing() {
        isKing = true;
    }

    public boolean isKing() {
        return isKing;
    }

    public Checker() {
        this.ID = (int) (Math.random() * Integer.MAX_VALUE);
    }

    public Checker(int ID) {
        this.ID = ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Checker checker = (Checker) o;
        return ID == checker.ID && isKing == checker.isKing;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, isKing);
    }
}
