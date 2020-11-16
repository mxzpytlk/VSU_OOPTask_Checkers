package vsu.course2.services;

import vsu.course2.console.*;
import vsu.course2.game.*;

import java.util.Scanner;

public class ConsoleInterfaceService {
    private final GameService gs = new GameService();
    private final ArtificialIntelligenceService ais = new ArtificialIntelligenceService();

    public ConsoleInterfaceService() { }
    public void startGame(ConsoleUserInterface cui) {
        Game game = cui.getGame();
        SimpleArtificialIntelligence[] players = cui.getPlayers();

        players[0] = new SimpleArtificialIntelligence(game, game.getCurrentPlayer().getPlayerID());
        game.changeTurnOrder();
        players[1] = new SimpleArtificialIntelligence(game, game.getCurrentPlayer().getPlayerID());
        game.changeTurnOrder();

        Scanner scn = new Scanner(System.in);
        drawField(cui);
        while (!gs.gameOver(game)) {
            scn.nextLine();
            ais.makeStep(game, game.getCurrentPlayer().getPlayerID());
            cui.changeTurnOrder();
            drawField(cui);
        }
    }

    private void drawField(ConsoleUserInterface cui) {
        char[][] desk = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                desk[i][j] = (i + j) % 2 == 0 ? '\u009f' : ' ';
            }
        }

        Field field = cui.getGame().getField();
        int firstPlayerId = cui.getPlayers()[0].getPlayerID();
        for (Field.Cell cell : field) {
            if (cell.hasCheck()) {
                if (cell.getCheck().getPlayerID() == firstPlayerId) {
                    desk[cell.getNumber()][cell.getLetter()] = '\u229B';
                } else {
                    desk[cell.getNumber()][cell.getLetter()] = '\u0BE6';
                }
            }
        }

        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
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
