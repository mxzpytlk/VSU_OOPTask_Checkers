package vsu.course2;

import exceptions.GameProcessException;

public class Game {
    private Player[] players = new Player[2];
    private Field field = new Field(8, 8);
    private int turnOrder = 0;

    public Game() {
        try {
            players[0] = new Player(field.getCell(0, 0));
            players[1] = new Player(field.getCell(7, 7));

            int letter = 0;
            int number = 0;

            for (int i = 0; i < 12; i++) {
                field.setChecker(players[0].getCheckers().get(i), letter, number);
                field.setChecker(players[1].getCheckers().get(i), 7 - letter, 7 - number);
                if (letter != 6 && letter != 7) {
                    letter += 2;
                } else {
                    number++;
                    letter = number % 2 == 0 ? 0 : 1;
                }
            }
        } catch (Exception ignored) {}
    }

    public void doStep(int prevLetter, int prevNumber, int nextLetter, int nextNumber) throws GameProcessException {
        if (gameOver()) return;
        if (!players[turnOrder].getCheckers().contains(field.getChecker(prevLetter, prevNumber)))
            throw new GameProcessException("Player doesn't have checkers on this position");

        //Потом обработать ситуацию, когда шашка бьет другую шашку и возвращается назад
        if (Math.abs(players[turnOrder].getStartPoint().getNumber() - prevNumber) <=
                Math.abs(players[turnOrder].getStartPoint().getNumber() - nextNumber) &&
                !field.getChecker(prevLetter, prevNumber).isKing()) {

            throw new GameProcessException("Checker can't go back");
        }
        field.moveChecker(prevLetter, prevNumber, nextLetter, nextNumber);

        turnOrder = (turnOrder + 1) % players.length;
    }

    public boolean gameOver() {
        return players[0].getCheckers().isEmpty() || players[1].getCheckers().isEmpty();
    }
}
