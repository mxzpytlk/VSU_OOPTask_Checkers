package vsu.course2.models.game;

import java.util.Objects;

public class Checker {

    private final int playerID;
    private boolean isKing = false;

    public void becomeKing() {
        isKing = true;
    }

    public boolean isKing() {
        return isKing;
    }

    public Checker(int playerID) {
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Checker checker = (Checker) o;
        return playerID == checker.playerID && isKing == checker.isKing;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerID, isKing);
    }
}
