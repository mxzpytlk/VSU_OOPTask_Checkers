package vsu.course2;

public class Game {
    private Player player1, player2;
    private Field field = new Field(8, 8);

    public Game() {
        try {
            player1 = new Player(field.getCell(0, 0));
            player2 = new Player(field.getCell(7, 7));
        } catch (Exception ignored) {}

        int letter = 0;
        int number = 0;

        for (int i = 0; i < 12; i++) {

        }
    }
}
