import java.util.*;

import static java.lang.StrictMath.abs;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Deploy deploy = new Deploy();
        Move move = new Move();
        Submarine mySubmarine = new Submarine();
        Submarine opponentSubmarine = new Submarine();
        Board board = new Board();
        Utils utils = new Utils();
        Torpedo torpedo = new Torpedo();
        List<Cell> listCellAlreadyVisited = new ArrayList<>();

        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        board.setNbrCellX(width);
        // check
        System.err.println("limit board on X= " + board.getNbrCellX());
        int height = in.nextInt();
        board.setNbrCellY(height);
        // check
        System.err.println("limit board on Y= " + board.getNbrCellY());
        int myId = in.nextInt();
        mySubmarine.setId(myId);
        if (in.hasNextLine()) {
            in.nextLine();
        }

        // create map
        HashMap<Integer, String> earthMap = new HashMap<Integer, String>();
        for (int i = 0; i < height; i++) {
            String line = in.nextLine();
            earthMap.put(i, line);        
        }
        // lunch displayEarthAnalysis method
        deploy.displayEarthAnalysis(earthMap);
        // record earthCell on board
        deploy.recordEarthCellOnBoard(earthMap, board);

        // ****** 1 **** Deployement:
        // map analyse for deploy
        String deployPositionString = deploy.deploy(earthMap);

        // order of deploiement
        System.out.println(deployPositionString);

        // create sectors
        List<Sector> sectorList = utils.makeSectors(0,0);
        board.setListSecteurs(sectorList);

        // ********* 2 **** Game loop:
        while (true) {
            int x = in.nextInt();
            mySubmarine.setPositionX(x);
            int y = in.nextInt();
            mySubmarine.setPositionY(y);
            Cell myMoveCell= new Cell(x,y,null);
            int myLife = in.nextInt();
            mySubmarine.setLife(myLife);
            int oppLife = in.nextInt();
            opponentSubmarine.setLife(oppLife);
            int torpedoCooldown = in.nextInt();
            mySubmarine.setTorpedoCooldown(torpedoCooldown);
            int sonarCooldown = in.nextInt();
            mySubmarine.setSonarCooldown(sonarCooldown);
            int silenceCooldown = in.nextInt();
            mySubmarine.setSilenceCooldown(silenceCooldown);
            int mineCooldown = in.nextInt();
            mySubmarine.setMineCooldown(mineCooldown);
            String sonarResult = in.next();
            mySubmarine.setSonarResult(sonarResult);
            if (in.hasNextLine()) {
                in.nextLine();
            }
            String opponentOrders = in.nextLine();
            mySubmarine.setOpponentOrders(opponentOrders);

            // create safe list without map end(around me in case of fire torpedo!)
            List<Cell> safeListAroundMe = utils.createSafeCellListAroundMe(mySubmarine.getPositionX(), mySubmarine.getPositionY(), board);
            mySubmarine.setSafeListOfCellAroundMe(safeListAroundMe);

            // ********** 3   **** Action:
            // think for next move
            String nextMove = move.moveIA1(mySubmarine, earthMap, board);

            // if torpedo is load -> create list torperdo range (without map limit and earth)
            if (mySubmarine.getTorpedoCooldown() == 0) {
                List<Cell> torpedorange = torpedo.createCellListTorpedoRange(mySubmarine.getPositionX(), mySubmarine.getPositionY(), board);
                mySubmarine.setListTorpedoRange(torpedorange);
                // check
//                System.err.println("Torpedo load and list range ok");
            }

            // if mySubmarin make SURFACE persist last mySubmarine position on object board
            if (nextMove == "SURFACE") {listCellAlreadyVisited = new ArrayList<>(); }
            listCellAlreadyVisited.add(myMoveCell);
            board.setListCellAlreadyVisited(listCellAlreadyVisited);

            // order for move
            System.out.println(nextMove);

            // print submarines info
            System.err.println("My submarine: " + mySubmarine.toString());
//            System.err.println("Opponent submarine: " + opponentSubmarine.toString());
        }
    }


}

/**
 * to deploy in real water!
 */
