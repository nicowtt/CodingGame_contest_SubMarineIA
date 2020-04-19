import java.util.ArrayList;
import java.util.List;
        // if important damage try to move silence (for now only one move on silence)
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.util.*;
import java.util.List;
import sun.util.resources.ms.CalendarData_ms_MY;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static java.lang.StrictMath.abs;

class Action {
    private Utils utils = new Utils();
    private Torpedo torpedo = new Torpedo();
    private List<Cell> listCellAlreadyVisited = new ArrayList<>();


    public void whenMySubmarineToSurface(Submarine mySubmarine, Board board) {
        Cell myMoveCell = new Cell(mySubmarine.getPositionX(), mySubmarine.getPositionY(), null, null);
        // if mySubmarine make SURFACE persist last mySubmarine position on object board
        if (mySubmarine.getNextMoveString() == "SURFACE") {
            listCellAlreadyVisited = new ArrayList<>();
        }
        listCellAlreadyVisited.add(myMoveCell);
        board.setListCellAlreadyVisited(listCellAlreadyVisited);
    }

    public void forChargingSubmarineTools(Submarine mySubmarine ) {
        String chargeSonar = "SONAR";
        String chargeTorpedo = "TORPEDO";
        String chargeSilence = "SILENCE";
        String sentSonar = "SONAR ";

        // if my submarine move -> Charge torpedo first and sonar after and silence after
        if (mySubmarine.getNextMoveString() != "SURFACE" && !mySubmarine.getLoadedTorpedo() && !mySubmarine.getMoveNextOnSilence()) {
            String nextMove = mySubmarine.getNextMoveString() + chargeTorpedo;
            mySubmarine.setNextMoveString(nextMove);
        }
        if (mySubmarine.getNextMoveString() != "SURFACE" && mySubmarine.getLoadedTorpedo() && !mySubmarine.getLoadedSonar() &&
                !mySubmarine.getMoveNextOnSilence()) {
            String nextMove = mySubmarine.getNextMoveString() + chargeSonar;
            mySubmarine.setNextMoveString(nextMove);
        }
        if (mySubmarine.getNextMoveString() != "SURFACE" && mySubmarine.getLoadedTorpedo() && mySubmarine.getLoadedSonar() &&
                mySubmarine.getLoadedSilence() && mySubmarine.getCanISendSonar() && !mySubmarine.getMoveNextOnSilence()) {
            //check
            System.err.println("passage sonar order");
            // lunch sonar
            String nextMove = sentSonar + String.valueOf(mySubmarine.getMySector()) + "|" + mySubmarine.getNextMoveString();
            mySubmarine.setNextMoveString(nextMove);
            mySubmarine.setLoadedSonar(false);
        }
        if (mySubmarine.getNextMoveString() != "SURFACE" && mySubmarine.getLoadedTorpedo() && mySubmarine.getLoadedSonar() &&
                !mySubmarine.getMoveNextOnSilence()) {
            String nextMove = mySubmarine.getNextMoveString() + chargeSilence;
            mySubmarine.setNextMoveString(nextMove);
        }
    }

    public void whenMySubmarineIs2PointsDamaged(Submarine mySubmarine) {
        String silence = "SILENCE ";
        if (mySubmarine.getNextMoveString() != "SURFACE" && mySubmarine.getMoveNextOnSilence() && mySubmarine.getLoadedSilence()) {
            //check
            System.err.println("next move on silence!");
            mySubmarine.setMoveNextOnSilence(false);
            mySubmarine.setLoadedSilence(false);
            mySubmarine.setNextMoveString(silence + mySubmarine.getMyNextMove().getCardinalPoint() + 1);

        }
        // check
        System.err.println("Fire Following Torpedo Feedback: " + mySubmarine.getCanIfireFollowingTorpedoFeedback());
        System.err.println("Fire Following Sonar Feedback: " + mySubmarine.getCanIfireFollowingSonarFeedback());
        System.err.println("Opponent presence on my sector: " + mySubmarine.getOppPresenceOnMySector());
        System.err.println("--Torpedo loaded: " + mySubmarine.getLoadedTorpedo() + " --");
    }

    public void fireChoiceWhenTorpediFeedbackAndSonarFeedback(Submarine mySubmarine) {
        if (mySubmarine.getCanIfireFollowingTorpedoFeedback() && mySubmarine.getCanIfireFollowingSonarFeedback()) {
            mySubmarine.setCanIfireFollowingSonarFeedback(false);
        }
    }

    public void fireWhenOppSendTorpedo(Submarine mySubmarine) {
        String addfireTorpedoString;

        // add fire on move order following opponent fire
        if (mySubmarine.getCanIfireFollowingTorpedoFeedback() && !mySubmarine.getCanIfireFollowingSonarFeedback() &&
                !mySubmarine.getMoveNextOnSilence()) {
            System.err.println("passage fire following opp torpedo");
            // get random cell on fireList
            Cell randomfireTorpedo = utils.randomCellOnList(mySubmarine.getTorpedoFireList());
            mySubmarine.setMyFireTorpedoCell(randomfireTorpedo);
            // if opp is lock
            if (mySubmarine.getOpponentCell() != null) {
                //check
                System.err.println("fire on opp cell!");
                addfireTorpedoString = "TORPEDO " + mySubmarine.getOpponentCell().getX()
                        + " " + mySubmarine.getOpponentCell().getY();
            } else {
                addfireTorpedoString = "TORPEDO " + randomfireTorpedo.getX() + " " + randomfireTorpedo.getY();
            }
            String nextMoveFire = addfireTorpedoString + "|" + mySubmarine.getNextMoveString();
            // order of move and fire
            mySubmarine.setLoadedTorpedo(false);
            //fire
            mySubmarine.setiFireOnPrecedentLoop(true);
            System.err.println("------- FIRE  -----------");
            System.out.println(nextMoveFire);
        }
    }

    public void fireWhenSonarFeedbackOnMySector(Submarine mySubmarine, Submarine opponentSubmarine, Board board) {
        String addfireTorpedoString;

        // add fire on move order following sonar or opponent presence on my sector
        if (mySubmarine.getCanIfireFollowingSonarFeedback() && !mySubmarine.getCanIfireFollowingTorpedoFeedback() &&
                !mySubmarine.getMoveNextOnSilence() ||
                mySubmarine.getOppPresenceOnMySector() && mySubmarine.getLoadedTorpedo()) {
            System.err.println("passage fire following opp on my sector (sonar or opp SURFACE)");
            // get random cell on mySector
            Cell randomfireTorpedo = torpedo.createPossibilitiCellOnSector(mySubmarine, board);
            if (randomfireTorpedo.getX() != -1) {
                mySubmarine.setMyFireTorpedoCell(randomfireTorpedo);
                // if opp is lock
                if (mySubmarine.getOpponentCell() != null) {
                    addfireTorpedoString = "TORPEDO " + mySubmarine.getOpponentCell().getX() + " " + mySubmarine.getOpponentCell().getY();
                } else {
                    addfireTorpedoString = "TORPEDO " + randomfireTorpedo.getX() + " " + randomfireTorpedo.getY();
                }
                //check
                System.err.println("addFireTorpedoString after sonar: " + addfireTorpedoString);
                String nextMoveFire = addfireTorpedoString + "|" + mySubmarine.getNextMoveString();

                // update my lifeLoopBefore and opponent
                mySubmarine.setLifeLoopBefore(mySubmarine.getLife());
                opponentSubmarine.setLifeLoopBefore(opponentSubmarine.getLife());
                // order of move and fire
                mySubmarine.setLoadedTorpedo(false);
                // fire
                mySubmarine.setiFireOnPrecedentLoop(true);
                System.err.println("------- FIRE  -----------");
                System.out.println(nextMoveFire);
            }
        }
    }

