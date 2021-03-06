import java.util.*;

/**
 * For move without crash
 */
class Move {
    Utils utils = new Utils();
    LocateOpponent locateOpponent = new LocateOpponent();
    private int destinationSector = 4; // 5 in fact
    private static int EMPTY = 0;
    private static int ISLAND = 1;
    private static int ALREADY_VISITED = 2;

    int map[][];

    /**
     * cell random move
     */
    public String moveIA1(Submarine mySubmarine, Board board) {
        System.err.println("move IA1 in progress ");
        // list of future move (4 cell auround mySubmarine position)
        List<Cell> listAllPossibleMove = getListOfPossibleMove(mySubmarine);

        // remove cells out of map
        List<Cell> listWithoutMapLimit = utils.removeEndOfMap(listAllPossibleMove, board);

        // remove earth on last list
        List<Cell> listWithoutMapLimitAndEarth = utils.removeEarthCellTable(listWithoutMapLimit, board);

        // remove alreadyVisit cell on last list
        List<Cell> listWithoutMapLimitEarthAndAlreadyVisited = utils.removeAlreadyVisitedCell(listWithoutMapLimitAndEarth, board);

        // random on cell with last list
        if (!listWithoutMapLimitEarthAndAlreadyVisited.isEmpty()) {
            Cell cellToMoveRandom = utils.randomCellOnList(listWithoutMapLimitEarthAndAlreadyVisited);
            // record my next move
            mySubmarine.setMyNextMove(cellToMoveRandom);

            // move to random cell
            if (cellToMoveRandom.getX() != -10) {
                if (cellToMoveRandom.getCardinalPoint() == "N ") { return "MOVE " + cellToMoveRandom.getCardinalPoint(); }
                if (cellToMoveRandom.getCardinalPoint() == "E ") { return "MOVE " + cellToMoveRandom.getCardinalPoint(); }
                if (cellToMoveRandom.getCardinalPoint() == "W ") { return "MOVE " + cellToMoveRandom.getCardinalPoint(); }
                if (cellToMoveRandom.getCardinalPoint() == "S ") { return "MOVE " + cellToMoveRandom.getCardinalPoint(); }
            }
        }
        return "SURFACE";
    }

    /**
     * balayage
     * @param mySubmarine
     * @param board
     * @return
     */
    public String moveIA2(Submarine mySubmarine, Board board) {
        System.err.println("move IA2 in progress ");
        Cell myPositionCell = new Cell(mySubmarine.getPositionX(), mySubmarine.getPositionY(), null, null);
        Cell destinationCell = new Cell(0,14,null,null);

        // list of future move (4 cell auround mySubmarine position)
        List<Cell> listAllPossibleMove = getListOfPossibleMove(mySubmarine);

        // remove cells out of map
        List<Cell> listWithoutMapLimit = utils.removeEndOfMap(listAllPossibleMove, board);

        // remove earth on last list
        List<Cell> listWithoutMapLimitAndEarth = utils.removeEarthCellTable(listWithoutMapLimit, board);

        // remove alreadyVisit cell on last list
        List<Cell> listWithoutMapLimitEarthAndAlreadyVisited = utils.removeAlreadyVisitedCell(listWithoutMapLimitAndEarth, board);

        // to each cell add distance for destination
        for (Cell cell: listWithoutMapLimitEarthAndAlreadyVisited) {
            Integer distance = utils.distanceFromCell(myPositionCell, destinationCell);
            cell.setDistanceToDestinationCell(distance);
        }
        // calc better path

        if (!listWithoutMapLimitEarthAndAlreadyVisited.isEmpty()) {
            Comparator<Cell> comparator = Comparator.comparing( Cell::getDistanceToDestinationCell);
            Cell betterPathCell = listWithoutMapLimitEarthAndAlreadyVisited.stream()
                    .min(comparator).get();
            // check
//            for (Cell cell:listWithoutMapLimitEarthAndAlreadyVisited ) {
//                System.err.println("test: " + cell.toString());
//            }
            // record my next move
            mySubmarine.setMyNextMove(betterPathCell);
            return "MOVE " + betterPathCell.getCardinalPoint();
        } else {
            return "SURFACE";
        }

    }

