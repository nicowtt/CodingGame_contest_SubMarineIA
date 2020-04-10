import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.StrictMath.abs;

class Utils {
    private static int EMPTY = 0;
    private static int ISLAND = 1;

    /**
     * For count distance with 2 cells with cardinal move
     * @param c
     * @param other
     * @return int
     */
    public int distanceFromCell(Cell c, Cell other){
        return abs(c.getX() - other.getX()) + abs(c.getY() - other.getY());
    }

    public Cell randomCellOnList (List<Cell> inputList) {
        Random rand = new Random();

        // limit for random 4 -> direction (North, South, West, Est).
        int max = inputList.size() - 1;
        int min = 0;
        int randomCellInt = rand.nextInt(max - min + 1) + min;
        Cell randomCell = inputList.get(randomCellInt);
        return randomCell;
    }

    public List<Sector> makeSectors(int xMin, int yMin) {
        int xMinIncrement = xMin;
        int yMinIncrement = yMin;
        int sectorCount = 1;

        List<Sector> sectorList = new ArrayList<>();
        for (int j = 1; j <= 3 ; j++) {
            for (int i = 1; i <= 3; i++) {
                Sector sector = fillSector(xMinIncrement, yMinIncrement);
                sector.setId(sectorCount);
                sectorList.add(sector);
                // boucle for x
                xMinIncrement = xMinIncrement + 5;
                sectorCount++;
            }
            xMinIncrement = xMin;
            // bouche for y
            yMinIncrement= yMinIncrement + 5;
        }

        // check
//        System.err.println("print sector 4 " + sectorList.get(3).toString());
        return sectorList;
    }

    public List<Cell> createSafeCellListAroundMe(Submarine mySubmarine, Board board) {
        List<Cell> listSafeCell = new ArrayList<>();
        int x = mySubmarine.getPositionX();
        int y = mySubmarine.getPositionY();
        // record My position
        Cell myPositionCell = new Cell(x,y,null, null);

        listSafeCell.add(myPositionCell);
        // record cardinal position cell
        Cell northCell = new Cell(x, y -1, null, null);
        listSafeCell.add(northCell);
        Cell southCell = new Cell(x, y +1, null, null);
        listSafeCell.add(southCell);
        Cell westCell = new Cell(x - 1, y, null, null);
        listSafeCell.add(westCell);
        Cell estCell = new Cell(x + 1, y, null, null);
        listSafeCell.add(estCell);

        // record diagonal position
        Cell northEstCell = new Cell(x + 1, y - 1, null, null);
        listSafeCell.add(northEstCell);
        Cell northWestCell = new Cell(x - 1, y - 1, null, null);
        listSafeCell.add(northWestCell);
        Cell southEstCell = new Cell(x + 1, y + 1, null, null);
        listSafeCell.add(southEstCell);
        Cell southWestCell = new Cell(x - 1, y + 1, null, null);
        listSafeCell.add(southWestCell);

        // remove cell out map
        return removeEndOfMap(listSafeCell, board);

    }

    public List<Cell> removeEndOfMap(List<Cell> list, Board board) {
        ArrayList<Cell> listWithoutMapLimit = new ArrayList<Cell>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getX() >= 0 && list.get(i).getX() < board.getNbrCellX() &&
                    list.get(i).getY() >= 0 && list.get(i).getY() < board.getNbrCellY()) {
                listWithoutMapLimit.add(list.get(i));
            }
        }
        // check
