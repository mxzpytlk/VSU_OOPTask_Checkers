package vsu.course2.services;

import vsu.course2.models.game.*;
import vsu.course2.models.game.exceptions.*;
import vsu.course2.models.game.field.Cell;
import vsu.course2.models.game.field.Field;
import vsu.course2.utills.CloneService;

import java.util.*;

import static java.lang.Math.abs;

public class GameService {
    private final FieldService fs = new FieldService();

    /**
     * Move check from first cell to second.
     * @param game Current game.
     * @param prevLetter Previous cell letter.
     * @param prevNumber Previous cell number.
     * @param nextLetter Next cell letter.
     * @param nextNumber Next cell number.
     * @throws SimpleCheckGoBackException Thrown if player try move simple check back.
     * @throws MovementWhileAttackCanBeCarriedOutException Thrown if player try make step while can attack enemy.
     * @throws CellNotExistException Thrown if previous or next cell are not exist.
     * @throws CellNotHaveChecksException Thrown if player don't have checks on previous cell.
     * @throws CellIsNotFreeException Thrown if next cell is not free.
     */
    public void doStep(Game game, int prevLetter, int prevNumber, int nextLetter, int nextNumber)
            throws SimpleCheckGoBackException, MovementWhileAttackCanBeCarriedOutException,
            CellNotExistException, CellNotHaveChecksException, CellIsNotFreeException {
        if (gameOver(game)) return;

        if (!game.getPlayers()[game.getTurnOrder()].hasCheck(fs.getChecker(prevLetter, prevNumber, game.getField())))
            throw new CellNotHaveChecksException("Player doesn't have checkers on this position");

        if (!canMakeStep(game, fs.getCell(prevLetter, prevNumber, game.getField()),
                fs.getCell(nextLetter, nextNumber, game.getField()))) {
            throw new SimpleCheckGoBackException("Checker can't go back");
        }

        if (playerCanHitEnemy(game)) {
            throw new MovementWhileAttackCanBeCarriedOutException("Check can't move if another check can attack enemy.");
        }

        fs.moveChecker(game.getField(), prevLetter, prevNumber, nextLetter, nextNumber);
        if (abs(game.getCurrentPlayer().getStartPoint().getNumber() - nextNumber) == game.getField().getHeight() - 1) {
            fs.getChecker(nextLetter, nextNumber, game.getField()).becomeKing();
        }

        game.changeTurnOrder();
    }

    /**
     * Check if current player can attack enemy.
     * @param game Current game.
     * @return True if current player can attack enemy.
     */
    private boolean playerCanHitEnemy(Game game) {
        return playerCanHitEnemyBySimpleCheck(game) || playerCanHitEnemyByKing(game);
    }

    private boolean playerCanHitEnemyByKing(Game game) {
        //TODO
        return playerCanHitEnemyBySimpleCheck(game) ;
    }

