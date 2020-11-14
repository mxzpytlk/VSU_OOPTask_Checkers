package vsu.course2.console;

import vsu.course2.game.*;

public class SimpleArtificialIntelligence {
    private final Game game;
    private final int playerID;

    public SimpleArtificialIntelligence(Game game, int playerID) {
        this.game = game;
        this.playerID = playerID;
    }

    public Game getGame() {
        return game;
    }

    public int getPlayerID() {
        return playerID;
    }
}
