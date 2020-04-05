import java.util.List;

class LocateOpponent {
    Utils utils = new Utils();
    Torpedo torpedo = new Torpedo();

    /**
     * Next opponent move
     * @param opponentOrders
     * @return
     */
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

    public boolean readIfOpponentSentTorpedo(String opponentOrders) {
        String[] torpedoCells = opponentOrders.split("TORPEDO ");
        // check
        if (torpedoCells.length > 1) {
            return true;
        } else {
            return false;
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

        // opponent Presence List After Torpedo Update With his next Movement
//        updateOpponentPresenceListAfterTorpedoWithNewMovement(mySubmarine, board);
    }

    public void updateOpponentPresenceListAfterTorpedoWithNewMovement(Submarine mySubmarine, Board board) {

        // read opponent next move
        String opponentNextMove = readOpponentMove(mySubmarine.getOpponentOrders());
        // if opp order is not silence
//        if (!opponentNextMove.contains("SILENCE")) {
//
//        }
        // read opponent torpedo explosion cell
        if (mySubmarine.getOpponentTorpedoExplosion() != null) {
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

    public int checkIfOpponentMakeSurface(Submarine mySubmarine, Board board) {
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
        // check
//        System.err.println("opponent surface on sector: " + opponentSectorNbr);
        return opponentSectorNbr;
    }

    public boolean checkIfOpponentMakeSilence(Submarine mySubmarine, Board board) {
        String opponentOrder = mySubmarine.getOpponentOrders();
        if (opponentOrder.contains("SILENCE")) {
            return true;
        }
        else return false;
    }
}
