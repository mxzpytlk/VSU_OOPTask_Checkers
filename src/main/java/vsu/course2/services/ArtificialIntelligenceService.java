package vsu.course2.services;

import vsu.course2.game.*;

public class ArtificialIntelligenceService {
    private final GameService gs = new GameService();

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
            if (makeStepBySimpleCheck(game, playerID, cell)) break;
        }
    }

    /**
     * Move forward players simple check if it exists and can be moved.
     * @param game
     *      Current game.
     * @param playerID
     *      ID of player, who's step is current.
     * @param cell
     *      Current cell from which check moves.
     * @return
     *      True if check has been moved.
     */
    private boolean makeStepBySimpleCheck(Game game, int playerID, Field.Cell cell) {
        Field field = game.getField();
        Field.Cell playerStartPoint =  game.getPlayer().getStartPoint();
        Direction direction = playerStartPoint.equals(new Field.Cell(0, 0)) ?
                Direction.UP : Direction.DOWN;

        if (cell.hasCheck() && cell.getCheck().getPlayerID() == playerID
                && (cell.getNumber() - playerStartPoint.getNumber() != 0)) {
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
            } catch (GameProcessException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
