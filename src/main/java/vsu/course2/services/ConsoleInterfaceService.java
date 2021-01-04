package vsu.course2.services;

import com.google.gson.Gson;
import vsu.course2.models.game.field.Cell;
import vsu.course2.models.game.field.Field;
import vsu.course2.models.game.Game;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Service which present checkers game in console.
 */
public class ConsoleInterfaceService {
    private final GameService gs = new GameService();
    private final ArtificialIntelligenceService ais = new ArtificialIntelligenceService();


    /**
     * Show game process from start to end by step in console.
     * @param game Current game
     */
    public void startGame(Game game) throws IOException {
        Scanner scn = new Scanner(System.in);
        drawField(game);
        while (!gs.gameOver(game)) {
            String command = scn.nextLine();
            if (command.equals("save")) {
                saveGame(game);
            } else if (command.equals("load")) {
                game = loadGame();
                drawField(game);
            }
            else {
                ais.makeStep(game);
                drawField(game);
            }
        }
    }

    private Game loadGame() throws IOException {
        Gson gson = new Gson();
        String JSONGame = Files.lines(Paths.get("src/main/resources/game.json"), StandardCharsets.UTF_8)
                .reduce("", (prev, cur) -> prev + "" + cur);
        return gson.fromJson(JSONGame, Game.class);
    }

    private void saveGame(Game game) {
        Gson gson = new Gson();
        String JSONGame = gson.toJson(game);
        try {
            FileWriter fw = new FileWriter("src/main/resources/game.json");
            fw.write(JSONGame);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
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
        for (Cell cell : field) {
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
