package vsu.course2.services.console;

import vsu.course2.models.game.Game;
import vsu.course2.services.json.JsonService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameLoader implements IConsoleGameConsumer{
    @Override
    public Game consume(Game game, String[] args) {
        String fileName = "src/main/resources/" + (args.length == 0 ? "game.json" : args[0]);
        String JSONGame;
        try {
            JSONGame = Files.lines(Paths.get(fileName), StandardCharsets.UTF_8)
                    .reduce("", (prev, cur) -> prev + "" + cur);
        } catch (IOException e) {
            System.out.println("Such file doesn't exist");
            return game;
        }
        Game newGame = new JsonService<Game>().deserialize(JSONGame, Game.class);
        drawField(newGame);
        return newGame;
    }
}