    public void justMoveIfICanFire(Submarine mySubmarine, Submarine opponentSubmarine) {
        if (!mySubmarine.getCanIfireFollowingSonarFeedback() && !mySubmarine.getCanIfireFollowingTorpedoFeedback()) {
            // update my lifeLoopBefore and opponent
            mySubmarine.setLifeLoopBefore(mySubmarine.getLife());
            opponentSubmarine.setLifeLoopBefore(opponentSubmarine.getLife());
            // no fire
            mySubmarine.setiFireOnPrecedentLoop(false);
            // order for move
            System.out.println(mySubmarine.getNextMoveString());
        }
    }
}


class Board {
    private static int EMPTY = 0;
    private static int ISLAND = 1;
    private static int ALREADY_VISITED = 2;

    private int nbrCellX;
    private int nbrCellY;
    private int map[][];
    private List<Cell> listCellAlreadyVisited;
    private List<Sector> listSecteurs;

    // constructor
    public Board() {
    }

    public Board(int nbrCellX, int nbrCellY, int[][] map , List<Cell> listCellAlreadyVisited, List<Sector> listSecteurs) {
        this.nbrCellX = nbrCellX;
        this.nbrCellY = nbrCellY;
        this.map = new int[nbrCellX][nbrCellY];
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
    public int[][] getMap() {
        return map;
    }
    public void setMap(int[][] map) {
        this.map = map;
    }

    // to string
    @Override
    public String toString() {
        return "Board{" +
                "nbrCellX=" + nbrCellX +
                ", nbrCellY=" + nbrCellY +
                ", listCellAlreadyVisited=" + listCellAlreadyVisited +
                ", listSecteurs=" + listSecteurs +
                '}';
    }

    public void createBiDirectionalTable(List<String> stringMap) {
        for (int i = 0; i < stringMap.size(); i++) {
            for (int j = 0; j < stringMap.get(i).length(); j++) {
                map[i][j] = stringMap.get(j).charAt(i) == 'x' ? ISLAND : EMPTY;
                if (map[i][j] == ISLAND) {
//                    System.err.println("ISLAND = " + i + j);
                }
            }
        }
    }

    public void setVisitedCell(Cell cell) {
        map[cell.getX()][cell.getY()] = ALREADY_VISITED;
    }

    public void setEmptyCell(Cell cell) {
        map[cell.getX()][cell.getY()] = EMPTY;
    }

    public void resetVisitedCell() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                map[i][j] = map[i][j] == ALREADY_VISITED ? EMPTY : map[i][j];
            }
        }
    }
}class Cell {
    private int x;
    private int y;
    private String cardinalPoint;
    private Integer distanceToDestinationCell;

    // constructor
    public Cell() {
    }

    public Cell(int x, int y, String cardinalPoint, Integer distanceToDestinationCell) {
        this.x = x;
        this.y = y;
        this.cardinalPoint = cardinalPoint;
        this.distanceToDestinationCell = distanceToDestinationCell;
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
    public Integer getDistanceToDestinationCell() {
        return distanceToDestinationCell;
    }
    public void setDistanceToDestinationCell(Integer distanceToDestinationCell) {
        this.distanceToDestinationCell = distanceToDestinationCell;
    }

    // to string
    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                ", cardinalPoint='" + cardinalPoint + '\'' +
                ", distanceToDestinationCell=" + distanceToDestinationCell +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
//        System.err.println("equals method:" + obj.toString());
//        System.err.println("equals method compare:" + x + y);
        Cell cell = (Cell) obj;
//        System.err.println("result:" + (x == cell.getX() && y == cell.getY()));
        return x == cell.getX() && y == cell.getY();
    }
}

/**
 * to deploy in real water!
 */
class Deploy {
    private static int EMPTY = 0;
    private static int ISLAND = 1;

    public String deploy0(Board board) {
        int map[][] = board.getMap();

        if (map[13][13] == EMPTY) {
            return "13 13";
        }
        if (map[13][1] == EMPTY) {
            return "13 1";
        }
        if (map[7][13] == EMPTY) {
            return "7 13";
        }
        else {
            return "0 0";
        }
    }
}
class LocateOpponent {
    Utils utils = new Utils();
    Torpedo torpedo = new Torpedo();
    int countOppPresence = 0;


    public void locateOpponent(Submarine mySubmarine, Submarine opponentSubmarine, Board board) {
        // ----------------------------------- after torpedo ------------------------------------------------------
        readIfOpponentSentTorpedo(mySubmarine,mySubmarine.getOpponentOrders());
        List<Cell> torpedorange = torpedo.createCellListTorpedoRange(mySubmarine.getPositionX(), mySubmarine.getPositionY(), board);
        mySubmarine.setListTorpedoRange(torpedorange);

        locateOpponentAfterTorpedo(mySubmarine, board);
        // check count fire list in progres
        if (mySubmarine.getTorpedoFireList() != null) {
            long countFireList = mySubmarine.getTorpedoFireList().stream().count();
            System.err.println("fire list in progress: " + countFireList);
        }
        // --------------------------------------------------------------------------------------------------------

        // ----------------------------------- check if opponent make surface -------------------------------------
        checkIfOpponentMakeSurface(mySubmarine, board);
        // --------------------------------------------------------------------------------------------------------

        // ----------------------------------- check if opponent make silent move -------------------------------------
        checkIfOpponentMakeSilence(mySubmarine, board);
        // --------------------------------------------------------------------------------------------------------

        // ----------------------------------- after goodluck fire ----------------------------------------------------
        locateOpponentAfter2pointOppIncreaseFire(opponentSubmarine, mySubmarine);
        locateOpponentAfter1pointOppIncreaseFire(opponentSubmarine, mySubmarine, board);
        // --------------------------------------------------------------------------------------------------------
    }

    // ***************************************************************************************************************

    public String readOpponentMove(String opponentOrders) {
        String[] moveOrders = opponentOrders.split("MOVE ");
//        int count = 0;
//            for (String a: moveOrders) {
//                System.err.println("move " + count + ":" + a);
//                count++;
//            }
        // check
        if (moveOrders.length > 1) {
            //check
//            System.err.println("opponent order move: " + moveOrders[1]);
            String opponentMove = moveOrders[1];
            return opponentMove;
        } else {
            return "opponent move unknown";
        }
    }

    public void readIfOpponentSentTorpedo(Submarine mySubmarine, String opponentOrders) {
        String[] torpedoCells = opponentOrders.split("TORPEDO ");
        // check
        if (torpedoCells.length > 1) {
            mySubmarine.setOpponentSendTorpedo(true);
        } else {
            mySubmarine.setOpponentSendTorpedo(false);
        }
    }

