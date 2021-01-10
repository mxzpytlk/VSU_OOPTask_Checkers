package vsu.course2.services.console;

import vsu.course2.models.game.Game;
import vsu.course2.models.game.Player;
import vsu.course2.models.game.exceptions.CellNotExistException;
import vsu.course2.models.game.field.Cell;
import vsu.course2.models.game.field.Field;
import vsu.course2.services.ArtificialIntelligenceService;
import vsu.course2.services.FieldService;
import vsu.course2.services.GameService;

import java.util.ArrayList;
import java.util.List;

public interface IConsoleGameConsumer {
    FieldService fs = new FieldService();
    GameService gs = new GameService();
    ArtificialIntelligenceService ais = new ArtificialIntelligenceService();

    Game consume(Game game, String[] args);

    default void drawField(Game game) {
        drawField(game, new ArrayList<>());
    }

    default void drawField(Game game, List<Cell> markedCells) {
        char[][] desk = new char[8][8];
        Field field = game.getField();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                try {
                    desk[i][j] = (i + j) % 2 == 0 ?
                            (markedCells.contains(fs.getCell(j, i, field)) ? 'x' : ' ')
                            : '\u2588';
                } catch (CellNotExistException e) {
                    e.printStackTrace();
                }
            }
        }

        Player firstPlayer = game.getPlayers()[0];
        for (Cell cell : field) {
            if (cell.hasCheck()) {
                if (firstPlayer.hasCheck(cell.getCheck())) {
                    desk[cell.getNumber()][cell.getLetter()] = !cell.getCheck().isKing() ? '\u229B' : '\u25CF';
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
