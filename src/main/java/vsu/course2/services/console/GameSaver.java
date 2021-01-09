package vsu.course2.services.console;

import vsu.course2.models.game.Game;
import vsu.course2.services.json.JsonService;

import java.io.FileWriter;
import java.io.IOException;

public class GameSaver implements IConsoleGameConsumer {
    @Override
    public Game consume(Game game, String[] args) {
        String fileName = "src/main/resources/" + (args.length == 0 ? "game.json" : args[0]);

        String JSONGame = new JsonService<Game>().serialize(game);
        try {
            FileWriter fw = new FileWriter(fileName);
            fw.write(JSONGame);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return game;
    }
}