    /**
     * Check if current player have simple check, which could attack enemy check.
     * @param game Current game.
     * @return True if current player have simple check, which could attack enemy check.
     */
    private boolean playerCanHitEnemyBySimpleCheck(Game game) {
        try {
            if (checkPlayerCanHitBySimple(game)) return true;
        } catch (GameProcessException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Check if current player have simple check, which could attack enemy check.
     * @param game Current game.
     * @return True if current player have simple check, which could attack enemy check.
     * @throws CellNotExistException Thrown if method implementation had logic mistakes.
     */
    private boolean checkPlayerCanHitBySimple(Game game) throws CellNotExistException {
        Field field = game.getField();
        Player curPlayer = game.getCurrentPlayer();
        Cell playerStartPoint = game.getCurrentPlayer().getStartPoint();
        TwoDimensionalDirection leftDirection = game.getCurrentPlayer().getStartPoint()
                .equals(fs.getCell(0, 0, field)) ?
                TwoDimensionalDirection.UP_LEFT : TwoDimensionalDirection.DOWN_RIGHT;
        TwoDimensionalDirection rightDirection = game.getCurrentPlayer().getStartPoint()
                .equals(fs.getCell(0, 0, field)) ?
                TwoDimensionalDirection.UP_RIGHT : TwoDimensionalDirection.DOWN_LEFT;

        for (Cell cell : field) {
            if (cell.hasCheck() && curPlayer.hasCheck(cell.getCheck())
                    && abs(cell.getNumber() - playerStartPoint.getNumber()) < field.getHeight() - 2) {
                if (abs(playerStartPoint.getLetter() - cell.getLetter()) > 1 &&
                        checkOnNextCellExist(game, cell, leftDirection) &&
                        game.getEnemyPlayer().hasCheck(getNextCell(game, cell, leftDirection).getCheck()) &&
                        !fs.getCell(cell.getLetter() + 2 * leftDirection.getHorizontalCoef(),
                                cell.getNumber() + 2 * leftDirection.getVerticalCoef(), field).hasCheck()) {
                    return true;
                } else if (abs(cell.getLetter() - playerStartPoint.getLetter()) < field.getWidth() - 2 &&
                        checkOnNextCellExist(game, cell, rightDirection) &&
                        game.getEnemyPlayer().hasCheck(getNextCell(game, cell, rightDirection).getCheck()) &&
                        !fs.getCell(cell.getLetter() + 2 * rightDirection.getHorizontalCoef(),
                                cell.getNumber() + 2 * rightDirection.getVerticalCoef(), field).hasCheck()) {
                    return true;
                }

            }
        }
        return false;
    }

    /**
     * Find next cell on direction.
     * @param game Current game.
     * @param curCell Cell, from which next cells is found,
     * @param direction Direction in which next cell is found.
     * @return Next cell on direction.
     * @throws CellNotExistException Thrown if cell is not exist.
     */
    public Cell getNextCell(Game game, Cell curCell, TwoDimensionalDirection direction)
            throws CellNotExistException {
        Field field = game.getField();
        return fs.getCell(curCell.getLetter() + direction.getHorizontalCoef(),
                curCell.getNumber() + direction.getVerticalCoef(), field);
    }

    /**
     * Check if enemy checks next cell in direction exist.
     * @param game Current game.
     * @param curCell Cell which is checked.
     * @param direction Direction, where function check checker existing.
     * @return True, if check on next cell exist .
     */
    public boolean checkOnNextCellExist(Game game, Cell curCell, TwoDimensionalDirection direction) {
        try {
            return getNextCell(game, curCell, direction).hasCheck();
        } catch (CellNotExistException e) {
            return false;
        }
    }

    /**
     * Check if cells situated un direct line and player can make step.
     * @param game Current game.
     * @param curCell Cell from which step possibility is checking.
     * @param nextCell Cell from which step possibility is checking.
     * @return True if cells situated un direct line and player can make step.
     */
    private boolean canMakeStep(Game game, Cell curCell, Cell nextCell) {
        if (abs(game.getCurrentPlayer().getStartPoint().getNumber() - curCell.getNumber()) >=
                abs(game.getCurrentPlayer().getStartPoint().getNumber() - nextCell.getNumber())) {
            return true;
        }

        return abs(curCell.getLetter() - nextCell.getLetter()) ==
                abs(curCell.getNumber() - nextCell.getNumber());
    }

    /**
     * Make one player attack another. Find enemy checks which should be deleted. Remove attacked checks from
     * enemy players list and field. Move check on first position in list to last position in list.
     * @param game Current game.
     * @param way
     *      List with cells where player should have check which make attack. First position is players check
     *      which make attack other is cells where players check stay after each enemy check attack.
     * @throws GameProcessException
     *      Thrown if check try to attack enemy check which is far from, try to go back or try to attack empty cell.
     */
    public void attackCheckers(Game game, List<Cell> way) throws GameProcessException {
        Field field = game.getField();


        if (fs.getCell(way.get(0), field).hasCheck() &&
                game.getEnemyPlayer().hasCheck(fs.getCell(way.get(0), field).getCheck())) {
            throw new GameProcessException("Player doesn't have checkers on this position");
        }

        if (way.get(0).getCheck().isKing()) {
            attackByKing(game, way);
        } else {
            attackBySimpleCHeck(game, way);
        }
        game.changeTurnOrder();
    }

    /**
     * Make one player attack another by simple check. Find enemy checks which should be deleted. Remove attacked checks
     * from enemy players list and field. Move check on first position in list to last position in list.
     * @param game Current game.
     * @param way List with cells where player should have check which make attack. First position is players check
     *            which make attack other is cells where players check stay after each enemy check attack. Cells must be
     *            separate by only one cell with enemies check.
     * @throws GameProcessException Thrown if check try to attack enemy check which is far from, try to go back or try
     * to attack empty cell.
     */
    private void attackBySimpleCHeck(Game game, List<Cell> way) throws GameProcessException {
        Field field = game.getField();

        ArrayList<Checker> eatenChecks = new ArrayList<>();

        for (int i = 0; i < way.size() - 1; i++) {
            if (!fs.areOnDirectLine(way.get(i), way.get(i + 1))
                    || abs(way.get(i).getLetter() - way.get(i + 1).getLetter()) != 2) {
                throw new CellsAreNotOnDirectLineException(way.get(i).toString() + way.get(i + 1).toString() +
                        " are mot on direct line.");
            } else if (fs.getCell(way.get(i + 1), field).hasCheck()) {
                throw new CellIsNotFreeException(way.get(i).toString() + way.get(i + 1).toString() +
                        "\nCheck can not attack enemy if there is another check behind");
            } else if (!fs.getCellBetweenTwoCells(field, way.get(i), way.get(i + 1)).hasCheck()) {
                throw new CellIsEmptyException(way.get(i).toString() + way.get(i + 1).toString() +
                        "There is not enemy check on attack way");
            }
            eatenChecks.add(fs.getCellBetweenTwoCells(field ,way.get(i), way.get(i + 1)).getCheck());
            fs.getCellBetweenTwoCells(field, way.get(i), way.get(i + 1)).removeCheck();
            if (abs(way.get(i + 1).getNumber() - game.getCurrentPlayer().getStartPoint().getNumber()) ==
                    field.getHeight() - 1) {
                way.get(0).getCheck().becomeKing();
                if (i < way.size() - 2) {
                    eatenChecks.addAll(attackByKing(game, way.subList(i + 1, way.size() - 1)));
                }
                break;
            }
        }
        game.getEnemyPlayer().removeCheck(eatenChecks.toArray(new Checker[0]));
        fs.moveChecker(field, way.get(0), way.get(way.size() - 1));
    }

    /**
     * Make one player attack another by king check. Find enemy checks which should be deleted. Remove attacked checks
     * from enemy players list and field. Move check on first position in list to last position in list.
     * @param game Current game.
     * @param way List with cells where player should have check which make attack. First position is players check
     *            which make attack other is cells where players check stay after each enemy check attack.
     * @throws GameProcessException Thrown if check try to attack enemy check which is far from, try to go back or try
     * to attack empty cell.
     */
    private ArrayList<Checker> attackByKing(Game game, List<Cell> way) throws GameProcessException {
        Field field = game.getField();
        ArrayList<Checker> eatenChecks = new ArrayList<>();

        for (int i = 0; i < way.size() - 1; i++) {
            ArrayList<Cell> directWay = fs.getWayBetweenCells(field, way.get(i), way.get(i + 1));

            eatenChecks.addAll(fs.checkersOnLine(directWay));
            for (Cell cell : directWay) {
                cell.removeCheck();
            }
        }



        game.getEnemyPlayer().removeCheck(eatenChecks.toArray(new Checker[0]));
        fs.moveChecker(field, way.get(0), way.get(way.size() - 1));
        return eatenChecks;
    }

    /**
     * Check if one player does not have check.
     * @param game Current game.
     * @return True if one player does not have check.
     */
    public boolean gameOver(Game game) {
        return game.getPlayers()[0].getCheckers().length == 0 || game.getPlayers()[1].getCheckers().length == 0;
    }

    public Map<Cell, List<List<Cell>>> getPossibleWays(Game game) {
        Field field = game.getField();
        Map<Cell, List<List<Cell>>> attacks = new HashMap<>();
        Map<Cell, List<List<Cell>>> steps = new HashMap<>();
        for (Cell cell : field) {
            if (cell.getCheck() != null && game.getCurrentPlayer().hasCheck(cell.getCheck())
                    && !cell.getCheck().isKing()) {
                List<List<Cell>> possibleWays = getPossibleAttacksToSimpleCheck(cell, game);
                if (possibleWays.size() > 0) {
                    attacks.put(cell, possibleWays);
                }
            }

            if (cell.getCheck() != null && game.getCurrentPlayer().hasCheck(cell.getCheck())
                    && cell.getCheck().isKing()) {
                List<List<Cell>> possibleWays = getPossibleAttacksToKing(cell, game);
                if (possibleWays.size() > 0) {
                    attacks.put(cell, possibleWays);
                }
            }

            if (cell.hasCheck() && !cell.getCheck().isKing()
                    && game.getCurrentPlayer().hasCheck(cell.getCheck()) &&  attacks.size() == 0) {


                List<List<Cell>> possibleWays = getPossibleWaysToSimpleCheck(cell, game);
                if (possibleWays.size() > 0) {
                    steps.put(cell, possibleWays);
                }
            }

            if (cell.hasCheck() && cell.getCheck().isKing()
                    && game.getCurrentPlayer().hasCheck(cell.getCheck()) &&  attacks.size() == 0) {


                List<List<Cell>> possibleWays = getPossibleWaysToKing(cell, game);
                if (possibleWays.size() > 0) {
                    steps.put(cell, possibleWays);
                }
            }
        }
        return attacks.size() > 0 ? attacks : steps;
    }

    private List<List<Cell>> getPossibleWaysToSimpleCheck(Cell cell, Game game) {
        List<List<Cell>> ways = new ArrayList<>();
        Field field = game.getField();

        Cell playerStartPoint =  game.getCurrentPlayer().getStartPoint();

        if (abs(cell.getNumber() - playerStartPoint.getNumber()) != field.getHeight() - 1) {
            try {
                TwoDimensionalDirection direction = playerStartPoint.equals(fs.getCell(0, 0, field)) ?
                        TwoDimensionalDirection.UP : TwoDimensionalDirection.DOWN;
                if (cell.getLetter() - playerStartPoint.getLetter() != 0 &&
                        !fs.getCell(cell.getLetter() - direction.getVerticalCoef(),
                                cell.getNumber() + direction.getVerticalCoef(), field).hasCheck()) {

                    ways.add(Arrays.asList(cell, fs.getCell(cell.getLetter() - direction.getVerticalCoef(),
                            cell.getNumber() + direction.getVerticalCoef(), field)));
                }
                if(abs(playerStartPoint.getLetter() - cell.getLetter()) != field.getHeight() -  1 &&
                        !fs.getCell(cell.getLetter() + direction.getVerticalCoef(), cell.getNumber()
                                + direction.getVerticalCoef(), field).hasCheck()) {

                    ways.add(Arrays.asList(cell, fs.getCell(cell.getLetter() + direction.getVerticalCoef(),
                            cell.getNumber() + direction.getVerticalCoef(), field)));
                }
            } catch (CellNotExistException e) {
                e.printStackTrace();
            }
        }

        return ways;
    }

    private List<List<Cell>> getPossibleAttacksToSimpleCheck(Cell cell, Game game) {
        List<List<Cell>> ways = new LinkedList<>();
        Field field = game.getField();

        for (Cell neighbour : fs.neighbours(cell, game.getField())) {
            try {
                TwoDimensionalDirection direction = fs.getDirectionFromStartToEnd(cell, neighbour);
                if (fs.cellExist(field, cell.getLetter() + direction.getHorizontalCoef() * 2,
                        cell.getNumber() + direction.getVerticalCoef() * 2) &&
                        fs.getCell(cell.getLetter() + direction.getHorizontalCoef(),
                                cell.getNumber() + direction.getVerticalCoef(), field).hasCheck()
                        && game.getEnemyPlayer().hasCheck(fs.getCell(cell.getLetter() + direction.getHorizontalCoef(),
                        cell.getNumber() + direction.getVerticalCoef(), field).getCheck()) &&
                        !fs.getCell(cell.getLetter() + direction.getHorizontalCoef() * 2,
                                cell.getNumber() + direction.getVerticalCoef() * 2, field).hasCheck()
                ) {
                    ways.add(Arrays.asList(cell, fs.getCell(cell.getLetter() + direction.getHorizontalCoef() * 2,
                            cell.getNumber() + direction.getVerticalCoef() * 2, field)));
                }

            } catch (GameProcessException e) {
                e.printStackTrace();
            }
        }

        List<List<Cell>> finalWays = new LinkedList<>();

        for (List<Cell> way : ways) {
            Game newGame = new CloneService<Game>().makeClone(game);

            try {
                attackCheckers(newGame, way);
                newGame.changeTurnOrder();
                List<List<Cell>> newWays =
                        !fs.getCell(fs.getCell(way.get(way.size() - 1), newGame.getField()), newGame.getField()).
                                getCheck().isKing() ?
                        getPossibleAttacksToSimpleCheck(fs.getCell(way.get(way.size() - 1),
                                newGame.getField()), newGame) :
                                getPossibleAttacksToKing(fs.getCell(way.get(way.size() - 1),
                                        newGame.getField()), newGame);
                if (newWays.size() == 0) {
                    finalWays.add(way);
                }

                for (List<Cell> newWay : newWays) {
                    List<Cell> addedWay = new LinkedList<>(way);
                    addedWay.addAll(newWay.subList(1, newWay.size()));
                    finalWays.add(addedWay);
                }
            } catch (GameProcessException e) {
                e.printStackTrace();
            }
        }


        return finalWays;
    }

    private List<List<Cell>> getPossibleWaysToKing(Cell cell, Game game) {
        List<List<Cell>> ways = new LinkedList<>();
        TwoDimensionalDirection[] directions = {TwoDimensionalDirection.DOWN_LEFT,
                TwoDimensionalDirection.DOWN_RIGHT,
                TwoDimensionalDirection.UP_RIGHT,
                TwoDimensionalDirection.UP_LEFT
        };

        for (TwoDimensionalDirection direction : directions) {
            List<Cell> way = fs.getWayToBoard(cell, direction, game.getField());
            for (Cell nextPos : way) {
                if (nextPos.hasCheck()) {
                    break;
                }
                ways.add(Arrays.asList(cell, nextPos));
            }
        }
        return ways;
    }

    private List<List<Cell>> getPossibleAttacksToKing(Cell cell, Game game) {
        List<List<Cell>> ways = new ArrayList<>();
        Field field = game.getField();
        TwoDimensionalDirection[] directions = {TwoDimensionalDirection.DOWN_LEFT,
                TwoDimensionalDirection.DOWN_RIGHT,
                TwoDimensionalDirection.UP_RIGHT,
                TwoDimensionalDirection.UP_LEFT};

        for (TwoDimensionalDirection direction : directions) {
            List<Cell> wayToBoard = fs.getWayToBoard(cell, direction, field);
            boolean shouldAddCell = false;
            for (int i = 0; i < wayToBoard.size() - 1; i++) {
                if (wayToBoard.get(i).hasCheck()
                        && (game.getCurrentPlayer().hasCheck(wayToBoard.get(i).getCheck())
                        || wayToBoard.get(i + 1).hasCheck())) {
                    break;
                }

                try {
                    if (wayToBoard.get(i).hasCheck() && wayToBoard.get(i + 1).getCheck() == null
                            && game.getEnemyPlayer().hasCheck(wayToBoard.get(i).getCheck())) {
                        ways.add(Arrays.asList(cell, getNextCell(game, wayToBoard.get(i), direction)));
                        i++;
                        shouldAddCell = true;
                    } else if (shouldAddCell) {
                        ways.add(Arrays.asList(cell, fs.getCell(wayToBoard.get(i), game.getField())));
                    }
                } catch (CellNotExistException e) {
                    e.printStackTrace();
                }
            }
        }

        List<List<Cell>> finalWays = new LinkedList<>();

        for (List<Cell> way : ways) {
            Game newGame = new CloneService<Game>().makeClone(game);

            try {
                attackCheckers(newGame, way);
                newGame.changeTurnOrder();
                List<List<Cell>> newWays =
                        getPossibleAttacksToKing(fs.getCell(way.get(way.size() - 1), newGame.getField()), newGame);
                if (newWays.size() == 0) {
                    finalWays.add(way);
                }

                for (List<Cell> newWay : newWays) {
                    List<Cell> addedWay = new LinkedList<>(way);
                    addedWay.addAll(newWay.subList(1, newWay.size()));
                    finalWays.add(addedWay);
                }
            } catch (GameProcessException e) {
                e.printStackTrace();
            }
        }

        return finalWays;
    }
}
