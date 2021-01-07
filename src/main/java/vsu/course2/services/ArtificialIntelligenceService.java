package vsu.course2.services;

import vsu.course2.models.game.Game;
import vsu.course2.models.game.exceptions.CellsAreEqualsException;
import vsu.course2.models.game.exceptions.CellsAreNotOnDirectLineException;
import vsu.course2.models.game.exceptions.GameProcessException;
import vsu.course2.models.game.field.Cell;
import vsu.course2.models.game.field.Field;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

/**
 * Service which controls players behaviour.
 */
public class ArtificialIntelligenceService {
    private final GameService gs = new GameService();
    private final FieldService fs = new FieldService();

    /**
     * Move first players check which program founds and which can be moved.
     * @param game Current game.
     */
    public void makeStep(Game game) {
        var allWaysForCells = gs.getPossibleWays(game).values();
        List<List<Cell>> possibleWays = new LinkedList<>();
        for (List<List<Cell>> waysForOneCell: allWaysForCells) {
            possibleWays.addAll(waysForOneCell);
        }
        Random rand = new Random();
        try {
            List<Cell> way = possibleWays.get(rand.nextInt(possibleWays.size()));
            if (isStep(way, game.getField())) {
                gs.doStep(game, way.get(0).getLetter(), way.get(0).getNumber(),
                            way.get(1).getLetter(), way.get(1).getNumber());
            } else {
                gs.attackCheckers(game, way);
            }
        } catch (GameProcessException e) {
            e.printStackTrace();
        }
    }

    private boolean isStep(List<Cell> way, Field field) {
        try {
            for (Cell cell : fs.getWayBetweenCells(field, way.get(0), way.get(1))) {
                if (cell.hasCheck()) {
                    return false;
                }
            }
        } catch (CellsAreNotOnDirectLineException | CellsAreEqualsException e) {
            e.printStackTrace();
        }
        return true;
    }
}
