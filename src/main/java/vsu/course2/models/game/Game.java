package vsu.course2.models.game;

import vsu.course2.models.game.field.Field;
import vsu.course2.services.FieldService;

public class Game {
    private final Player[] players = new Player[2];
    private final Field field;
    private final FieldService fs = new FieldService();
    private int turnOrder = 0;

    public Game() {
        this.field = fs.createField(8, 8);
        try {
            players[0] = new Player(fs.getCell(0, 0, field));
            players[1] = new Player(fs.getCell(7, 7, field));

            int letter = 0;
            int number = 0;

            for (int i = 0; i < 12; i++) {
                fs.setChecker(players[0].getCheckers()[i], letter, number, field);
                fs.setChecker(players[1].getCheckers()[i], 7 - letter, 7 - number, field);
                if (letter != 6 && letter != 7) {
                    letter += 2;
                } else {
                    number++;
                    letter = number % 2 == 0 ? 0 : 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Field getField() {
        return field;
    }

    public Player getCurrentPlayer() {
        return players[turnOrder];
    }

    public Player getEnemyPlayer() {
        return players[(turnOrder + 1) % players.length];
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getTurnOrder() {
        return turnOrder;
    }

    /**
     * Change player who make step.
     */
    public void changeTurnOrder() {
        turnOrder = (turnOrder + 1) % players.length;
    }
}