    public Cell readOpponentTorpedoCell(String opponentOrders) {
        Cell opponentTorpedoCell = new Cell();

        // if many opponent word
        if (opponentOrders.contains("|")) {
            String[] words = opponentOrders.split("\\|");
            for (String word:words) {
                if (word.contains("TORPEDO ")) {
                    String[] blocks = word.split(" ");
                    int torpX = Integer.valueOf(blocks[1]);
                    int torpY = Integer.valueOf(blocks[2]);
                    opponentTorpedoCell.setX(torpX);
                    opponentTorpedoCell.setY(torpY);
                }
            }
        } else {
            if (opponentOrders.contains("TORPEDO ")) {
                String[] blocks = opponentOrders.split(" ");
                int torpX = Integer.valueOf(blocks[1]);
                int torpY = Integer.valueOf(blocks[2]);
                opponentTorpedoCell.setX(torpX);
                opponentTorpedoCell.setY(torpY);
            }
        }

        // check
//        System.err.println("opponent order Torpedo cell: " + opponentTorpedoCell.toString());
        return opponentTorpedoCell;

    }

    public void createRangeCellOfOpponentPositionWhenSendTorpedo(Submarine mySubmarine, Board board) {

        // find torpedo explosion cell
        Cell opponentTorpedoCell = readOpponentTorpedoCell(mySubmarine.getOpponentOrders());

        // record torpedo explosion cell
        mySubmarine.setOpponentTorpedoExplosion(opponentTorpedoCell);

        // create list of position where opponent is (after he has send torpedo)
        List<Cell> listPresenceOpponentTorpedo = torpedo.createCellListTorpedoRange(opponentTorpedoCell.getX(), opponentTorpedoCell.getY(),board);

        // record presence opponent list on mySubmarine
        mySubmarine.setListOpponentPositionAfterTorpedo(listPresenceOpponentTorpedo);
        // check
//                System.err.println("opp list with torp: " + listPresenceOpponentTorpedo);
        long nbrCasePosible = listPresenceOpponentTorpedo.stream().count();
        System.err.println("opponnent send torp ->: " + nbrCasePosible + " cell position possibility!!");
    }

    public void updateOpponentPresenceListAfterTorpedoWithNewMovement(Submarine mySubmarine, Board board) {

        // read opponent next move
        String opponentNextMove = readOpponentMove(mySubmarine.getOpponentOrders());

        if (mySubmarine.getOpponentOrders().contains("SILENCE")) {
            mySubmarine.setOpponentTorpedoExplosion(null);
            mySubmarine.setTorpedoFireList(null);
            mySubmarine.setListOpponentPositionAfterTorpedo(null);
            mySubmarine.setOpponentCell(null);
            mySubmarine.setNbrOfCellOnTorpedoFireList(0);
            mySubmarine.setCanIfireFollowingSonarFeedback(false);
            mySubmarine.setCanIfireTorpedoFollowingOppTorp(false);
            mySubmarine.setCanIfireFollowingTorpedoFeedback(false);

            //check
            System.err.println("opp move in silent -> reset opponentTorpedoExplosion list");
        } else {
            // read opponent torpedo explosion cell
            // count torpedoFireList
            if (mySubmarine.getTorpedoFireList() != null) {
                Long torpedoFireList = mySubmarine.getTorpedoFireList().stream().count(); // >= 8 -> update is better with my new torpedo fire (-1 on opp)
                if (mySubmarine.getOpponentTorpedoExplosion() != null && torpedoFireList >= 8) {
                    Cell opponentTorpedocell = mySubmarine.getOpponentTorpedoExplosion();

                    // find torpedo explosion Cell following his next move
                    Cell opponentTorpedoCellAfterMove = utils.findCellWithCardinalPoint(opponentNextMove,opponentTorpedocell.getX(), opponentTorpedocell.getY());
                    // check
                    System.err.println("new cell center range opp : " + opponentTorpedoCellAfterMove.toString());

                    // re-center list of presence with opponent next move
                    List<Cell> listNewPresenceOpponentTorpedo = torpedo.createCellListTorpedoRange(opponentTorpedoCellAfterMove.getX(), opponentTorpedoCellAfterMove.getY(),board);

                    // re-record new range posibility of opponent presence
                    mySubmarine.setListOpponentPositionAfterTorpedo(listNewPresenceOpponentTorpedo);
                    // check
//        System.err.println("update opp range list(torpedo): " + mySubmarine.getListOpponentPositionAfterTorpedo().toString());

                    // record new torpedo explosion cell
                    mySubmarine.setOpponentTorpedoExplosion(opponentTorpedoCellAfterMove);
                }
            }

        }
        // check
//        System.err.println("passage update presence list after torpedo");
//        System.err.println("mySubmarine.getOpponentTorpedoExplosion = " + mySubmarine.getOpponentTorpedoExplosion());



    }

    public void checkIfOpponentMakeSurface(Submarine mySubmarine, Board board) {
        int opponentSectorNbr = -1;
        // read opponent next move
        String opponentNextMove = mySubmarine.getOpponentOrders();
        // check
        System.err.println("opp orders:" + opponentNextMove);
        if (opponentNextMove.matches("SURFACE.*")) {
            // check
            System.err.println("passage opp orders surface ");
            // get number of opponent sector surface
            String surfaceSector[] = opponentNextMove.split("SURFACE ");
            if (surfaceSector[1].contains("|")) {
                //check
                System.err.println("special passage bad writing opp orders surface");
                String surfaceSectorAffined[] = surfaceSector[1].split("|");
                opponentSectorNbr = Integer.valueOf(surfaceSectorAffined[0]);
            } else {
                opponentSectorNbr = Integer.valueOf(surfaceSector[1]);
            }
        }

        if (opponentSectorNbr != -1) {
            // compare with my sector position
            if (opponentSectorNbr == mySubmarine.getMySector()) {
//                oppPresenceOnMySector = true;
            }
            // todo if not -> move to this sector
        }
        mySubmarine.setOpponentSurfaceSector(opponentSectorNbr);

        // check
//        System.err.println("opponent surface on sector: " + opponentSectorNbr);
    }

    public void checkIfOpponentMakeSilence(Submarine mySubmarine, Board board) {
        String opponentOrder = mySubmarine.getOpponentOrders();
        if (opponentOrder.contains("SILENCE")){
            // check
            System.err.println("opponent move silently ");
            mySubmarine.setOpponentTorpedoExplosion(null);
            mySubmarine.setOpponentCell(null);
            //check
            System.err.println("follow opp torp lost");
            mySubmarine.setTorpedoFireList(null);
           // reset oppPresenceCell
            mySubmarine.setOpponentCell(null);
            // reset increase opp ( opp life -1)
            mySubmarine.setListOppCellIncrease(false);

        }
    }

