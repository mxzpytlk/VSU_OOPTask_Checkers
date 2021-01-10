package vsu.course2.services.console;

import vsu.course2.models.game.Game;
import vsu.course2.models.game.exceptions.*;

public class CheckMover implements IConsoleGameConsumer {
    @Override
    public Game consume(Game game, String[] args) {
        try {
            gs.doStep(game, Integer.parseInt(args[0]), Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            drawField(game);
        } catch (GameProcessException e) {
            System.out.println(e.getMessage());
        }
        return game;
    }
}