class Deploy {
    /**
     * test for analyse map
     */
    public void displayEarthAnalysis(HashMap<Integer, String> earthMap) {
        for (int i = 0; i < 15; i++) {
            String value = earthMap.get(i);
            System.err.println(value);
        }
    }

    /**
     * Deploy corner bottom left or bottom Middle
     */
    public String deploy(HashMap<Integer, String> earthMap) {
        // 1 deploy corner bottom left or middle bottom if there is earth
        String line14 = earthMap.get(14);
        char deployPoint = line14.charAt(0);
        if (deployPoint == '.') { return "0 14"; }
        else { return "7 14"; }
    }

    public Cell recordDeploiementCell(String deploy) {
        Cell deployCell = new Cell();
        String space = " ";
        String positions[] = deploy.split(space);
        String posXstring = positions[0];
        String posYstring =  positions[1];
        deployCell.setX(Integer.parseInt(posXstring));
        deployCell.setY(Integer.parseInt(posYstring));
        return deployCell;
    }

    public void recordEarthCellOnBoard(HashMap<Integer, String> earthMap, Board board) {
        List<Cell> listCellContainsEarth = new ArrayList<>();
        char oneChar;
        for (int i = 0; i < earthMap.size(); i++) {
            String line = earthMap.get(i);
            for (int j = 0; j < line.length(); j++) {
                oneChar = line.charAt(j);
                if (oneChar == 'x') {
                    Cell earthCell = new Cell(j,i, null);
                    // add earthCell on list of earth on board object
                    listCellContainsEarth.add(earthCell);
                    board.setListCellEarth(listCellContainsEarth);
                }
            }
        }
         // check
         System.err.println("list earth on board " + listCellContainsEarth.toString());
    }
}

/**
 * For move without crash
 */
class Move {
    Utils utils = new Utils();

    /**
     * First moving IA random move
     */
    public String moveIA1(Submarine mySubmarine, HashMap<Integer, String> earthMap, Board board) {
        // list of future move (4 cell auround mySubmarine position)
        List<Cell> listAllPossibleMove = new ArrayList<>();

        // create list of possible move cell
        Cell northCell = new Cell(mySubmarine.getPositionX(), mySubmarine.getPositionY() - 1, "N ");
        listAllPossibleMove.add(northCell);
        Cell southCell = new Cell(mySubmarine.getPositionX(), mySubmarine.getPositionY() + 1, "S ");
        listAllPossibleMove.add(southCell);
        Cell westCell = new Cell(mySubmarine.getPositionX() - 1, mySubmarine.getPositionY(), "W ");
        listAllPossibleMove.add(westCell);
        Cell estCell = new Cell(mySubmarine.getPositionX() + 1, mySubmarine.getPositionY(), "E ");
        listAllPossibleMove.add(estCell);

        // remove cells out of map
        List<Cell> listWithoutMapLimit = utils.removeEndOfMap(listAllPossibleMove, board);

        // remove earth on last list
        List<Cell> listWithoutMapLimitAndEarth = utils.removeEarthCell(listWithoutMapLimit, board);

        // remove alreadyVisit cell on last list
        List<Cell> listWithoutMapLimitEarthAndAlreadyVisited = utils.removeAlreadyVisitedCell(listWithoutMapLimitAndEarth, board);

        // random on cell with last list
        if (!listWithoutMapLimitEarthAndAlreadyVisited.isEmpty()) {
            Cell cellToMoveRandom = utils.randomCellOnList(listWithoutMapLimitEarthAndAlreadyVisited);

            // move to random cell
            if (cellToMoveRandom.getX() != -10) {
                if (cellToMoveRandom.getCardinalPoint() == "N ") { return "MOVE " + cellToMoveRandom.getCardinalPoint() + "TORPEDO"; }
                if (cellToMoveRandom.getCardinalPoint() == "E ") { return "MOVE " + cellToMoveRandom.getCardinalPoint() + "TORPEDO"; }
                if (cellToMoveRandom.getCardinalPoint() == "W ") { return "MOVE " + cellToMoveRandom.getCardinalPoint() + "TORPEDO"; }
                if (cellToMoveRandom.getCardinalPoint() == "S ") { return "MOVE " + cellToMoveRandom.getCardinalPoint() + "TORPEDO"; }
            }
        }
        return "SURFACE";
    }

}