    public void locateOpponentAfterTorpedo(Submarine mySubmarine, Board board) {
        if (mySubmarine.getListOpponentPositionAfterTorpedo() != null && !mySubmarine.getListOppCellIncrease()) { // no opp pos Cell increase
            updateOpponentPresenceListAfterTorpedoWithNewMovement(mySubmarine, board); // record result list on mySubmarine
        }
        if (mySubmarine.getListOpponentPositionAfterTorpedo() != null && mySubmarine.getListOppCellIncrease()) {// opp pos Cell increase (life opp -1)
            // check
            System.err.println("passage 2em opp move update fire list");
            affineListAroundMyTorpedo(mySubmarine, board);
        }
        if (mySubmarine.getOpponentSendTorpedo() && !mySubmarine.getListOppCellIncrease()) { // no opp pos Cell increase
            createRangeCellOfOpponentPositionWhenSendTorpedo(mySubmarine, board);
        }
    }

    public void locateOpponentAfter2pointOppIncreaseFire(Submarine opponentSubmarine, Submarine mySubmarine) {
        int compareOppLife = utils.compareLifeOfOpponent(opponentSubmarine);

        // move fireTorpedoCell whith opp movement
        // if my fire is on the good cell -> lock it
        if (compareOppLife == 2 || mySubmarine.getOpponentCell() != null) {
            // get opponent orders
            String oppMove = readOpponentMove(mySubmarine.getOpponentOrders());
            if (oppMove.equals("N") || oppMove.equals("S") || oppMove.equals("E") || oppMove.equals("W")) {
                if (countOppPresence == 0) {
                    if (mySubmarine.getMyFireTorpedoCell() != null) {
                        mySubmarine.setOpponentCell(utils.findCellWithCardinalPoint(oppMove, mySubmarine.getMyFireTorpedoCell().getX(), mySubmarine.getMyFireTorpedoCell().getY()));
                        countOppPresence++;
                        // check
                        System.err.println("opp life -2 !!!");
                        if (mySubmarine.getOpponentCell() != null) {
                            System.err.println("Opponent cell = " + mySubmarine.getOpponentCell().toString());
                        }
                    }
                }
                else {
                    if (mySubmarine.getOpponentCell() != null) {
                        mySubmarine.setOpponentCell(utils.findCellWithCardinalPoint(oppMove, mySubmarine.getOpponentCell().getX(), mySubmarine.getOpponentCell().getY()));
                        //check
                        if (mySubmarine.getOpponentCell() != null) {
                            System.err.println("Opponent cell second pass = " + mySubmarine.getOpponentCell().toString());
                        }
                    }
                }
            }
        }
    }


    public void locateOpponentAfter1pointOppIncreaseFire(Submarine opponentSubmarine, Submarine mySubmarine, Board board) {
        int compareOppLife = utils.compareLifeOfOpponent(opponentSubmarine);
        String opponentMove = mySubmarine.getOpponentOrders();

        if (compareOppLife == 1 && mySubmarine.getiFireOnPrecedentLoop() && !opponentMove.contains("SURFACE")) {
            // check
            System.err.println("----------->>> good fire opp loose 1 point !!");
            // create cell around my torpedo cell
            List<Cell> listAroundMyTorpedo = utils.cellsAroundOnecell(mySubmarine.getMyFireTorpedoCell(), board);
            // update mySubmarine next fireList
            mySubmarine.setTorpedoFireList(listAroundMyTorpedo);
            //check
            System.err.println("display new fire list: " + listAroundMyTorpedo);
            // todo move this zone with opp movement -> ok for one move
            affineListAroundMyTorpedo(mySubmarine, board);
        }
    }

