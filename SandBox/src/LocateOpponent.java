import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.List;

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
            System.err.println("opponent order move: " + moveOrders[1]);
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
        System.err.println("opponent order Torpedo cell: " + opponentTorpedoCell.toString());
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
        System.err.println("passage update presence list after torpedo");
        System.err.println("mySubmarine.getOpponentTorpedoExplosion = " + mySubmarine.getOpponentTorpedoExplosion());



    }

    public void checkIfOpponentMakeSurface(Submarine mySubmarine, Board board) {
        int opponentSectorNbr = -1;
        // read opponent next move
        String opponentNextMove = mySubmarine.getOpponentOrders();
        if (opponentNextMove.contains("SURFACE")) {
            // check
//            System.err.println("passage opp surface ");
            // get number of opponent sector surface
            String surfaceSector[] = opponentNextMove.split("SURFACE ");
            opponentSectorNbr = Integer.valueOf(surfaceSector[1]);
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
                            System.err.println("new opp cell = " + mySubmarine.getOpponentCell().toString());
                        }
                    }
                }
                else {
                    if (mySubmarine.getOpponentCell() != null) {
                        mySubmarine.setOpponentCell(utils.findCellWithCardinalPoint(oppMove, mySubmarine.getOpponentCell().getX(), mySubmarine.getOpponentCell().getY()));
                        //check
                        if (mySubmarine.getOpponentCell() != null) {
                            System.err.println("new opp cell second pass = " + mySubmarine.getOpponentCell().toString());
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
