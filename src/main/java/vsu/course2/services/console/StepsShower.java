package vsu.course2.services.console;

import vsu.course2.models.game.Game;
import vsu.course2.models.game.exceptions.CellNotExistException;
import vsu.course2.models.game.field.Cell;

import java.util.LinkedList;
import java.util.List;

public class StepsShower implements IConsoleGameConsumer{
    @Override
    public Game consume(Game game, String[] args) {
        try {
            int letter = Integer.parseInt(args[0]);
            int number = Integer.parseInt(args[1]);
            List<List<Cell>> possibleWays = gs.getPossibleWays(game).get(fs.getCell(letter, number, game.getField()));
            if (possibleWays == null) {
                return game;
            }
            List<Cell> markedCells = new LinkedList<>();
            for (List<Cell> way : possibleWays) {
                markedCells.addAll(way);
            }
            drawField(game, markedCells);
        } catch (CellNotExistException e) {
            System.out.println(e.getMessage());
        }
        return game;
    }
}
