package vsu.course2;

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
}
