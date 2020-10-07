package vsu.course2;

import java.util.ArrayList;

public class Player {
    private final int playerID;
    private final ArrayList<Checker> playerChecks = new ArrayList<>();
    private Field.Cell startPoint;

    public Player(Field.Cell startPoint) {
        playerID = (int) (Math.random() * Integer.MAX_VALUE);
        for (int i = 0; i < 12; i++) {
            playerChecks.add(new Checker(playerID));
        }

        this.startPoint = startPoint;
    }

    public ArrayList<Checker> getCheckers() {
        return playerChecks;
    }

    public int id() {
        return playerID;
    }

    public Field.Cell getStartPoint() {
        return startPoint;
    }
}
