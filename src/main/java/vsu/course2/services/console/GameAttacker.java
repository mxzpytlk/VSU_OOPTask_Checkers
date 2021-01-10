package vsu.course2.services.console;

import vsu.course2.models.game.Game;
import vsu.course2.models.game.exceptions.GameProcessException;
import vsu.course2.models.game.field.Cell;

import java.util.LinkedList;
import java.util.List;

public class GameAttacker implements IConsoleGameConsumer {
    @Override
    public Game consume(Game game, String[] args) {
        try {
            List<Cell> way = new LinkedList<>();
            way.add(fs.getCell(Integer.parseInt(args[0]), Integer.parseInt(args[1]), game.getField()));
            for (int i = 2; i < args.length; i += 2) {
                way.add(fs.getCell(Integer.parseInt(args[i]), Integer.parseInt(args[i + 1]), game.getField()));
            }

            gs.attackCheckers(game, way);
            drawField(game);
        } catch (GameProcessException e) {
            System.out.println(e.getMessage());
        }
        return game;
    }
}
