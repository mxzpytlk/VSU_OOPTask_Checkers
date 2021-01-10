package vsu.course2.services;

import vsu.course2.models.game.Game;
import vsu.course2.services.console.IConsoleGameConsumer;
import vsu.course2.services.game.GameService;

import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

import static vsu.course2.services.console.ConsoleCommand.getCommandByName;


/**
 * Service which present checkers game in console.
 */
public class ConsoleInterfaceService {
    private final GameService gs = new GameService();

    /**
     * Show game process from start to end by step in console.
     * @param game Current game
     */
    public void startGame(Game game) {
        Scanner scn = new Scanner(System.in);
        ((IConsoleGameConsumer) (game1, args) -> null).drawField(game);
        while (!gs.gameOver(game)) {
            String[] inputArr = scn.nextLine().split("\\s+");
            String command = "";
            String[] args = new String[0];
            if (inputArr.length > 0) {
                command = inputArr[0].toLowerCase(Locale.ROOT);
                if (inputArr.length > 1) {
                    args = Arrays.copyOfRange(inputArr, 1, inputArr.length);
                }
            }

            game = getCommandByName(command).consume(game, args);
        }
    }
}