    /**
     * sector random move
     * @param mySubmarine
     * @param board
     * @return
     */
    public String moveIA3(Submarine mySubmarine, Board board) {
        Random rand = new Random();
        // limit for random 9 -> all secteur
        int max = 8;
        int min = 0;
        System.err.println("move IA3 in progress ");
        mySubmarine.setDestinationSector(destinationSector);

        // check
        System.err.println("my submarine destination sector is: " + (mySubmarine.getDestinationSector()+1));
        // check
        System.err.println("my submarine is on sector: " + (mySubmarine.getMySector()));

        // random sector if destination is ok
        if ((mySubmarine.getDestinationSector()+1) == mySubmarine.getMySector()) {
            destinationSector = rand.nextInt(max - min + 1) + min;
        }

        // get list of cell on this sector
        List<Cell> listSectorDestination = board.getListSecteurs().get(destinationSector).getListCell();

        // get a cell in destination sector
        Cell destinationCell = utils.randomCellOnList(listSectorDestination);
        String goToDestination = moveToOneCell(mySubmarine, board, destinationCell);
        // check
//            System.err.println("i move to " + destinationCell);
        return goToDestination;
    }

    /**
     * Avoid dead end with full fill algorithm
     * @param mySubmarine
     * @param board
     * @return
     */
    public String moveIA4(Submarine mySubmarine, Board board) {
        map = board.getMap();
        Cell myCell = new Cell(mySubmarine.getPositionX(), mySubmarine.getPositionY(), null, null);

        String direction = getBestDirection(myCell,board, mySubmarine);
        if (direction != "-") {
            return direction;
        } else {
            board.resetVisitedCell();
            return "SURFACE";
        }
    }

    /**
     * Avoid dead end(full fill algo) and follow opp movement if possible
     * @param mySubmarine
     * @param board
     * @return
     */
    public String moveIA5(Submarine mySubmarine, Board board) {
        map = board.getMap();
        String direction = mixMoveIA5AndFollowOpp(mySubmarine, board);

        if (direction != "-") {
            return direction;
        } else {
            board.resetVisitedCell();
            return "SURFACE";
        }
    }

    public String mixMoveIA5AndFollowOpp(Submarine mySubmarine, Board board) {
        Cell myCell = new Cell(mySubmarine.getPositionX(), mySubmarine.getPositionY(), null, null);
        String oppOrders = locateOpponent.readOpponentMove(mySubmarine.getOpponentOrders());

        board.setVisitedCell(myCell);

        Cell northCell = new Cell(mySubmarine.getPositionX(), mySubmarine.getPositionY() - 1, "N ", null);
        Cell southCell = new Cell(mySubmarine.getPositionX(), mySubmarine.getPositionY() + 1, "S ", null);
        Cell eastCell = new Cell(mySubmarine.getPositionX() + 1 , mySubmarine.getPositionY(), "E ", null);
        Cell westCell = new Cell(mySubmarine.getPositionX() - 1, mySubmarine.getPositionY(), "W ", null);

        int northCells = backtrack(map, northCell, board, new ArrayList<>());
        int southCells = backtrack(map, southCell, board, new ArrayList<>());
        int eastCells = backtrack(map, eastCell, board, new ArrayList<>());
        int westCells = backtrack(map, westCell, board, new ArrayList<>());

        System.err.println("N= " + northCells + " S= " + southCells + " E= " + eastCells + " W= " + westCells);

        int maxCells = Math.max(northCells, Math.max(southCells, Math.max(eastCells, westCells)));

        if (maxCells == 0) {
            return "-";
        }
        else if (oppOrders.equals("N") && northCells > 1) {
            mySubmarine.setMyNextMove(northCell);
            mySubmarine.setPossibilityOfCellsLeak(northCells);
            System.err.println("Following opp on N direction");
            return "MOVE N ";
        }
        else if (oppOrders.equals("S") && southCells > 1) {
            mySubmarine.setMyNextMove(southCell);
            mySubmarine.setPossibilityOfCellsLeak(southCells);
            System.err.println("Following opp on S direction");
            return "MOVE S ";
        }
        else if (oppOrders.equals("E") && eastCells > 1) {
            mySubmarine.setMyNextMove(eastCell);
            mySubmarine.setPossibilityOfCellsLeak(eastCells);
            System.err.println("Following opp on E direction");
            return "MOVE E ";
        }
        else if (oppOrders.equals("W") && westCells > 1) {
            mySubmarine.setMyNextMove(westCell);
            mySubmarine.setPossibilityOfCellsLeak(westCells);
            System.err.println("Following opp on W direction");
            return "MOVE W ";
        } else {
            if (northCells == maxCells) {
                mySubmarine.setMyNextMove(northCell);
                mySubmarine.setPossibilityOfCellsLeak(northCells);
                return "MOVE N ";
            }
            else if (southCells == maxCells) {
                mySubmarine.setMyNextMove(southCell);
                mySubmarine.setPossibilityOfCellsLeak(southCells);
                return "MOVE S ";
            }
            else if (eastCells == maxCells) {
                mySubmarine.setMyNextMove(eastCell);
                mySubmarine.setPossibilityOfCellsLeak(eastCells);
                return "MOVE E ";
            }
            else {
                mySubmarine.setMyNextMove(westCell);
                mySubmarine.setPossibilityOfCellsLeak(westCells);
                return "MOVE W ";
            }
        }

    }