    public void affineListAroundMyTorpedo(Submarine mySubmarine, Board board) {
        String oppOrders = mySubmarine.getOpponentOrders();
        List<Cell> listAroundMyTorpedo;

        if (oppOrders.contains("MOVE W")) {
            Cell newCellWithOppMove = new Cell(mySubmarine.getMyFireTorpedoCell().getX() - 1,
                    mySubmarine.getMyFireTorpedoCell().getY(), null, null);
            //check
            System.err.println("new center cell following opp move W = " + newCellWithOppMove);
            // re-made list fire with opp move
            listAroundMyTorpedo = utils.cellsAroundOnecell(newCellWithOppMove, board);
            //check
            Long countCell = listAroundMyTorpedo.stream().count();
            System.err.println("new opp presence possibility = " + countCell);
            mySubmarine.setListOpponentPositionAfterTorpedo(listAroundMyTorpedo);
            mySubmarine.setListOppCellIncrease(true);
            // set new cell torpedo with last information
            mySubmarine.setMyFireTorpedoCell(newCellWithOppMove);

        }
        if (oppOrders.contains("MOVE N")) {
            Cell newCellWithOppMove = new Cell(mySubmarine.getMyFireTorpedoCell().getX() ,
                    mySubmarine.getMyFireTorpedoCell().getY() - 1, null, null);
            //check
            System.err.println("new center cell following opp move N = " + newCellWithOppMove);
            // re-made list fire with opp move
            listAroundMyTorpedo = utils.cellsAroundOnecell(newCellWithOppMove, board);
            //check
            Long countCell = listAroundMyTorpedo.stream().count();
            System.err.println("new opp presence possibility = " + countCell);
            mySubmarine.setListOpponentPositionAfterTorpedo(listAroundMyTorpedo);
            mySubmarine.setListOppCellIncrease(true);
            // set new cell torpedo with last information
            mySubmarine.setMyFireTorpedoCell(newCellWithOppMove);

        }
        if (oppOrders.contains("MOVE E")) {
            Cell newCellWithOppMove = new Cell(mySubmarine.getMyFireTorpedoCell().getX() + 1,
                    mySubmarine.getMyFireTorpedoCell().getY(), null, null);
            //check
            System.err.println("new center cell following opp move E = " + newCellWithOppMove);
            // re-made list fire with opp move
            listAroundMyTorpedo = utils.cellsAroundOnecell(newCellWithOppMove, board);
            //check
            Long countCell = listAroundMyTorpedo.stream().count();
            System.err.println("new opp presence possibility = " + countCell);
            mySubmarine.setListOpponentPositionAfterTorpedo(listAroundMyTorpedo);
            mySubmarine.setListOppCellIncrease(true);
            // set new cell torpedo with last information
            mySubmarine.setMyFireTorpedoCell(newCellWithOppMove);

        }
        if (oppOrders.contains("MOVE S")) {
            Cell newCellWithOppMove = new Cell(mySubmarine.getMyFireTorpedoCell().getX(),
                    mySubmarine.getMyFireTorpedoCell().getY() + 1, null, null);
            //check
            System.err.println("new center cell following opp move S = " + newCellWithOppMove);
            // re-made list fire with opp move
            listAroundMyTorpedo = utils.cellsAroundOnecell(newCellWithOppMove, board);
            //check
            Long countCell = listAroundMyTorpedo.stream().count();
            System.err.println("new opp presence possibility = " + countCell);
            mySubmarine.setListOpponentPositionAfterTorpedo(listAroundMyTorpedo);
            mySubmarine.setListOppCellIncrease(true);
            // set new cell torpedo with last information
            mySubmarine.setMyFireTorpedoCell(newCellWithOppMove);

        }
        if (oppOrders.contains("SILENCE")) {
            System.err.println("opp move silently..., reset fire list");
            mySubmarine.setTorpedoFireList(null);
        }
    }



}

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
/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    private Deploy deploy;
    private Move move;
    private Submarine mySubmarine;
    private Submarine opponentSubmarine;
    private Board board;
    private Utils utils;
    private Torpedo torpedo;
    private LocateOpponent locateOpponent;
    private Action action;

    public static void main(String args[]) {
      new Player().run();
    }

    public void run() {
        deploy = new Deploy();
        move = new Move();
        mySubmarine = new Submarine();
        opponentSubmarine = new Submarine();
        board = new Board();
        utils = new Utils();
        torpedo = new Torpedo();
        locateOpponent = new LocateOpponent();
        action = new Action();

        Scanner in = new Scanner(System.in);
        int width = in.nextInt();
        int height = in.nextInt();
        int myId = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }
        // create biDirectional table for map
        List<String> mapLines = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            String line = in.nextLine();
            mapLines.add(line);
        }
        // create new board
        board = new Board(width, height,null, null, utils.makeSectors(0, 0));
        board.createBiDirectionalTable(mapLines);

        // order of deploiement
        System.out.println(deploy.deploy0(board));

        // initial state of mySubmarine
        mySubmarine.startInitialState(mySubmarine, board);

        // ***************************************** 2 **** Game loop**************************************************
        while (true) {
            // initial state for each loop
            mySubmarine.startInitialStateGameLoop(mySubmarine);
            int x = in.nextInt();
            int y = in.nextInt();
            int myLife = in.nextInt();
            int oppLife = in.nextInt();
            opponentSubmarine.setLife(oppLife);
            int torpedoCooldown = in.nextInt();
            int sonarCooldown = in.nextInt();
            int silenceCooldown = in.nextInt();
            int mineCooldown = in.nextInt();
            String sonarResult = in.next();
            if (in.hasNextLine()) {
                in.nextLine();
            }
            String opponentOrders = in.nextLine();

            mySubmarine.updateSubmarine(myId,x,y,myLife,torpedoCooldown,sonarCooldown,silenceCooldown,mineCooldown,
                    sonarResult, opponentOrders);
            mySubmarine.setSafeListOfCellAroundMe(utils.createSafeCellListAroundMe(mySubmarine, board));

            // ******************************* 3   **** Locate opponent ***********************************************
            locateOpponent.locateOpponent(mySubmarine, opponentSubmarine, board);

            // ******************************* 4 ****** MySubmarine Check *********************************************
            mySubmarine.check(mySubmarine,opponentSubmarine, board);

            // ******************************* 5 ****** MOVE AND ACTION ***********************************************
            // --security-- ->if i can't move to opp pos
            if (mySubmarine.getNbrOfCellOnTorpedoFireList() == 0) { mySubmarine.setOpponentCell(null); }
            // IA1 -> random cell ; IA2 -> scanning ; IA3 -> random cell(each move) on random sector.
//            mySubmarine.setNextMoveString(move.moveIA3(mySubmarine, board));
//            mySubmarine.setNextMoveString(move.moveIA4(mySubmarine, board));
            if (mySubmarine.getOpponentCell() != null ||
                    (mySubmarine.getNbrOfCellOnTorpedoFireList() <= 9) && mySubmarine.getNbrOfCellOnTorpedoFireList() > 0) {
                System.err.println("try to following opp move !");
                mySubmarine.setNextMoveString(move.moveIA5(mySubmarine, board)); }
            else { mySubmarine.setNextMoveString(move.moveIA4(mySubmarine, board)); }
            // ----------------------------------------------------------------
            action.whenMySubmarineToSurface(mySubmarine, board);
            action.forChargingSubmarineTools(mySubmarine);
            action.whenMySubmarineIs2PointsDamaged(mySubmarine);

            // ----------- FIRE IF YOU CAN  ---------- FIRE -------------- FIRE -----------
            action.fireChoiceWhenTorpediFeedbackAndSonarFeedback(mySubmarine);
            action.fireWhenOppSendTorpedo(mySubmarine);
            action.fireWhenSonarFeedbackOnMySector(mySubmarine, opponentSubmarine, board);

            // -----------OR JUST MOVE  ---------- JUST MOVE -------------- JUST MOVE -----------
            action.justMoveIfICanFire(mySubmarine, opponentSubmarine);

            // print submarines info
//            System.err.println("My submarine: " + mySubmarine.toString());
//            System.err.println("Opponent submarine: " + opponentSubmarine.toString());
        }
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

class Submarine {
    private int id;
    private int positionX;
    private int positionY;
    private int mySector;
    private int life;
    private int lifeLoopBefore;
    private int torpedoCooldown;
    private int sonarCooldown;
    private int silenceCooldown;
    private int mineCooldown;
    private String sonarResult;
    private String opponentOrders;
    private Cell opponentCell;
    private int opponentSurfaceSector;

    private List<Cell> safeListOfCellAroundMe;
    private List<Cell> listTorpedoRange;
    private List<Cell> listOpponentPositionAfterTorpedo;
    private Cell opponentTorpedoExplosion;
    private List<Cell> torpedoFireList;
    private Cell myNextMove;
    private int destinationSector;
    private Cell myFireTorpedoCell;

    private Boolean loadedTorpedo;
    private Boolean loadedSonar;
    private Boolean loadedSilence;
    private Boolean isOpponentSendTorpedo;

    private Boolean canIfireTorpedoFollowingOppTorp;
    private Boolean canIfireFollowingTorpedoFeedback;
    private Boolean canIfireFollowingSonarFeedback;
    private Boolean canISendSonar;

    private Boolean moveNextOnSilence;
    private Boolean oppPresenceOnMySector;

    private String nextMoveString;

    private Boolean iFireOnPrecedentLoop;
    private Boolean listOppCellIncrease;

    private int nbrOfCellOnTorpedoFireList;
    private int possibilityOfCellsLeak;


    // constructor
    public Submarine() {
    }

