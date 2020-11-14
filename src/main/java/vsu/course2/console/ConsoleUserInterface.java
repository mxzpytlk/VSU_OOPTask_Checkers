package vsu.course2.console;

import vsu.course2.game.*;

public class ConsoleUserInterface {
    private final Game game;
    private int turnOrder = 0;
    private final SimpleArtificialIntelligence[] players = new SimpleArtificialIntelligence[2];

    public ConsoleUserInterface() {
        game = new Game();
    }

    public Game getGame() {
        return game;
    }

    public SimpleArtificialIntelligence[] getPlayers() {
        return players;
    }

    public void changeTurnOrder() {
        turnOrder = (turnOrder + 1) % players.length;
    }
}
