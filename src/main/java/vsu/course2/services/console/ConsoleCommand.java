package vsu.course2.services.console;

import vsu.course2.models.game.Game;

public enum ConsoleCommand {
    SAVE("save", new GameSaver()),
    LOAD("load", new GameLoader()),
    STEPS("steps", new StepsShower()),
    MOVE("move", new CheckMover()),
    ATTACK("attack", new GameAttacker()),
    CONTINUE("continue", new GameContinuer());


    String commandName;
    IConsoleGameConsumer consumer;

    ConsoleCommand(String commandName, IConsoleGameConsumer consumer) {
        this.commandName = commandName;
        this.consumer = consumer;
    }
    
    public static ConsoleCommand getCommandByName(String commandName) {
        for (ConsoleCommand command : values()) {
            if (command.commandName.equals(commandName)) {
                return command;
            }
        }
        return CONTINUE;
    }

    public Game consume(Game game, String[] args) {
        return consumer.consume(game, args);
    }
}