    public Submarine(int id, int positionX, int positionY, int mySector, int life, int lifeLoopBefore, int torpedoCooldown, int sonarCooldown, int silenceCooldown, int mineCooldown, String sonarResult, String opponentOrders, Cell opponentCell, int opponentSurfaceSector, List<Cell> safeListOfCellAroundMe, List<Cell> listTorpedoRange, List<Cell> listOpponentPositionAfterTorpedo, Cell opponentTorpedoExplosion, List<Cell> torpedoFireList, Cell myNextMove, int destinationSector, Cell myFireTorpedoCell, Boolean loadedTorpedo, Boolean loadedSonar, Boolean loadedSilence, Boolean isOpponentSendTorpedo, Boolean canIfireTorpedoFollowingOppTorp, Boolean canIfireFollowingTorpedoFeedback, Boolean canIfireFollowingSonarFeedback, Boolean canISendSonar, Boolean moveNextOnSilence, Boolean oppPresenceOnMySector, String nextMoveString, Boolean iFireOnPrecedentLoop, Boolean listOppCellIncrease, int nbrOfCellOnTorpedoFireList, int possibilityOfCellsLeak) {
        this.id = id;
        this.positionX = positionX;
        this.positionY = positionY;
        this.mySector = mySector;
        this.life = life;
        this.lifeLoopBefore = lifeLoopBefore;
        this.torpedoCooldown = torpedoCooldown;
        this.sonarCooldown = sonarCooldown;
        this.silenceCooldown = silenceCooldown;
        this.mineCooldown = mineCooldown;
        this.sonarResult = sonarResult;
        this.opponentOrders = opponentOrders;
        this.opponentCell = opponentCell;
        this.opponentSurfaceSector = opponentSurfaceSector;
        this.safeListOfCellAroundMe = safeListOfCellAroundMe;
        this.listTorpedoRange = listTorpedoRange;
        this.listOpponentPositionAfterTorpedo = listOpponentPositionAfterTorpedo;
        this.opponentTorpedoExplosion = opponentTorpedoExplosion;
        this.torpedoFireList = torpedoFireList;
        this.myNextMove = myNextMove;
        this.destinationSector = destinationSector;
        this.myFireTorpedoCell = myFireTorpedoCell;
        this.loadedTorpedo = loadedTorpedo;
        this.loadedSonar = loadedSonar;
        this.loadedSilence = loadedSilence;
        this.isOpponentSendTorpedo = isOpponentSendTorpedo;
        this.canIfireTorpedoFollowingOppTorp = canIfireTorpedoFollowingOppTorp;
        this.canIfireFollowingTorpedoFeedback = canIfireFollowingTorpedoFeedback;
        this.canIfireFollowingSonarFeedback = canIfireFollowingSonarFeedback;
        this.canISendSonar = canISendSonar;
        this.moveNextOnSilence = moveNextOnSilence;
        this.oppPresenceOnMySector = oppPresenceOnMySector;
        this.nextMoveString = nextMoveString;
        this.iFireOnPrecedentLoop = iFireOnPrecedentLoop;
        this.listOppCellIncrease = listOppCellIncrease;
        this.nbrOfCellOnTorpedoFireList = nbrOfCellOnTorpedoFireList;
        this.possibilityOfCellsLeak = possibilityOfCellsLeak;
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
    public int getMySector() {
        return mySector;
    }
    public void setMySector(int mySector) {
        this.mySector = mySector;
    }
    public int getLife() {
        return life;
    }
    public void setLife(int life) {
        this.life = life;
    }
    public int getLifeLoopBefore() {
        return lifeLoopBefore;
    }
    public void setLifeLoopBefore(int lifeLoopBefore) {
        this.lifeLoopBefore = lifeLoopBefore;
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
    public List<Cell> getListOpponentPositionAfterTorpedo() {
        return listOpponentPositionAfterTorpedo;
    }
    public void setListOpponentPositionAfterTorpedo(List<Cell> listOpponentPositionAfterTorpedo) {
        this.listOpponentPositionAfterTorpedo = listOpponentPositionAfterTorpedo;
    }
    public Cell getOpponentTorpedoExplosion() {
        return opponentTorpedoExplosion;
    }
    public void setOpponentTorpedoExplosion(Cell opponentTorpedoExplosion) {
        this.opponentTorpedoExplosion = opponentTorpedoExplosion;
    }
    public List<Cell> getTorpedoFireList() {
        return torpedoFireList;
    }
    public void setTorpedoFireList(List<Cell> torpedoFireList) {
        this.torpedoFireList = torpedoFireList;
    }
    public Cell getMyNextMove() {
        return myNextMove;
    }
    public void setMyNextMove(Cell myNextMove) {
        this.myNextMove = myNextMove;
    }
    public int getDestinationSector() {
        return destinationSector;
    }
    public void setDestinationSector(int destinationSector) {
        this.destinationSector = destinationSector;
    }
    public Cell getMyFireTorpedoCell() {
        return myFireTorpedoCell;
    }
    public void setMyFireTorpedoCell(Cell myFireTorpedoCell) {
        this.myFireTorpedoCell = myFireTorpedoCell;
    }
    public Cell getOpponentCell() {
        return opponentCell;
    }
    public void setOpponentCell(Cell opponentCell) {
        this.opponentCell = opponentCell;
    }
    public Boolean getLoadedTorpedo() {
        return loadedTorpedo;
    }
    public void setLoadedTorpedo(Boolean loadedTorpedo) {
        this.loadedTorpedo = loadedTorpedo;
    }
    public Boolean getLoadedSonar() {
        return loadedSonar;
    }
    public void setLoadedSonar(Boolean loadedSonar) {
        this.loadedSonar = loadedSonar;
    }
    public Boolean getLoadedSilence() {
        return loadedSilence;
    }
    public void setLoadedSilence(Boolean loadedSilence) {
        this.loadedSilence = loadedSilence;
    }
    public Boolean getOpponentSendTorpedo() {
        return isOpponentSendTorpedo;
    }
    public void setOpponentSendTorpedo(Boolean opponentSendTorpedo) {
        isOpponentSendTorpedo = opponentSendTorpedo;
    }
    public Boolean getCanIfireTorpedoFollowingOppTorp() {
        return canIfireTorpedoFollowingOppTorp;
    }
    public void setCanIfireTorpedoFollowingOppTorp(Boolean canIfireTorpedoFollowingOppTorp) {
        this.canIfireTorpedoFollowingOppTorp = canIfireTorpedoFollowingOppTorp;
    }
    public Boolean getCanIfireFollowingTorpedoFeedback() {
        return canIfireFollowingTorpedoFeedback;
    }
    public void setCanIfireFollowingTorpedoFeedback(Boolean canIfireFollowingTorpedoFeedback) {
        this.canIfireFollowingTorpedoFeedback = canIfireFollowingTorpedoFeedback;
    }
    public Boolean getCanISendSonar() {
        return canISendSonar;
    }
    public void setCanISendSonar(Boolean canISendSonar) {
        this.canISendSonar = canISendSonar;
    }
    public Boolean getCanIfireFollowingSonarFeedback() {
        return canIfireFollowingSonarFeedback;
    }
    public void setCanIfireFollowingSonarFeedback(Boolean canIfireFollowingSonarFeedback) {
        this.canIfireFollowingSonarFeedback = canIfireFollowingSonarFeedback;
    }
    public Boolean getMoveNextOnSilence() {
        return moveNextOnSilence;
    }
    public void setMoveNextOnSilence(Boolean moveNextOnSilence) {
        this.moveNextOnSilence = moveNextOnSilence;
    }
    public Boolean getOppPresenceOnMySector() {
        return oppPresenceOnMySector;
    }
    public void setOppPresenceOnMySector(Boolean oppPresenceOnMySector) {
        this.oppPresenceOnMySector = oppPresenceOnMySector;
    }
    public int getOpponentSurfaceSector() {
        return opponentSurfaceSector;
    }
    public void setOpponentSurfaceSector(int opponentSurfaceSector) {
        this.opponentSurfaceSector = opponentSurfaceSector;
    }
    public String getNextMoveString() {
        return nextMoveString;
    }
    public void setNextMoveString(String nextMoveString) {
        this.nextMoveString = nextMoveString;
    }
    public Boolean getiFireOnPrecedentLoop() {
        return iFireOnPrecedentLoop;
    }
    public void setiFireOnPrecedentLoop(Boolean iFireOnPrecedentLoop) {
        this.iFireOnPrecedentLoop = iFireOnPrecedentLoop;
    }
    public Boolean getListOppCellIncrease() {
        return listOppCellIncrease;
    }
    public void setListOppCellIncrease(Boolean listOppCellIncrease) {
        this.listOppCellIncrease = listOppCellIncrease;
    }
    public int getNbrOfCellOnTorpedoFireList() {
        return nbrOfCellOnTorpedoFireList;
    }
    public void setNbrOfCellOnTorpedoFireList(int nbrOfCellOnTorpedoFireList) {
        this.nbrOfCellOnTorpedoFireList = nbrOfCellOnTorpedoFireList;
    }
    public int getPossibilityOfCellsLeak() {
        return possibilityOfCellsLeak;
    }
    public void setPossibilityOfCellsLeak(int possibilityOfCellsLeak) {
        this.possibilityOfCellsLeak = possibilityOfCellsLeak;
    }

    // to string
    @Override
    public String toString() {
        return "Submarine{" +
                "id=" + id +
                ", positionX=" + positionX +
                ", positionY=" + positionY +
                ", mySector=" + mySector +
                ", life=" + life +
                ", lifeLoopBefore=" + lifeLoopBefore +
                ", torpedoCooldown=" + torpedoCooldown +
                ", sonarCooldown=" + sonarCooldown +
                ", silenceCooldown=" + silenceCooldown +
                ", mineCooldown=" + mineCooldown +
                ", sonarResult='" + sonarResult + '\'' +
                ", opponentOrders='" + opponentOrders + '\'' +
                ", opponentCell=" + opponentCell +
                ", safeListOfCellAroundMe=" + safeListOfCellAroundMe +
                ", listTorpedoRange=" + listTorpedoRange +
                ", listOpponentPositionAfterTorpedo=" + listOpponentPositionAfterTorpedo +
                ", opponentTorpedoExplosion=" + opponentTorpedoExplosion +
                ", torpedoFireList=" + torpedoFireList +
                ", myNextMove=" + myNextMove +
                ", destinationSector=" + destinationSector +
                ", myFireTorpedoCell=" + myFireTorpedoCell +
                ", loadedTorpedo=" + loadedTorpedo +
                ", loadedSonar=" + loadedSonar +
                ", loadedSilence=" + loadedSilence +
                ", isOpponentSendTorpedo=" + isOpponentSendTorpedo +
                '}';
    }

    public void updateSubmarine(int id, int positionX, int positionY, int life, int torpedoCooldown,
                                int sonarCooldown, int silenceCooldown, int mineCooldown,
                                String sonarResult, String opponentOrders ) {
        this.setId(id);
        this.setPositionX(positionX);
        this.setPositionY(positionY);
        this.setLife(life);
        this.setTorpedoCooldown(torpedoCooldown);
        this.setSonarCooldown(sonarCooldown);
        this.setSilenceCooldown(silenceCooldown);
        this.setMineCooldown(mineCooldown);
        this.setSonarResult(sonarResult);
        this.setOpponentOrders(opponentOrders);
    }

    public void startInitialState(Submarine mySubmarine, Board board) {
        mySubmarine.setLoadedTorpedo(false);
        mySubmarine.setLoadedSonar(false);
        mySubmarine.setLoadedSilence(false);
        mySubmarine.setOpponentSendTorpedo(false);
        mySubmarine.setiFireOnPrecedentLoop(false);
        mySubmarine.setListOppCellIncrease(false);
    }

    public void startInitialStateGameLoop(Submarine mySubmarine) {
        mySubmarine.canIfireTorpedoFollowingOppTorp = false;
        mySubmarine.canIfireFollowingTorpedoFeedback = false;
        mySubmarine.canISendSonar = false;
        mySubmarine.canIfireFollowingSonarFeedback = false;
        mySubmarine.moveNextOnSilence = false;
        mySubmarine.oppPresenceOnMySector = false;
    }

    public void check(Submarine mySubmarine, Submarine opponentSubmarine, Board board) {
        Utils utils = new Utils();
        Torpedo torpedo = new Torpedo();

        // sonar feedback
        System.err.println("retour sonar: " + mySubmarine.getSonarResult());

        // mySector position
        mySubmarine.setMySector(utils.findMyPositionSector(mySubmarine, board));

        // check if i can fire torpedo following my position (my torpedo loaded and opponent locate list)
        if (mySubmarine.getTorpedoCooldown() == 0 && mySubmarine.getListOpponentPositionAfterTorpedo() != null) {
            torpedo.canIFireTorpedo(mySubmarine, board);
        }
        // check if my torpedo is loaded
        if (mySubmarine.getTorpedoCooldown() == 0) {
            mySubmarine.setLoadedTorpedo(true);
            // check
            System.err.println("Torpedo loaded and list range ok");
        }
        // check if my sonar is loaded
        if (mySubmarine.getSonarCooldown() == 0) {
            mySubmarine.setLoadedSonar(true);
            //check
            System.err.println("Sonar loaded");
        }
        // check if silence is loaded
        if (mySubmarine.getSilenceCooldown() == 0) {
            mySubmarine.setLoadedSilence(true);
            //check
            System.err.println("Silence loaded");
            // lunch sonar in my sector
            if (mySubmarine.getSonarCooldown() == 0) {
                mySubmarine.setCanISendSonar(true);
                //check
                System.err.println("sonar can sent!");
            }
        }
        // add torpedo order if possible
        if (mySubmarine.getCanIfireTorpedoFollowingOppTorp() && mySubmarine.getLoadedTorpedo()) {
            mySubmarine.setCanIfireFollowingTorpedoFeedback(true);
            mySubmarine.setLoadedTorpedo(false);
        }

        // if mySubmarine life increase 2 point -> next move to silence if possible
        int lifeDown = utils.compareLifeLoopBefore(mySubmarine);
        // check
            System.err.println("my life down : " + lifeDown);
        if ((lifeDown == 2) && mySubmarine.getLoadedSilence()) {
            // next move to silence
            mySubmarine.setMoveNextOnSilence(true);
        }

        // if opponent life increase 2 point -> lock his position
        int oppLifeDown = utils.compareLifeOfOpponent(opponentSubmarine);

        // check if opponent is in my  sector with sonar feedback
        if (mySubmarine.getSonarResult().equals("Y")) {
            mySubmarine.setCanIfireFollowingSonarFeedback(true);
        }

        // check countNbrOfCellOnTorpedoFireList
        setNbrOfCellOnTorpedoFireList(countNbrOfCellOnTorpedoFireList());
    }

    public int countNbrOfCellOnTorpedoFireList() {
        if (getTorpedoFireList() != null ) {
            long nbr = getTorpedoFireList().stream().count();
            // check
            System.err.println("nbr of cell torpedo fire list: " + nbr);
            return (int)nbr;
        } else {
            return 0;
        }

    }
}
class Torpedo {
    Utils utils = new Utils();

    /**
     * Create cell list of my torpedo range or opponent case range (when he has send torpedo)
     * removing s and y (my position or explosion position)
     * @param x myPositionX or explosedOponentTorpedoX
     * @param y myPositionY or explosedOponentTorpedoY
     * @param board
     * @return
     */
    public List<Cell> createCellListTorpedoRange(int x, int y, Board board) {
        List<Cell> listCellTorpedoRange = new ArrayList<>();
        int rangeMinX = -4;
        int rangeMaxX = 4;
        int centerCellY = y;
        int countChangeSide = 0;
        //down and up triangle
        for (int j = 1; j <= 11; j++) {
            // add line
            for (int i = rangeMinX; i <= rangeMaxX ; i++) {
                // create centerCell
                Cell leftEndCell = new Cell(x + i, centerCellY, null,null);
                listCellTorpedoRange.add(leftEndCell);
                countChangeSide++;
            }
            // add down triangle
            if (countChangeSide <= 25) {
                // check
//                System.err.println("passage down" );
                rangeMaxX--;
                rangeMinX++;
                centerCellY++;
                if (countChangeSide == 25) {
                    countChangeSide++;
                }
            } else {
                if (countChangeSide == 26) {
                    rangeMinX = -4;
                    rangeMaxX = 4;
                    centerCellY = y;
                    countChangeSide++;
                }
                // add up triangle
                if (countChangeSide >= 26) {
                    // check
                    rangeMaxX--;
                    rangeMinX++;
                    centerCellY--;
                }
            }
        }
        // check all
//        System.err.println("my torpedo range list all " + listCellTorpedoRange.toString());

        // remove end of map
        List<Cell> listWithoutEndOfMap = utils.removeEndOfMap(listCellTorpedoRange, board);
        // remove earth on last list
        List<Cell> listWithoutEndOfMapAndEarth = utils.removeEarthCellTable(listWithoutEndOfMap, board);
        // remove center position
        List<Cell> listWithoutEndOfMapAndEarthAndCenterCell = listWithoutEndOfMapAndEarth;
        for (int i = 0; i < listWithoutEndOfMapAndEarth.size(); i++) {
            if (listWithoutEndOfMapAndEarth.get(i).getX() == x && listWithoutEndOfMapAndEarth.get(i).getY() == y) {
                listWithoutEndOfMapAndEarthAndCenterCell.remove(i);
            }

        }
        // check
//        System.err.println("my torpedo range list " + listWithoutEndOfMapAndEarthAndXY.toString());
//        long nbrCasePosible = listWithoutEndOfMapAndEarthAndXY.stream().count();
//        System.err.println("my taget case possible(torp): " + nbrCasePosible);
        return listWithoutEndOfMapAndEarthAndCenterCell;
    }

    public List<Cell> mixRangePossibilityAfterTorpedoWithMyRangeTorpedo (List<Cell> inputRangeListAfterTorpedo, Submarine mySubmarine) {
        List<Cell> possibilityOfTorpedoFire = new ArrayList<>();
        List<Cell> myRangeTorpedoList = mySubmarine.getListTorpedoRange();
        //check.
//        System.err.println("5 myRangeTorpedoList= " + myRangeTorpedoList.stream().count());

        for (int i = 0; i < inputRangeListAfterTorpedo.size() ; i++) { // ok this list contains cells!
            for (int j = 0; j < myRangeTorpedoList.size(); j++) {
                if (inputRangeListAfterTorpedo.get(i).getX() == myRangeTorpedoList.get(j).getX()) {
                    if (inputRangeListAfterTorpedo.get(i).getY() == myRangeTorpedoList.get(j).getY()) {
                        possibilityOfTorpedoFire.add(myRangeTorpedoList.get(j));
                    }
                }
            }
        }
        // check
//        System.err.println("Possibility of fire on: " + possibilityOfTorpedoFire.toString());
        return possibilityOfTorpedoFire;
    }

    public void canIFireTorpedo(Submarine mySubmarine, Board board) {
        List<Cell> potentialfireList = mixRangePossibilityAfterTorpedoWithMyRangeTorpedo(mySubmarine.getListOpponentPositionAfterTorpedo(), mySubmarine);
        // remove cell around me
        utils.createSafeCellListAroundMe(mySubmarine, board);
        // remove safeList of potentialFireList
        List<Cell> fireList = utils.removeCellsOnList(potentialfireList, mySubmarine.getSafeListOfCellAroundMe());
        mySubmarine.setTorpedoFireList(fireList);
        // check
        Long countFirecell = fireList.stream().count();
        System.err.println("Possibility of fire on: " + countFirecell);
        if (!fireList.isEmpty()) {
            mySubmarine.setCanIfireTorpedoFollowingOppTorp(true);
        } else {
            mySubmarine.setCanIfireTorpedoFollowingOppTorp(false);
        }
    }

    public Cell createPossibilitiCellOnSector(Submarine mySubmarine, Board board) {
        List<Cell> resultList = new ArrayList<>();
        int countFireList = 0;

        // get random cell on sector without safe list around me and only my torpedo range
        List<Cell> mySectorCellList = utils.findSectorCellWithPosition(mySubmarine, board);
        // list of safe cell (around me)
        utils.createSafeCellListAroundMe(mySubmarine, board);
        List<Cell> mySafeCellList = mySubmarine.getSafeListOfCellAroundMe();
        // remove safe cell to mysecto cells
        List<Cell> mySectorWithoutsafeList = utils.removeCellsOnList(mySectorCellList, mySafeCellList);
        // check
//        System.err.println("3 Create possibility Fire cell on sector " + mySectorWithoutsafeList.stream().count()); //21
        // get my range torpedo list
        // keep only forFireList - myrangeTorpedo -> to verify
        List<Cell> fireList = mixRangePossibilityAfterTorpedoWithMyRangeTorpedo(mySectorWithoutsafeList,mySubmarine); // 0!! bug
        // check
//        System.err.println("4 Create possibility Fire cell on sector " + fireList.stream().count());
        if (fireList.stream().count() == 0) {
            mySubmarine.setCanIfireFollowingSonarFeedback(false);
        }
        // check
//                System.err.println("potential fire following sonar: " + fireList.toString());
        System.err.println("potential fire on my sector: " + fireList.stream().count());
        for (int i = 0; i < fireList.size(); i++) {
            countFireList++;
        }
        // get random cell on fireList.
        if (countFireList > 0) {
            Cell randomfireTorpedo = utils.randomCellOnList(fireList);
            return randomfireTorpedo;
        } else {
            return new Cell(-1,-1,null,null);
        }

    }
}

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

    public void myNextMoveWhenILeak(Submarine mySubmarine, int nbrOfLeakcells) {
        String cardinalString = mySubmarine.getMyNextMove().getCardinalPoint();
        Cell futurPos = new Cell();
        // check
        System.err.println("cardinal string:" + cardinalString);
        if (cardinalString.equals("N ")) {
            futurPos.setX(mySubmarine.getPositionX());
            futurPos.setY(mySubmarine.getPositionY() - nbrOfLeakcells);
        }
        if (cardinalString.equals("S ")) {
            futurPos.setX(mySubmarine.getPositionX());
            futurPos.setY(mySubmarine.getPositionY() + nbrOfLeakcells);
        }
        if (cardinalString.equals("E ")) {
            futurPos.setX(mySubmarine.getPositionX() + nbrOfLeakcells);
            futurPos.setY(mySubmarine.getPositionY());
        }
        else {
            futurPos.setX(mySubmarine.getPositionX() - nbrOfLeakcells);
            futurPos.setY(mySubmarine.getPositionY());
        }
        mySubmarine.setMyNextMove(futurPos);
        // check
        System.err.println("my new pos after leak = " + futurPos.toString());
    }
}