package vsu.course2;

import vsu.course2.models.game.Game;
import vsu.course2.services.ConsoleInterfaceService;

public class Main {

    public static void main(String[] args) {
	    new ConsoleInterfaceService().startGame(new Game());
    }
}
