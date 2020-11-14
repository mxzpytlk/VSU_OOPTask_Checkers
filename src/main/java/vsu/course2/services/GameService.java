package vsu.course2.services;

import vsu.course2.game.*;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    FieldService fs = new FieldService();

    public GameService() { }

    public void doStep(Game game, int prevLetter, int prevNumber, int nextLetter, int nextNumber) throws GameProcessException {
        if (gameOver(game)) return;

        if (!game.getPlayers()[game.getTurnOrder()].hasCheck(game.getField().getChecker(prevLetter, prevNumber)))
            throw new GameProcessException("Player doesn't have checkers on this position");

        if (!canMakeStep(game, game.getField().getCell(prevLetter, prevNumber),
                game.getField().getCell(nextLetter, nextNumber))) {
            throw new GameProcessException("Checker can't go back");
        }

        if (playerCanHitEnemy(game)) {
            throw new GameProcessException("Check can't move if another check can attack enemy.");
        }

        fs.moveChecker(game.getField(), prevLetter, prevNumber, nextLetter, nextNumber);
        if (game.getPlayers()[game.getTurnOrder()].getStartPoint().getLetter() - nextLetter == 0) {
            game.getField().getChecker(nextLetter, nextNumber).becomeKing();
        }

        game.changeTurnOrder();
    }

    private boolean playerCanHitEnemy(Game game) {
        return playerCanHitEnemyBySimpleCheck(game) || playerCanHitEnemyByKing(game);
    }

    private boolean playerCanHitEnemyByKing(Game game) {
        //TODO:
        return false;
    }

    private boolean playerCanHitEnemyBySimpleCheck(Game game) {
        Field field = game.getField();
        int playerID = game.getPlayer().getPlayerID();
        Field.Cell playerStartPoint =  game.getPlayer().getStartPoint();
        Direction direction = game.getPlayer().getStartPoint().equals(new Field.Cell(0, 0)) ?
                Direction.UP : Direction.DOWN;

        for (Field.Cell cell : field) {
            if (cell.hasCheck() && cell.getCheck().getPlayerID() == playerID
                    && (cell.getNumber() - playerStartPoint.getNumber() != 0)) {
                try {
                    if (checkOnNextLeftCellExist(field, playerID, playerStartPoint, direction, cell)) {
                        return true;
                    } else if (checkOnNextRightCellExist(field, playerID, playerStartPoint, direction, cell)) {
                        return true;
                    }
                } catch (GameProcessException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    private boolean checkOnNextLeftCellExist(Field field, int playerID,
                                             Field.Cell playerStartPoint,
                                             Direction direction, Field.Cell curCell) throws GameProcessException {
        return curCell.getLetter() - playerStartPoint.getLetter() != 0 &&
                field.getCell(curCell.getLetter() - direction.getCoef(),
                        curCell.getNumber() + direction.getCoef()).hasCheck() &&
                field.getCell(curCell.getLetter() - direction.getCoef(),
                        curCell.getNumber() + direction.getCoef()).getCheck()
                        .getPlayerID() != playerID;
    }

    private boolean checkOnNextRightCellExist(Field field, int playerID,
                                             Field.Cell playerStartPoint,
                                             Direction direction, Field.Cell curCell) throws GameProcessException {
        return Math.abs(curCell.getLetter() - playerStartPoint.getLetter()) != 7 &&
                field.getCell(curCell.getLetter() + direction.getCoef(),
                        curCell.getNumber() + direction.getCoef()).hasCheck() &&
                field.getCell(curCell.getLetter() + direction.getCoef(),
                        curCell.getNumber() + direction.getCoef()).getCheck()
                        .getPlayerID() != playerID;
    }

    private boolean canMakeStep(Game game, Field.Cell curCell, Field.Cell nextCell) {
        if (Math.abs(game.getPlayers()[game.getTurnOrder()].getStartPoint().getNumber() - curCell.getNumber()) >=
                Math.abs(game.getPlayers()[game.getTurnOrder()].getStartPoint().getNumber() - nextCell.getNumber())) {
            return true;
        }

        return Math.abs(curCell.getLetter() - nextCell.getLetter()) ==
                        Math.abs(curCell.getNumber() - nextCell.getNumber());
    }

    public ArrayList<Checker> attackCheckers(Game game, ArrayList<Field.Cell> way) throws GameProcessException {
        Field field = game.getField();
        Player[] players = game.getPlayers();

        if (field.getCell(way.get(0)).hasCheck() &&
                field.getCell(way.get(0)).getCheck().getPlayerID() != players[game.getTurnOrder()].id()) {
            throw new GameProcessException("Player doesn't have checkers on this position");
        }
        ArrayList<Checker> eatenChecks =
                way.get(0).hasCheck() ? attackByKing(game, way) : attackBySimpleCHeck(game, way);
        fs.moveChecker(field, way.get(0), way.get(way.size() - 1));
        game.changeTurnOrder();
        players[game.getTurnOrder()].removeCheck(eatenChecks.toArray(new Checker[0]));
        return eatenChecks;
    }

    private ArrayList<Checker> attackBySimpleCHeck(Game game, ArrayList<Field.Cell> way) throws GameProcessException {
        Field field = game.getField();
        Player[] players = game.getPlayers();
        ArrayList<Checker> eatenChecks = new ArrayList<>();

        for (int i = 0; i < way.size() - 1; i++) {
            if (!fs.areOnDirectLine(way.get(i), way.get(i + 1))
                    || Math.abs(way.get(i).getLetter() - way.get(i + 1).getLetter()) != 2){
                throw new GameProcessException("Simple check can't make this move");
            } else if (field.getCell(way.get(i + 1)).hasCheck()) {
                throw new GameProcessException("Check can not attack enemy if there is another check behind");
            } else if (!fs.getCellBetweenTwoCells(field, way.get(i), way.get(i + 1)).hasCheck()) {
                throw new GameProcessException("There is not enemy check on attack way");
            }

            eatenChecks.add(fs.getCellBetweenTwoCells(field ,way.get(i), way.get(i + 1)).getCheck());
            if (Math.abs(way.get(i + 1).getLetter() - players[game.getTurnOrder()].getStartPoint().getLetter()) == 8) {
                way.get(0).getCheck().becomeKing();
                eatenChecks.addAll(attackByKing(game, way.subList(i + 1, way.size() - 1)));
                break;
            }
        }
        return eatenChecks;
    }

    private ArrayList<Checker> attackByKing(Game game, List<Field.Cell> way) throws GameProcessException {
        Field field = game.getField();
        ArrayList<Checker> eatenChecks = new ArrayList<>();

        for (int i = 0; i < way.size() - 1; i++) {
            ArrayList<Field.Cell> directWay = fs.getWayBetweenCells(field, way.get(i), way.get(i + 1));

            if (directWay.get(directWay.size() - 1).hasCheck()) {
                throw new GameProcessException("There are checkers on the way");
            }

            if (!canAttack(directWay)) {
                throw new GameProcessException("Checker can't attack another check " +
                        "if there is check after enemy");
            }
            eatenChecks.addAll(fs.checkersOnLine(directWay));
        }
        return eatenChecks;
    }

    private boolean canAttack(ArrayList<Field.Cell> directWay) {
        boolean hasCheckerOnDirection,
                result = false;

        for (int i = 1; i < directWay.size() - 1; i++) {
            hasCheckerOnDirection = directWay.get(i).hasCheck();
            if (hasCheckerOnDirection) {
                result = true;
                if (directWay.get(i + 1).hasCheck()) {
                    return false;
                }
            }
        }
        return result;
    }

    public boolean gameOver(Game game) {
        return game.getPlayers()[0].getCheckers().length == 0 || game.getPlayers()[1].getCheckers().length == 0;
    }
}
