package vsu.course2.services;

import vsu.course2.game.*;

public class ArtificialIntelligenceService {
    private final GameService gs = new GameService();

    ArtificialIntelligenceService() {}

    private enum Direction {
        UP(1),
        DOWN(-1);

        int coef;
        Direction(int coef) {
            this.coef = coef;
        }
    }

    /**
     * Move first players check which program founds and which can be moved.
     * @param game
     *      Current game.
     * @param playerID
     *      ID of player, who's step is current.
     */
    public void makeStep(Game game, int playerID) {
        Field field = game.getField();
        Field.Cell playerStartPoint =  game.getPlayer().getStartPoint();
        Direction direction = playerStartPoint.equals(new Field.Cell(0, 0)) ?
                Direction.UP : Direction.DOWN;

        for (Field.Cell cell: field.getCells()) {
            if (cell.hasCheck() && cell.getCheck().getPlayerID() == playerID
                    && (cell.getNumber() - playerStartPoint.getNumber() != 0)) {
                try {
                    if (cell.getLetter() != 0
                            && !field.getCell(cell.getLetter() - 1, cell.getNumber() + direction.coef).hasCheck()) {

                        gs.doStep(game, cell.getLetter(), cell.getNumber(),
                                cell.getLetter() - 1, cell.getNumber() + direction.coef);
                        break;
                    } else if(cell.getLetter() != 7
                            && !field.getCell(cell.getLetter() + 1, cell.getNumber() + direction.coef).hasCheck()) {

                        gs.doStep(game, cell.getLetter(), cell.getNumber(),
                                cell.getLetter() + 1, cell.getNumber() + direction.coef);
                        break;
                    }
                } catch (GameProcessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