class LocateOpponent {

}

class Torpedo {
    Utils utils = new Utils();

    public List<Cell> createCellListTorpedoRange(int x, int y, Board board) {
        List<Cell> listCellTorpedoRange = new ArrayList<>();
        int rangeMinX = -4;
        int rangeMaxX = 4;
        int centerCellY = y;
        int countChangeSide = 0;
        //down triangle
        for (int j = 1; j <= 10; j++) {
            for (int i = rangeMinX; i <= rangeMaxX ; i++) {
                // create centerCell
                Cell leftEndCell = new Cell(x + i, y, null);
                listCellTorpedoRange.add(leftEndCell);
                countChangeSide++;
            }
            if (countChangeSide < 7) {
                rangeMaxX--;
                rangeMinX++;
                y++;
            }
            if (countChangeSide >= 7) {
                rangeMaxX--;
                rangeMinX++;
                y--;
            }
        }

        // remove end of map
        List<Cell> listWithoutEndOfMap = utils.removeEndOfMap(listCellTorpedoRange, board);
        // remove earth on last list
        List<Cell> listWithoutEndOfMapAndEarth = utils.removeEarthCell(listWithoutEndOfMap, board);
        // check
//        System.err.println("torpedo range list " + listWithoutEndOfMapAndEarth.toString());
        return listWithoutEndOfMapAndEarth;
    }

}

class Utils {
    /**
     * For count distance with 2 cells with cardinal move
     * @param c
     * @param other
     * @return int
     */
    public static int distanceFromCell(Cell c, Cell other){
        return abs(c.getX() - other.getX()) + abs(c.getY() - other.getY());
    }

