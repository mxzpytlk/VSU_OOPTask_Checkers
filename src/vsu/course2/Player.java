package vsu.course2;

import java.util.ArrayList;

public class Player {
    private static final Field field = new Field(8, 8);
    private final int playerID;
    private final ArrayList<Checker> playerChecks = new ArrayList<>();

    public Player() {
        playerID = (int) (Math.random() * Integer.MAX_VALUE);
        for (int i = 0; i < 12; i++) {
            playerChecks.add(new Checker(playerID));
        }
    }
}
