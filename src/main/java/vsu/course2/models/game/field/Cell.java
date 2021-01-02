package vsu.course2.models.game.field;

import vsu.course2.models.game.Checker;

import java.util.Objects;

public class Cell {
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

    @Override
    public String toString() {
        return super.toString();
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

    public boolean hasCheck() {
        return curCheck != null;
    }
}