    public Cell randomCellOnList (List<Cell> inputList) {
        Random rand = new Random();

        // limit for random 4 -> direction (North, South, West, Est)
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

    public List<Cell> createSafeCellListAroundMe(int x, int y, Board board) {
        List<Cell> listSafeCell = new ArrayList<>();
        // record My position
        Cell myPositionCell = new Cell(x,y,null);

        listSafeCell.add(myPositionCell);
        // record cardinal position cell
        Cell northCell = new Cell(x, y -1, null);
        listSafeCell.add(northCell);
        Cell southCell = new Cell(x, y +1, null);
        listSafeCell.add(southCell);
        Cell westCell = new Cell(x - 1, y, null);
        listSafeCell.add(westCell);
        Cell estCell = new Cell(x + 1, y, null);
        listSafeCell.add(estCell);

        // record diagonal position
        Cell northEstCell = new Cell(x + 1, y - 1, null);
        listSafeCell.add(northEstCell);
        Cell northWestCell = new Cell(x - 1, y - 1, null);
        listSafeCell.add(northWestCell);
        Cell southEstCell = new Cell(x + 1, y + 1, null);
        listSafeCell.add(southEstCell);
        Cell southWestCell = new Cell(x - 1, y + 1, null);
        listSafeCell.add(southWestCell);

        // remove cell out map
        List<Cell> listWithoutEndOfMap = removeEndOfMap(listSafeCell, board);

        // check
//        System.err.println("safe list: " + listWithoutEndOfMap.toString());
        return listWithoutEndOfMap;
    }



//    public List<Cell> removeOutLimitMap(List<Cell> listForRemoveEndOfMap) {
//        // remove cell out map
//        for (int i = 0; i < listForRemoveEndOfMap.size(); i++) {
//            if (listForRemoveEndOfMap.get(i).getX() == -1 ) {listForRemoveEndOfMap.remove(i); }
//        }
//        for (int i = 0; i < listForRemoveEndOfMap.size(); i++) {
//            if (listForRemoveEndOfMap.get(i).getX() == 15 ) {listForRemoveEndOfMap.remove(i); }
//        }
//        for (int i = 0; i < listForRemoveEndOfMap.size(); i++) {
//            if (listForRemoveEndOfMap.get(i).getY() == -1 ) {listForRemoveEndOfMap.remove(i); }
//        }
//        for (int i = 0; i < listForRemoveEndOfMap.size(); i++) {
//            if (listForRemoveEndOfMap.get(i).getY() == 15 ) {listForRemoveEndOfMap.remove(i); }
//        }
//        return  listForRemoveEndOfMap;
//    }

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

    public List<Cell> removeEarthCell(List<Cell> inputList, Board board) {
        ArrayList<Cell> listWithoutEarthCell = new ArrayList<Cell>();
        ArrayList<Cell> listEarthCell = (ArrayList<Cell>) board.getListCellEarth();
        for (int i = 0; i < inputList.size(); i++) {
            boolean cellOk = true;
            for (int j = 0; j < listEarthCell.size(); j++) {
                if (listEarthCell.get(j).getX() == inputList.get(i).getX() &&
                        listEarthCell.get(j).getY() == inputList.get(i).getY()) {
                    cellOk = false;
                }
            }
            if (cellOk) { listWithoutEarthCell.add(inputList.get(i)); }
        }
        // check
//        System.err.println("list of possible move without map limit and earth: " + listWithoutEarthCell.toString());
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
                Cell cell = new Cell(xMin + j, yMin + i, null);
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
}

// Objects

class Submarine {
    private int id;
    private int positionX;
    private int positionY;
    private int life;
    private int torpedoCooldown;
    private int sonarCooldown;
    private int silenceCooldown;
    private int mineCooldown;
    private String sonarResult;
    private String opponentOrders;
    private List<Cell> safeListOfCellAroundMe;
    private List<Cell> listTorpedoRange;


    // constructor
    public Submarine() {
    }

    public Submarine(int id, int positionX, int positionY, int life, int torpedoCooldown, int sonarCooldown, int silenceCooldown, int mineCooldown, String sonarResult, String opponentOrders, List<Cell> safeListOfCellAroundMe, List<Cell> listTorpedoRange) {
        this.id = id;
        this.positionX = positionX;
        this.positionY = positionY;
        this.life = life;
        this.torpedoCooldown = torpedoCooldown;
        this.sonarCooldown = sonarCooldown;
        this.silenceCooldown = silenceCooldown;
        this.mineCooldown = mineCooldown;
        this.sonarResult = sonarResult;
        this.opponentOrders = opponentOrders;
        this.safeListOfCellAroundMe = safeListOfCellAroundMe;
        this.listTorpedoRange = listTorpedoRange;
    }

    // getters setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPositionX() {
        return positionX;
    }
    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }
    public int getPositionY() {
        return positionY;
    }
    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }
    public int getLife() {
        return life;
    }
    public void setLife(int life) {
        this.life = life;
    }
    public int getTorpedoCooldown() {
        return torpedoCooldown;
    }
    public void setTorpedoCooldown(int torpedoCooldown) {
        this.torpedoCooldown = torpedoCooldown;
    }
    public int getSonarCooldown() {
        return sonarCooldown;
    }
    public void setSonarCooldown(int sonarCooldown) {
        this.sonarCooldown = sonarCooldown;
    }
    public int getSilenceCooldown() {
        return silenceCooldown;
    }
    public void setSilenceCooldown(int silenceCooldown) {
        this.silenceCooldown = silenceCooldown;
    }
    public int getMineCooldown() {
        return mineCooldown;
    }
    public void setMineCooldown(int mineCooldown) {
        this.mineCooldown = mineCooldown;
    }
    public String getSonarResult() {
        return sonarResult;
    }
    public void setSonarResult(String sonarResult) {
        this.sonarResult = sonarResult;
    }
    public String getOpponentOrders() {
        return opponentOrders;
    }
    public void setOpponentOrders(String opponentOrders) {
        this.opponentOrders = opponentOrders;
    }
    public List<Cell> getSafeListOfCellAroundMe() {
        return safeListOfCellAroundMe;
    }
    public void setSafeListOfCellAroundMe(List<Cell> safeListOfCellAroundMe) {
        this.safeListOfCellAroundMe = safeListOfCellAroundMe;
    }
    public List<Cell> getListTorpedoRange() {
        return listTorpedoRange;
    }
    public void setListTorpedoRange(List<Cell> listTorpedoRange) {
        this.listTorpedoRange = listTorpedoRange;
    }

    // to string
    @Override
    public String toString() {
        return "Submarine{" +
                "id=" + id +
                ", positionX=" + positionX +
                ", positionY=" + positionY +
                ", life=" + life +
                ", torpedoCooldown=" + torpedoCooldown +
                ", sonarCooldown=" + sonarCooldown +
                ", silenceCooldown=" + silenceCooldown +
                ", mineCooldown=" + mineCooldown +
                ", sonarResult='" + sonarResult + '\'' +
                ", opponentOrders='" + opponentOrders + '\'' +
                ", safeListOfCellAroundMe=" + safeListOfCellAroundMe +
                ", listTorpedoRange=" + listTorpedoRange +
                '}';
    }
}

