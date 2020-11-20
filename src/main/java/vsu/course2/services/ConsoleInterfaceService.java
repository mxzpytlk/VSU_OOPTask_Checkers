package vsu.course2.services;

import vsu.course2.game.*;

import java.util.Scanner;

/**
 * Service which present checkers game in console.
 */
public class ConsoleInterfaceService {
    private final GameService gs = new GameService();
    private final ArtificialIntelligenceService ais = new ArtificialIntelligenceService();

    public ConsoleInterfaceService() { }

    /**
     * Show game process from start to end by step in console.
     * @param game Current game
     */
    public void startGame(Game game) {
        Scanner scn = new Scanner(System.in);
        drawField(game);
        while (!gs.gameOver(game)) {
            scn.nextLine();
            ais.makeStep(game);
            drawField(game);
        }
    }

    /**
     * Draw game field in current state.
     * @param game Current game.
     */
    private void drawField(Game game) {
        char[][] desk = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                desk[i][j] = (i + j) % 2 == 0 ? '\u009f' : ' ';
            }
        }

        Field field = game.getField();
        int firstPlayerId = game.getPlayers()[0].getPlayerID();
        for (Field.Cell cell : field) {
            if (cell.hasCheck()) {
                if (cell.getCheck().getPlayerID() == firstPlayerId) {
                    desk[cell.getNumber()][cell.getLetter()] = !cell.getCheck().isKing() ? '\u229B' : '\u2741';
                } else {
                    desk[cell.getNumber()][cell.getLetter()] = !cell.getCheck().isKing() ? '\u0BE6' : '\u06DE';
                }
            }
        }

        for (int i = game.getField().getHeight() - 1; i >= 0; i--) {
            for (int j = 0; j < game.getField().getWidth(); j++) {
                System.out.print(desk[i][j]);
            }
            System.out.println();
        }

        for (int i = 0; i < 30; i++) {
            System.out.print("-");
        }
        System.out.println();
    }
}
