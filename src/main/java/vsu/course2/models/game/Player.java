package vsu.course2.models.game;

import vsu.course2.models.game.field.Cell;

import java.util.ArrayList;
import java.util.Arrays;

public class Player {
    private final ArrayList<Checker> playerChecks = new ArrayList<>();
    private final Cell startPoint;

    public Player(Cell startPoint) {
        for (int i = 0; i < 12; i++) {
            playerChecks.add(new Checker());
        }

        this.startPoint = startPoint;
    }

    public Checker[] getCheckers() {
        return playerChecks.toArray(new Checker[0]);
    }

    public boolean hasCheck(Checker check) {
        return playerChecks.contains(check);
    }

    /**
     * Find cell, which is situated in down left corner relatively player.
     * @return Corner, which is situated in down left corner relatively player.
     */
    public Cell getStartPoint() {
        return startPoint;
    }

    public void removeCheck(Checker[] checks) {
        playerChecks.removeAll(Arrays.asList(checks));
    }
}
