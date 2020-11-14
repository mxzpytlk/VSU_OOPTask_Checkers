package vsu.course2.services;

import vsu.course2.game.*;

public class ArtificialIntelligenceService {
    private final GameService gs = new GameService();

    ArtificialIntelligenceService() {}

    public void makeStep(Game game, int playerID) {
        Field field = game.getField();
        for (Field.Cell cell: field.getCells()) {
            if (cell.hasCheck() && cell.getCheck().getPlayerID() == playerID) {
                try {
                    if (cell.getLetter() != 0
                            && !field.getCell(cell.getLetter() - 1, cell.getNumber() + 1).hasCheck()) {

                        gs.doStep(game, cell.getLetter(), cell.getNumber(),
                                cell.getLetter() - 1, cell.getNumber() + 1);
                        break;
                    } else if(cell.getLetter() != 7
                            && !field.getCell(cell.getLetter() + 1, cell.getNumber() + 1).hasCheck()) {

                        gs.doStep(game, cell.getLetter(), cell.getNumber(),
                                cell.getLetter() + 1, cell.getNumber() + 1);
                        break;
                    }
                } catch (GameProcessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
