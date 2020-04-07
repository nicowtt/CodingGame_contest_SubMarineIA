import java.util.*;

/**
 * For move without crash
 */
class Move {
    Utils utils = new Utils();
    private int destinationSector = 4; // 5 in fact
    private static int EMPTY = 0;

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