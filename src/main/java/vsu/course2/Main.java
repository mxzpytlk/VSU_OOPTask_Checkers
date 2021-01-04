package vsu.course2;

import vsu.course2.models.game.Game;
import vsu.course2.services.ConsoleInterfaceService;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException { ;
        new ConsoleInterfaceService().startGame(new Game());

    }
}
