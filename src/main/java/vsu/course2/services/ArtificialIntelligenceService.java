package vsu.course2.services;

import vsu.course2.models.game.*;
import vsu.course2.models.game.exceptions.*;

import java.util.ArrayList;

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
        Field field = game.getField();
        int playerID = game.getCurrentPlayer().getPlayerID();

        for (Field.Cell cell: field) {
            if (cell.hasCheck() && cell.getCheck().getPlayerID() == playerID) {
                if (makeStepBySimpleCheck(game, cell)) {
                    break;
                }
                if (makeAttackBySimpleCheck(game, cell)) {
                    break;
                }
            }
        }
    }

    /**
     * Try to attack neighbours by check on cell. Return information about attack success.
     * @param game Current game.
     * @param cell Cell from which player try attack neighbours.
     * @return True if player attack enemy.
     */
    private boolean makeAttackBySimpleCheck(Game game, Field.Cell cell) {
        Field field = game.getField();
        ArrayList<Field.Cell> way = new ArrayList<>();
        way.add(cell);

        for (Field.Cell neighbour : game.getField().neighbours(cell)) {
            try {
                TwoDimensionalDirection direction = fs.getDirectionFromStartToEnd(cell, neighbour);
                if (fs.cellExist(field, cell.getLetter() + direction.getHorizontalCoef() * 2,
                        cell.getNumber() + direction.getVerticalCoef() * 2) &&
                    field.getCell(cell.getLetter() + direction.getHorizontalCoef(),
                            cell.getNumber() + direction.getVerticalCoef()).hasCheck() &&
                    field.getCell(cell.getLetter() + direction.getHorizontalCoef(),
                            cell.getNumber() + direction.getVerticalCoef()).getCheck().getPlayerID() ==
                        game.getEnemyPlayer().getPlayerID() &&
                    !field.getCell(cell.getLetter() + direction.getHorizontalCoef() * 2,
                                cell.getNumber() + direction.getVerticalCoef() * 2).hasCheck()
                    ) {
                    way.add(field.getCell(cell.getLetter() + direction.getHorizontalCoef() * 2,
                            cell.getNumber() + direction.getVerticalCoef() * 2));
                    gs.attackCheckers(game, way);
                    return true;
                }

            } catch (GameProcessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Move forward players simple check if it exists and can be moved.
     * @param game Current game.
     * @param cell Current cell from which check moves.
     * @return True if check has been moved.
     */
    private boolean makeStepBySimpleCheck(Game game, Field.Cell cell) {
        Field field = game.getField();
        Field.Cell playerStartPoint =  game.getCurrentPlayer().getStartPoint();
        TwoDimensionalDirection direction = null;
        try {
            direction = playerStartPoint.equals(field.getCell(0, 0)) ?
                    TwoDimensionalDirection.UP : TwoDimensionalDirection.DOWN;
        } catch (CellNotExistException e) {
            e.printStackTrace();
        }

        if (abs(cell.getNumber() - playerStartPoint.getNumber()) != field.getHeight() - 1) {

            try {
                if (cell.getLetter() - playerStartPoint.getLetter() != 0 &&
                        !field.getCell(cell.getLetter() - direction.getVerticalCoef(),
                        cell.getNumber() + direction.getVerticalCoef()).hasCheck()) {

                    gs.doStep(game, cell.getLetter(), cell.getNumber(),
                            cell.getLetter() - direction.getVerticalCoef(),
                            cell.getNumber() + direction.getVerticalCoef());
                    return true;
                } else if(abs(playerStartPoint.getLetter() - cell.getLetter()) != field.getHeight() -  1 &&
                        !field.getCell(cell.getLetter() + direction.getVerticalCoef(), cell.getNumber()
                            + direction.getVerticalCoef()).hasCheck()) {

                    gs.doStep(game, cell.getLetter(), cell.getNumber(),
                            cell.getLetter() + direction.getVerticalCoef(),
                            cell.getNumber() + direction.getVerticalCoef());
                    return true;
                }
            } catch (MovementWhileAttackCanBeCarriedOutException e) {
                return false;
            } catch (CellNotExistException | SimpleCheckGoBackException | CellNotHaveChecksException
                    | CellIsNotFreeException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