    public String getBestDirection(Cell myPosCell, Board board, Submarine mySubmarine) {
        board.setVisitedCell(myPosCell);

        Cell northCell = new Cell(myPosCell.getX(), myPosCell.getY() - 1, "N ", null);
        Cell southCell = new Cell(myPosCell.getX(), myPosCell.getY() + 1, "S ", null);
        Cell eastCell = new Cell(myPosCell.getX() + 1 , myPosCell.getY(), "E ", null);
        Cell westCell = new Cell(myPosCell.getX() - 1, myPosCell.getY(), "W ", null);

        int northCells = backtrack(map, northCell, board, new ArrayList<>());
        int southCells = backtrack(map, southCell, board, new ArrayList<>());
        int eastCells = backtrack(map, eastCell, board, new ArrayList<>());
        int westCells = backtrack(map, westCell, board, new ArrayList<>());

        System.err.println("N= " + northCells + " S= " + southCells + " E= " + eastCells + " W= " + westCells);

        int maxCells = Math.max(northCells, Math.max(southCells, Math.max(eastCells, westCells)));

        if (maxCells == 0) {
            return "-";
        }

        if (northCells == maxCells) {
            mySubmarine.setMyNextMove(northCell);
            mySubmarine.setPossibilityOfCellsLeak(northCells);
            return "MOVE N ";
        }
        if (southCells == maxCells) {
            mySubmarine.setMyNextMove(southCell);
            mySubmarine.setPossibilityOfCellsLeak(southCells);
            return "MOVE S ";
        }
        if (eastCells == maxCells) {
            mySubmarine.setMyNextMove(eastCell);
            mySubmarine.setPossibilityOfCellsLeak(eastCells);
            return "MOVE E ";
        }
        mySubmarine.setMyNextMove(westCell);
        mySubmarine.setPossibilityOfCellsLeak(westCells);
        return "MOVE W ";
    }

    public int backtrack(int[][] map, Cell cell, Board board, ArrayList<Cell> visited) {


        if (!utils.isOnTheMap(cell) || !utils.isSafeToMove(cell, board) || arrayContainsPos(visited, cell)) {
//            System.err.println("Dead end");
            return 0;
        }

        board.setVisitedCell(cell);
        visited.add(cell);

        Cell northCell = new Cell(cell.getX(), cell.getY() - 1, null, null);
        Cell southCell = new Cell(cell.getX(), cell.getY() + 1, null, null);
        Cell eastCell = new Cell(cell.getX() + 1 , cell.getY(), null, null);
        Cell westCell = new Cell(cell.getX() - 1, cell.getY(), null, null);

        int northCellsBackTrack = backtrack(map, northCell, board, visited);
        int southCellsBackTrack = backtrack(map, southCell, board, visited);
        int eastCellsBackTrack = backtrack(map, eastCell, board, visited);
        int westCellsBackTrack = backtrack(map, westCell, board, visited);


        map[cell.getX()][cell.getY()] = EMPTY; // for have cell same as before

        return Math.max(northCellsBackTrack, Math.max(southCellsBackTrack, Math.max(eastCellsBackTrack, westCellsBackTrack)) + 1);
    }

    private boolean arrayContainsPos(ArrayList<Cell> arr, Cell cell ) {
        for (int i = 0; i < arr.size(); i++) {
            if(arr.get(i).equals(cell)) {
                return true;
            }
        }
        return false;
    }