class Cell {
    private int x;
    private int y;
    private String cardinalPoint;

    // constructor
    public Cell() {
    }

    public Cell(int x, int y, String cardinalPoint) {
        this.x = x;
        this.y = y;
        this.cardinalPoint = cardinalPoint;
    }

    // getters setters
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public String getCardinalPoint() {
        return cardinalPoint;
    }
    public void setCardinalPoint(String cardinalPoint) {
        this.cardinalPoint = cardinalPoint;
    }

    // to string
    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                ", cardinalPoint='" + cardinalPoint + '\'' +
                '}';
    }
}

class Board {
    private int nbrCellX;
    private int nbrCellY;
    private List<Cell> listCellEarth;
    private List<Cell> listCellAlreadyVisited;
    private List<Sector> listSecteurs;

    // constructor
    public Board() {
    }

    public Board(int nbrCellX, int nbrCellY, List<Cell> listCellEarth, List<Cell> listCellAlreadyVisited, List<Sector> listSecteurs) {
        this.nbrCellX = nbrCellX;
        this.nbrCellY = nbrCellY;
        this.listCellEarth = listCellEarth;
        this.listCellAlreadyVisited = listCellAlreadyVisited;
        this.listSecteurs = listSecteurs;
    }

    // getters setters
    public int getNbrCellX() {
        return nbrCellX;
    }
    public void setNbrCellX(int nbrCellX) {
        this.nbrCellX = nbrCellX;
    }
    public int getNbrCellY() {
        return nbrCellY;
    }
    public void setNbrCellY(int nbrCellY) {
        this.nbrCellY = nbrCellY;
    }
    public List<Cell> getListCellEarth() {
        return listCellEarth;
    }
    public void setListCellEarth(List<Cell> listCellEarth) {
        this.listCellEarth = listCellEarth;
    }
    public List<Cell> getListCellAlreadyVisited() {
        return listCellAlreadyVisited;
    }
    public void setListCellAlreadyVisited(List<Cell> listCellAlreadyVisited) {
        this.listCellAlreadyVisited = listCellAlreadyVisited;
    }
    public List<Sector> getListSecteurs() {
        return listSecteurs;
    }
    public void setListSecteurs(List<Sector> listSecteurs) {
        this.listSecteurs = listSecteurs;
    }

    // to string


    @Override
    public String toString() {
        return "Board{" +
                "nbrCellX=" + nbrCellX +
                ", nbrCellY=" + nbrCellY +
                ", listCellEarth=" + listCellEarth +
                ", listCellAlreadyVisited=" + listCellAlreadyVisited +
                ", listSecteurs=" + listSecteurs +
                '}';
    }
}

class Sector {

    private int id;
    private List<Cell> listCell;
    private Cell minCell;
    private Cell maxCell;

    // constructeur
    public Sector() {
    }

    public Sector(int id, List<Cell> listCell, Cell minCell, Cell maxCell) {
        this.id = id;
        this.listCell = listCell;
        this.minCell = minCell;
        this.maxCell = maxCell;
    }

    // getters ad setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public List<Cell> getListCell() {
        return listCell;
    }
    public void setListCell(List<Cell> listCell) {
        this.listCell = listCell;
    }
    public Cell getMinCell() {
        return minCell;
    }
    public void setMinCell(Cell minCell) {
        this.minCell = minCell;
    }
    public Cell getMaxCell() {
        return maxCell;
    }
    public void setMaxCell(Cell maxCell) {
        this.maxCell = maxCell;
    }

    // to string
    @Override
    public String toString() {
        return "Sector{" +
                "id=" + id +
                ", listCell=" + listCell +
                ", minCell=" + minCell +
                ", maxCell=" + maxCell +
                '}';
    }
}



