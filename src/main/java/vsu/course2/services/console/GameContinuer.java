package vsu.course2.services.console;

import vsu.course2.models.game.Game;

public class GameContinuer implements IConsoleGameConsumer{
    @Override
    public Game consume(Game game, String[] args) {
        ais.makeStep(game);
        drawField(game);
        return game;
    }
}
