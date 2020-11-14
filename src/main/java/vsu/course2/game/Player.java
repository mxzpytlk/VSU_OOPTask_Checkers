package vsu.course2.game;

import java.util.ArrayList;
import java.util.Arrays;

public class Player {
    private final int playerID;
    private final ArrayList<Checker> playerChecks = new ArrayList<>();
    private final Field.Cell startPoint;

    public Player(Field.Cell startPoint) {
        playerID = (int) (Math.random() * Integer.MAX_VALUE);
        for (int i = 0; i < 12; i++) {
            playerChecks.add(new Checker(playerID));
        }

        this.startPoint = startPoint;
    }

    public Checker[] getCheckers() {
        return playerChecks.toArray(new Checker[0]);
    }

    public int getPlayerID() {
        return playerID;
    }

    public int id() {
        return playerID;
    }

    public boolean hasCheck(Checker check) {
        return playerChecks.contains(check);
    }

    /**
     * Find cell in corner where player start game.
     * @return
     *      Corner, where player start game.
     */
    public Field.Cell getStartPoint() {
        return startPoint;
    }

    public void removeCheck(Checker[] checks) {
        playerChecks.removeAll(Arrays.asList(checks));
    }
}