package vsu.course2.services;

import vsu.course2.game.*;
import vsu.course2.game.exceptions.*;

import java.util.ArrayList;


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
                    break;
                }
            }
        }
    }

    private boolean makeAttackBySimpleCheck(Game game, Field.Cell cell) {
        Field field = game.getField();
        ArrayList<Field.Cell> way = new ArrayList<>();
        way.add(cell);

        for (Field.Cell neighbour : game.getField().neighbours(cell)) {
            try {
                TwoDimensionalDirection direction = fs.findDirectionFromStartToEnd(cell, neighbour);
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

//    private void attackBySimpleCheck(Game game, Field.Cell cell) {
//        Field.Cell playerStartPoint =  game.getCurrentPlayer().getStartPoint();
//        TwoDimensionalDirection direction = playerStartPoint.equals(new Field.Cell(0, 0)) ?
//                TwoDimensionalDirection.UP : TwoDimensionalDirection.DOWN;
//
//        try {
//            if (Math.abs(playerStartPoint.getLetter() - cell.getLetter()) > 1 &&
//                    gs.checkOnNextLeftCellExist(game, cell) &&
//                    gs.checkOnNextLeftCellExist(game,
//                            game.getField().getCell(cell.getLetter() - direction.getVerticalCoef(),
//                                    cell.getNumber() + direction.getVerticalCoef()))) {
//                gs.attackCheckers(game,
//                        Arrays.asList( cell, new Field.Cell(cell.getLetter() - 2 * direction.getVerticalCoef(),
//                        cell.getNumber() + 2 * direction.getVerticalCoef()) ) );
//            } else {
//                gs.attackCheckers(game,
//                        Arrays.asList( cell, new Field.Cell(cell.getLetter() + 2 * direction.getVerticalCoef(),
//                                cell.getNumber() + 2 * direction.getVerticalCoef()) ) );
//            }
//        } catch (GameProcessException e) {
//            e.printStackTrace();
//        }
//    }

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
        TwoDimensionalDirection direction = playerStartPoint.equals(new Field.Cell(0, 0)) ?
                TwoDimensionalDirection.UP : TwoDimensionalDirection.DOWN;

        if (cell.getNumber() - playerStartPoint.getNumber() != 0) {

            try {
                if (cell.getLetter() != 0
                        && !field.getCell(cell.getLetter() - 1,
                        cell.getNumber() + direction.getVerticalCoef()).hasCheck()) {

                    gs.doStep(game, cell.getLetter(), cell.getNumber(),
                            cell.getLetter() - 1, cell.getNumber() + direction.getVerticalCoef());
                    return true;
                } else if(cell.getLetter() != 7
                        && !field.getCell(cell.getLetter() + 1, cell.getNumber()
                            + direction.getVerticalCoef()).hasCheck()) {

                    gs.doStep(game, cell.getLetter(), cell.getNumber(),
                            cell.getLetter() + 1, cell.getNumber() + direction.getVerticalCoef());
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
