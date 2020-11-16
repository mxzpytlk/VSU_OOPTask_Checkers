package vsu.course2.services;

import vsu.course2.game.*;
import vsu.course2.game.exceptions.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ArtificialIntelligenceService {
    private final GameService gs = new GameService();
    private final FieldService fs = new FieldService();

    ArtificialIntelligenceService() {}

    /**
     * Move first players check which program founds and which can be moved.
     * @param game
     *      Current game.
     * @param playerID
     *      ID of player, who's step is current.
     */
    public void makeStep(Game game, int playerID) {
        Field field = game.getField();

        for (Field.Cell cell: field) {
            if (cell.hasCheck() && cell.getCheck().getPlayerID() == playerID) {
                if (makeStepBySimpleCheck(game, cell)) {
                    break;
                }
                if (makeAttackBySimpleCheck(game, cell)) {

                }
            }
        }
    }

    private boolean makeAttackBySimpleCheck(Game game, Field.Cell cell) {
        Field.Cell playerStartPoint =  game.getCurrentPlayer().getStartPoint();
        Direction direction = playerStartPoint.equals(new Field.Cell(0, 0)) ?
                Direction.UP : Direction.DOWN;

        ArrayList<Field.Cell> way = new ArrayList<>();

        for (Field.Cell neighbour : game.getField().neighbours(cell)) {

        }
    }

    private void attackBySimpleCheck(Game game, Field.Cell cell) {
        Field.Cell playerStartPoint =  game.getCurrentPlayer().getStartPoint();
        Direction direction = playerStartPoint.equals(new Field.Cell(0, 0)) ?
                Direction.UP : Direction.DOWN;

        try {
            if (Math.abs(playerStartPoint.getLetter() - cell.getLetter()) > 1 &&
                    gs.checkOnNextLeftCellExist(game, cell) &&
                    gs.checkOnNextLeftCellExist(game,
                            game.getField().getCell(cell.getLetter() - direction.getCoef(),
                                    cell.getNumber() + direction.getCoef()))) {
                gs.attackCheckers(game,
                        Arrays.asList( cell, new Field.Cell(cell.getLetter() - 2 * direction.getCoef(),
                        cell.getNumber() + 2 * direction.getCoef()) ) );
            } else {
                gs.attackCheckers(game,
                        Arrays.asList( cell, new Field.Cell(cell.getLetter() + 2 * direction.getCoef(),
                                cell.getNumber() + 2 * direction.getCoef()) ) );
            }
        } catch (GameProcessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Move forward players simple check if it exists and can be moved.
     * @param game
     *      Current game.
     * @param cell
     *      Current cell from which check moves.
     * @return
     *      True if check has been moved.
     */
    private boolean makeStepBySimpleCheck(Game game, Field.Cell cell) {
        Field field = game.getField();
        Field.Cell playerStartPoint =  game.getCurrentPlayer().getStartPoint();
        Direction direction = playerStartPoint.equals(new Field.Cell(0, 0)) ?
                Direction.UP : Direction.DOWN;

        if (cell.getNumber() - playerStartPoint.getNumber() != 0) {

            try {
                if (cell.getLetter() != 0
                        && !field.getCell(cell.getLetter() - 1,
                        cell.getNumber() + direction.getCoef()).hasCheck()) {

                    gs.doStep(game, cell.getLetter(), cell.getNumber(),
                            cell.getLetter() - 1, cell.getNumber() + direction.getCoef());
                    return true;
                } else if(cell.getLetter() != 7
                        && !field.getCell(cell.getLetter() + 1, cell.getNumber() + direction.getCoef()).hasCheck()) {

                    gs.doStep(game, cell.getLetter(), cell.getNumber(),
                            cell.getLetter() + 1, cell.getNumber() + direction.getCoef());
                    return true;
                }
            } catch (MovementWhileAttackCanBeCarriedOutException e) {
                return false;
            } catch (CellNotExistException | PlayerNotHaveCheckException | SimpleCheckGoBackException
                    | CellNotHaveChecksException | CellIsNotFreeException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