    public Cell getOneCellOnrandomSector(Submarine mySubmarine, Board board) {
        List<Sector> listOfAllSector = board.getListSecteurs();
        Random rand = new Random();

        // get random sector
        int max = listOfAllSector.size() - 1;
        int min = 0;
        int randomSector = rand.nextInt(max - min + 1) + min;

        // get cell list of sector
        Sector randomSecteur = board.getListSecteurs().get(randomSector);
        // check
        System.err.println("i move to sector " + randomSecteur.getId());
        // record sector destination
        mySubmarine.setDestinationSector(randomSecteur.getId());
        // get random cell on it
        Cell randomCellOnSector = utils.randomCellOnList(randomSecteur.getListCell());
        return randomCellOnSector;
    }

    public String moveToOneCell(Submarine mySubmarine, Board board, Cell destinationCell) {
        Cell myPositionCell = new Cell(mySubmarine.getPositionX(), mySubmarine.getPositionY(), null, null);

        // list of future move (4 cell auround mySubmarine position)
        List<Cell> listAllPossibleMove = getListOfPossibleMove(mySubmarine);

        // remove cells out of map
        List<Cell> listWithoutMapLimit = utils.removeEndOfMap(listAllPossibleMove, board);

        // remove earth on last list
        List<Cell> listWithoutMapLimitAndEarth = utils.removeEarthCellTable(listWithoutMapLimit, board);

        // remove alreadyVisit cell on last list
        List<Cell> listWithoutMapLimitEarthAndAlreadyVisited = utils.removeAlreadyVisitedCell(listWithoutMapLimitAndEarth, board);

        // to each cell add distance for destination
        for (Cell cell: listWithoutMapLimitEarthAndAlreadyVisited) {
            Integer distance = utils.distanceFromCell(myPositionCell, destinationCell);
            cell.setDistanceToDestinationCell(distance);
        }
        // calc better path
        if (!listWithoutMapLimitEarthAndAlreadyVisited.isEmpty()) {
            // add distance with destination
            Cell betterPathCell = new Cell();
            int distance;
            for (int i = 0; i < listWithoutMapLimitEarthAndAlreadyVisited.size(); i++) {
                distance = utils.distanceFromCell(listWithoutMapLimitEarthAndAlreadyVisited.get(i), destinationCell);
                listWithoutMapLimitEarthAndAlreadyVisited.get(i).setDistanceToDestinationCell(distance);
//                System.err.println("distance " + distance);
            }
            // choice the min distance
            int min = 2000;
            for (int i = 0; i < listWithoutMapLimitEarthAndAlreadyVisited.size(); i++) {
                if (listWithoutMapLimitEarthAndAlreadyVisited.get(i).getDistanceToDestinationCell() < min) {
                    min = listWithoutMapLimitEarthAndAlreadyVisited.get(i).getDistanceToDestinationCell();
                    betterPathCell = listWithoutMapLimitEarthAndAlreadyVisited.get(i);
                }
            }
            // check
//            System.err.println("min " + min);
            // record my next move
            mySubmarine.setMyNextMove(betterPathCell);
            return "MOVE " + betterPathCell.getCardinalPoint();
        } else {
            return "SURFACE";
        }
    }

    public List<Cell> getListOfPossibleMove(Submarine mySubmarine) {
        // list of future move (4 cell auround mySubmarine position)
        List<Cell> listAllPossibleMove = new ArrayList<>();

        // create list of possible move cell
        Cell northCell = new Cell();
        northCell.setX(mySubmarine.getPositionX());
        northCell.setY(mySubmarine.getPositionY() - 1);
        northCell.setCardinalPoint("N ");
        listAllPossibleMove.add(northCell);

        Cell southCell = new Cell();
        southCell.setX(mySubmarine.getPositionX());
        southCell.setY(mySubmarine.getPositionY() + 1);
        southCell.setCardinalPoint("S ");
        listAllPossibleMove.add(southCell);

        Cell westCell = new Cell();
        westCell.setX(mySubmarine.getPositionX() - 1);
        westCell.setY(mySubmarine.getPositionY());
        westCell.setCardinalPoint("W ");
        listAllPossibleMove.add(westCell);

        Cell estCell = new Cell();
        estCell.setX(mySubmarine.getPositionX() + 1);
        estCell.setY(mySubmarine.getPositionY());
        estCell.setCardinalPoint("E ");
        listAllPossibleMove.add(estCell);

        return listAllPossibleMove;
    }
}