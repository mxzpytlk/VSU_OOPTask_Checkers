package vsu.course2;

public class ConsoleUserInterface {
    private final Game game;

    ConsoleUserInterface() {
        game = new Game();
    }

    public void startGame() {
        while (!game.gameOver()) {
        }
    }

    public void drawField() {
        char[][] desk = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                desk[i][j] = (i + j) % 2 == 0 ? '\u009f' : ' ';
            }
        }

        Field field = game.getField();
        for (Field.Cell cell : field.getCells()) {
            if (cell.hasCheck()) {
                desk[cell.getNumber()][cell.getLetter()] = '\u0BE6';
            }
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(desk[i][j]);
            }
            System.out.println();
        }

        for (int i = 0; i < 30; i++) {
            System.out.print("-");
        }
    }
}
