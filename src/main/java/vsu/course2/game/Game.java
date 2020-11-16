package vsu.course2.game;

public class Game {
    private final Player[] players = new Player[2];
    private final Field field = new Field(8, 8);
    private int turnOrder = 0;

    public Game() {
        try {
            players[0] = new Player(field.getCell(0, 0));
            players[1] = new Player(field.getCell(7, 7));

            int letter = 0;
            int number = 0;

            for (int i = 0; i < 12; i++) {
                field.setChecker(players[0].getCheckers()[i], letter, number);
                field.setChecker(players[1].getCheckers()[i], 7 - letter, 7 - number);
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