//        System.err.println("list of possible move without map limit: " + listWithoutMapLimit.toString());
        return listWithoutMapLimit;
    }

    public List<Cell> removeEarthCellTable(List<Cell> inputList, Board board) {
        ArrayList<Cell> listWithoutEarthCell = new ArrayList<Cell>();
        int map[][] = board.getMap();

        for (int i = 0; i < inputList.size(); i++) {
            if(map[inputList.get(i).getX()][inputList.get(i).getY()] == EMPTY) {
                listWithoutEarthCell.add(inputList.get(i));
            }
        }
        return listWithoutEarthCell;
    }

    public List<Cell> removeAlreadyVisitedCell(List<Cell> inputList, Board board) {
        ArrayList<Cell> listWithoutVisitedCell = new ArrayList<Cell>();
        ArrayList<Cell> listVisitedCell = (ArrayList<Cell>) board.getListCellAlreadyVisited();

        if (listVisitedCell != null) {
            for (int i = 0; i < inputList.size(); i++) {
                boolean cellOk = true;
                for (int j = 0; j < listVisitedCell.size(); j++) {
                    if (listVisitedCell.get(j).getX() == inputList.get(i).getX() &&
                            listVisitedCell.get(j).getY() == inputList.get(i).getY()) {
                        cellOk = false;
                    }
                }
                if (cellOk) {
                    listWithoutVisitedCell.add(inputList.get(i));
                }
            }
        } else {
            // add input cell
            for (int i = 0; i < inputList.size(); i++) {
                listWithoutVisitedCell.add(inputList.get(i));
            }
        }
        return listWithoutVisitedCell;
    }

    public Sector fillSector(int xMin, int yMin) {
        Sector sector = new Sector();
        // set minCell of sector
        Cell minCell = new Cell();
        minCell.setX(xMin);
        minCell.setY(yMin);
        sector.setMinCell(minCell);

        // create list of cell
        List<Cell> listCellSector = new ArrayList<>();

        for (int i =0; i < 5; i++) {
            for(int j = 0; j < 5 ;j++) {
                Cell cell = new Cell(xMin + j, yMin + i, null, null);
                listCellSector.add(cell);
                if (i == 4 && j == 4) {
                    sector.setMaxCell(cell);
                }
            }
        }
        sector.setListCell(listCellSector);
        // check
//        System.err.println("one sector " + listCellSector.toString());
        return sector;
    }

    public Cell findCellWithCardinalPoint(String cardinalPoint, int x, int y) {
        Cell cell = new Cell();
        if (cardinalPoint.equals("S")) {
            cell.setX(x);
            cell.setY(y + 1);
        }
        if (cardinalPoint.equals("N")) {
            cell.setX(x);
            cell.setY(y - 1);
        }
        if (cardinalPoint.equals("W")) {
            cell.setX(x - 1);
            cell.setY(y);
        }
        if (cardinalPoint.equals("E")) {
            cell.setX(x + 1);
            cell.setY(y);
        }
        // check
//        System.err.println("passage recenter cell: ");
        return cell;
    }

    public int findMyPositionSector(Submarine mySubmarine, Board board) {
        List<Sector> listSectors = board.getListSecteurs();
        Cell myPositionCell = new Cell(mySubmarine.getPositionX(), mySubmarine.getPositionY(), null, null);
        int mySector = 0;

        for (Sector sector: listSectors) {
            for (Cell cell: sector.getListCell()) {
                if (cell.getX() == myPositionCell.getX() && cell.getY() == myPositionCell.getY()) {
                    mySector = sector.getId();
                }
            }
        }
        // check
//        System.err.println("my sector: " + mySector);
        return mySector;
    }

    /**
     * for compare of my life
     * @param mySubmarine
     * @return
     */
    public int compareLifeLoopBefore(Submarine mySubmarine) {
        int life = mySubmarine.getLife();
        int lifeloopBefore = mySubmarine.getLifeLoopBefore();

        if ((lifeloopBefore - life) == 2) {
            return 2;
        }
        else if ((lifeloopBefore - life) == 1) {
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * compare opponent life
     * @param opponentSubmarine
     * @return
     */
    public int compareLifeOfOpponent(Submarine opponentSubmarine) {
        int oppLife = opponentSubmarine.getLife();
        int lifeLoopBefore = opponentSubmarine.getLifeLoopBefore();

        if ((lifeLoopBefore - oppLife) == 2) {
            return 2;
        }
        else if ((lifeLoopBefore - oppLife) == 1) {
            return 1;
        }
        else {
            return 0;
        }

    }

    /**
     * For remove cell if there is present on fullList
     * @param fullList
     * @param cellListToRemoveIfPresent
     * @return full list without cell on second list(if present)
     */
    public List<Cell> removeCellsOnList(List<Cell> fullList, List<Cell> cellListToRemoveIfPresent) {
        List<Cell> resultList = new ArrayList<>();
        for (int i = 0; i < fullList.size(); i++) {
            resultList.add(fullList.get(i));
        }

        for (int i = 0; i < fullList.size() ; i++) {
            for (int j = 0; j < cellListToRemoveIfPresent.size(); j++) {
                if (fullList.get(i).getX() == cellListToRemoveIfPresent.get(j).getX() &&
                        fullList.get(i).getY() == cellListToRemoveIfPresent.get(j).getY()) {
                    resultList.remove(fullList.get(i));
                }
            }
        }
        return resultList;
    }

    public List<Cell> findSectorCellWithPosition(Submarine mySubmarine, Board board) {
        List<Cell> result = new ArrayList<>();
        for (int i = 0; i < board.getListSecteurs().size(); i++) {
            if(board.getListSecteurs().get(i).getId() == mySubmarine.getMySector()) {
                result = board.getListSecteurs().get(i).getListCell();
            }
        }
        return result;
    }

    public boolean isOnTheMap(Cell cell) {
        if(cell.getX() < 0 || cell.getX() >= 15 || cell.getY() < 0 || cell.getY() >= 15) {
            return false;
        }
//        System.err.println("Cell " + cell.toString() + "is on the map");
        return true;
    }

    public boolean isSafeToMove(Cell cell, Board board) {
        int map[][] = board.getMap();
        if (isOnTheMap(cell) && map[cell.getX()][cell.getY()] == EMPTY) {
//            System.err.println("Cell " + cell.toString() + "is safe");
            return true;
        } else {
            return false;
        }
    }

    public List<Cell> cellsAroundOnecell(Cell cellIn, Board board) {
        int map[][] = board.getMap();
        List<Cell> listCell = new ArrayList<>();

        //get cell around cellIn
        Cell northCell = new Cell(cellIn.getX(), cellIn.getY() - 1, null, null);
        Cell northEstCell = new Cell(cellIn.getX() + 1, cellIn.getY() - 1, null, null);
        Cell estCell = new Cell(cellIn.getX() + 1, cellIn.getY(), null, null);
        Cell estSouthCell = new Cell(cellIn.getX() + 1, cellIn.getY() + 1, null, null);
        Cell southCell = new Cell(cellIn.getX(), cellIn.getY() + 1, null, null);
        Cell westSouthCell = new Cell(cellIn.getX() -1, cellIn.getY() + 1, null, null);
        Cell WestCell = new Cell(cellIn.getX() -1, cellIn.getY(), null, null);
        Cell northWestCell = new Cell(cellIn.getX() - 1, cellIn.getY() - 1, null, null);

        // check if out of map  and earth and add on list
        if (isOnTheMap(northCell)) {
            if (map[northCell.getX()][northCell.getY()] == EMPTY) {listCell.add(northCell);}
        }
        if (isOnTheMap(northEstCell)) {
            if (map[northEstCell.getX()][northEstCell.getY()] == EMPTY) {listCell.add(northEstCell);}
        }
        if (isOnTheMap(estCell)) {
            if (map[estCell.getX()][estCell.getY()] == EMPTY) {listCell.add(estCell);}

        }
        if (isOnTheMap(estSouthCell)) {
            if (map[estSouthCell.getX()][estSouthCell.getY()] == EMPTY) {listCell.add(estSouthCell);}

        }
        if (isOnTheMap(southCell)) {
            if (map[southCell.getX()][southCell.getY()] == EMPTY) {listCell.add(southCell);}
        }
        if (isOnTheMap(westSouthCell)) {
            if (map[westSouthCell.getX()][westSouthCell.getY()] == EMPTY) {listCell.add(westSouthCell);}
        }
        if (isOnTheMap(WestCell)) {
            if (map[WestCell.getX()][WestCell.getY()] == EMPTY) {listCell.add(WestCell);}

        }
        if (isOnTheMap(northWestCell)) {
            if (map[northWestCell.getX()][northWestCell.getY()] == EMPTY) {listCell.add(northWestCell);}
        }
        // + cellIn
        listCell.add(cellIn);

        //check
//        System.err.println("list cell around this cell: " + listCell.toString());
        return listCell;
    }
}