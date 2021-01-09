package vsu.course2.services.console;

public enum ConsoleCommand {
    SAVE("save", new GameSaver()),
    LOAD("load", new GameLoader()),
    STEPS("steps", new StepsShower()),
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

    public IConsoleGameConsumer getConsumer() {
        return consumer;
    }
}
