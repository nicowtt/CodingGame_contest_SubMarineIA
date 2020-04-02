import java.util.*;
import java.util.stream.Collectors;

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
        LocateOpponent locateOpponent = new LocateOpponent();
        List<Cell> listCellAlreadyVisited = new ArrayList<>();
        String chargeSonar = "SONAR";
        String chargeTorpedo = "TORPEDO";
        String chargeSilence = "SILENCE";
        String sentSonar = "SONAR ";
        String silence = "SILENCE ";
        boolean isOpponentSentTorpedo = false;
        boolean loadedTorpedo = false;
        boolean loadedSonar = false;
        boolean loadedSilence = false;

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

        // ****** 1 **** Deployement********************************************************************************************:
        // map analyse for deploy
        String deployPositionString = deploy.deploy1(earthMap);

        // order of deploiement
        System.out.println(deployPositionString);

        // create sectors
        List<Sector> sectorList = utils.makeSectors(0,0);
        board.setListSecteurs(sectorList);

        // ********* 2 **** Game loop********************************************************************************************:
        while (true) {
            boolean fireTorpedoFollowingOppTorp = false;
            boolean fireFollowingTorpedoFeedback = false;
            boolean lunchSonar = false;
            boolean fireFollowingSonarFeedback = false;
            boolean moveNextOnSilence = false;
            boolean oppPresenceOnMySector = false;
            int x = in.nextInt();
            mySubmarine.setPositionX(x);
            int y = in.nextInt();
            mySubmarine.setPositionY(y);
            Cell myMoveCell= new Cell(x,y,null,null);
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

            // utils
            utils.createSafeCellListAroundMe(mySubmarine, board);
            isOpponentSentTorpedo = locateOpponent.readIfOpponentSentTorpedo(mySubmarine.getOpponentOrders());
            List<Cell> torpedorange = torpedo.createCellListTorpedoRange(mySubmarine.getPositionX(), mySubmarine.getPositionY(), board);
            mySubmarine.setListTorpedoRange(torpedorange);

            // check count fire list in progres
            if (mySubmarine.getTorpedoFireList() != null) {
                long countFireList = mySubmarine.getTorpedoFireList().stream().count();
                // check
                System.err.println("fire list in progress: " + countFireList);
            }

            // ********** 3   **** Locate opponent ************************************************************************:
            // ----------------------------------- after torpedo -----------------------------------------------------
            if (mySubmarine.getListOpponentPositionAfterTorpedo() != null) { // update with his next move
                locateOpponent.updateOpponentPresenceListAfterTorpedoWithNewMovement(mySubmarine, board); // record result list on mySubmarine
            }
            if (isOpponentSentTorpedo) {
                locateOpponent.createRangeCellOfOpponentPositionWhenSendTorpedo(mySubmarine, board);
            }
            // --------------------------------------------------------------------------------------------------------
            // check if opponent make surface
            int opponentSurfaceSector = locateOpponent.checkIfOpponentMakeSurface(mySubmarine, board);
            if (opponentSurfaceSector != -1) {
                // compare with my sector position
                if (opponentSurfaceSector == mySubmarine.getMySector()) {
                    oppPresenceOnMySector = true;
                }
                // todo if not -> move to this sector
            }

            // check if opponent make silence move
            boolean opponentMoveSilently = locateOpponent.checkIfOpponentMakeSilence(mySubmarine, board);
            if (opponentMoveSilently) {
                // check
                System.err.println("opponent move silently ");
                mySubmarine.setOpponentTorpedoExplosion(null);
                mySubmarine.setTorpedoFireList(null);
            }
            // *********** 4 ****** MySubmarine Check **********************************************************************
            // sonar feedback
            //check
            System.err.println("retour sonar: " + mySubmarine.getSonarResult());

            // check mySector position
            int mySectorId = utils.findMyPositionSector(mySubmarine, board);
            mySubmarine.setMySector(mySectorId);

            // check if i can fire torpedo following my position (my torpedo loaded and opponent locate list)
            if (mySubmarine.getTorpedoCooldown() == 0 && mySubmarine.getListOpponentPositionAfterTorpedo() != null) {
                fireTorpedoFollowingOppTorp = torpedo.canIFireTorpedo(mySubmarine, board);
            }
            // check if my torpedo is loaded
            if (mySubmarine.getTorpedoCooldown() == 0) {
                loadedTorpedo = true;
                // check
                System.err.println("Torpedo loaded and list range ok");
            }
            // check if my sonar is loaded
            if (mySubmarine.getSonarCooldown() == 0) {
                loadedSonar = true;
                //check
                System.err.println("Sonar loaded");
            }
            // check if silence is loaded
            if (mySubmarine.getSilenceCooldown() == 0) {
                loadedSilence = true;
                //check
                System.err.println("Silence loaded");
                // lunch sonar in my sector
                if (mySubmarine.getSonarCooldown() == 0) {
                    lunchSonar = true;
                    //check
                    System.err.println("sonar can sent!");
                }
            }
            // add torpedo order if possible
            if (fireTorpedoFollowingOppTorp && loadedTorpedo) {
                fireFollowingTorpedoFeedback = true;
                loadedTorpedo = false;
            }

            // if mySubmarine life increase 2 point -> next move to silence if possible
            int lifeDown = utils.compareLifeLoopBefore(mySubmarine);
            // check
//            System.err.println("life down: " + lifeDown);
            if ((lifeDown == 2) && loadedSilence) {
                // next move to silence
                moveNextOnSilence = true;
            }


            // ********** 5   **** Action ***********************************************************************************:
            // think for next move
            // first solution (random)
//            String nextMove = move.moveIA1(mySubmarine, board);
            // Second solution (to a point)
            String nextMove = move.moveIA3(mySubmarine, board);

            // check if opponent is in my  sector with sonar feedback
            //check
            if (mySubmarine.getSonarResult().equals("Y")) {
                fireFollowingSonarFeedback = true;
            }

            // if mySubmarin make SURFACE persist last mySubmarine position on object board
            if (nextMove == "SURFACE") {listCellAlreadyVisited = new ArrayList<>(); }
            listCellAlreadyVisited.add(myMoveCell);
            board.setListCellAlreadyVisited(listCellAlreadyVisited);


            // if my submarine move -> Charge torpedo first and sonar after and silence after
            if (nextMove != "SURFACE" && !loadedTorpedo && !moveNextOnSilence) {
                nextMove = nextMove + chargeTorpedo;
            }
            if (nextMove != "SURFACE" && loadedTorpedo && !loadedSonar && !moveNextOnSilence) {
                nextMove = nextMove + chargeSonar;
            }
            if (nextMove != "SURFACE" && loadedTorpedo && loadedSonar && loadedSilence && lunchSonar && !moveNextOnSilence) {
                //check
                System.err.println("passage sonar order");
                // lunch sonar
                nextMove = sentSonar + String.valueOf(mySubmarine.getMySector()) + "|" + nextMove;
                loadedSonar = false;
            }
            if (nextMove != "SURFACE" && loadedTorpedo && loadedSonar && !moveNextOnSilence) {
                nextMove = nextMove + chargeSilence;
            }
            // if important damage try to move silence (for now only one move on silence)
            if (nextMove != "SURFACE" && moveNextOnSilence && loadedSilence) {
                //check
                System.err.println("next move on silence!");
                moveNextOnSilence = false;
                loadedSilence = false;
                nextMove = silence + mySubmarine.getMyNextMove().getCardinalPoint() + 1;
            }
            // check
                System.err.println("Fire Following Torpedo Feedback: " + fireFollowingTorpedoFeedback);
                System.err.println("Fire Following Sonar Feedback: " + fireFollowingSonarFeedback);
                System.err.println("Opponent presence on my sector: " + oppPresenceOnMySector);
                System.err.println("--Torpedo loaded: " + loadedTorpedo + " --");

            // ----------- FIRE ---------- FIRE -------------- FIRE -----------
            // add fire on move order following opponent fire
            if (fireFollowingTorpedoFeedback && !fireFollowingSonarFeedback && !moveNextOnSilence) {
                System.err.println("passage fire following opp torpedo");
                // get random cell on fireList
                Cell randomfireTorpedo = utils.randomCellOnList(mySubmarine.getTorpedoFireList());
                String addfireTorpedoString = "TORPEDO " + randomfireTorpedo.getX() + " " + randomfireTorpedo.getY();
                String nextMoveFire = addfireTorpedoString + "|" + nextMove;
                // order of move and fire
                loadedTorpedo = false;
                System.err.println("------- FIRE  -----------");
                System.out.println(nextMoveFire);
            }
            // add fire on move order following sonar or opponent presence on my sector
            else if (fireFollowingSonarFeedback && !fireFollowingTorpedoFeedback && !moveNextOnSilence ||
                    oppPresenceOnMySector && loadedTorpedo){
                System.err.println("passage fire following opp on my sector (sonar or opp SURFACE)");
                // get random cell on mySector
                Cell randomfireTorpedo = torpedo.createPossibilitiCellOnSector(mySubmarine, board);
                String addfireTorpedoString = "TORPEDO " + randomfireTorpedo.getX() + " " + randomfireTorpedo.getY();
                String nextMoveFire = addfireTorpedoString + "|" + nextMove;

                // update lifeLoopBefore
                mySubmarine.setLifeLoopBefore(mySubmarine.getLife());
                // order of move and fire
                loadedTorpedo = false;
                System.err.println("------- FIRE  -----------");
                System.out.println(nextMoveFire);

            }
            // -----------JUST MOVE  ---------- JUST MOVE -------------- JUST MOVE -----------
            else {
                // update lifeLoopBefore
                mySubmarine.setLifeLoopBefore(mySubmarine.getLife());
                // order for move
                System.out.println(nextMove);
            }

            // print submarines info
//            System.err.println("My submarine: " + mySubmarine.toString());
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
    public String deploy1(HashMap<Integer, String> earthMap) {
        // 1 deploy corner bottom left or middle bottom if there is earth
        String line14 = earthMap.get(14);
        char deployPoint = line14.charAt(0);
        if (deployPoint == '.') { return "0 14"; }
        else { return "7 14"; }
    }

    /**
     * Deploy corner up left or Middle up
     */
    public String deploy2(HashMap<Integer, String> earthMap) {
        String line0 = earthMap.get(0);
        char deployPoint = line0.charAt(0);
        if (deployPoint == '.') { return "0 0"; }
        else { return "7 0"; }
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
                    Cell earthCell = new Cell(j,i, null,null);
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
    private int destinationSector = 7; // 8 in fact

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
        List<Cell> listWithoutMapLimitAndEarth = utils.removeEarthCell(listWithoutMapLimit, board);

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
        List<Cell> listWithoutMapLimitAndEarth = utils.removeEarthCell(listWithoutMapLimit, board);

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
        int min = 1;
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
        List<Cell> listWithoutMapLimitAndEarth = utils.removeEarthCell(listWithoutMapLimit, board);

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
        List<Cell> listWithoutEndOfMapAndEarth = utils.removeEarthCell(listWithoutEndOfMap, board);
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

        for (int i = 0; i < inputRangeListAfterTorpedo.size() ; i++) {
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

    public boolean canIFireTorpedo(Submarine mySubmarine, Board board) {
        List<Cell> potentialfireList = mixRangePossibilityAfterTorpedoWithMyRangeTorpedo(mySubmarine.getListOpponentPositionAfterTorpedo(), mySubmarine);
        // remove cell around me
        utils.createSafeCellListAroundMe(mySubmarine, board);
        // remove safeList of potentialFireList
        List<Cell> fireList = utils.removeCellsOnList(potentialfireList, mySubmarine.getSafeListOfCellAroundMe());
        mySubmarine.setTorpedoFireList(fireList);
        // check
        Long countFirecell = fireList.stream().count();
        System.err.println("Possibility of fire on: " + countFirecell);
//                System.err.println("Possibility of fire on: " + fireList.toString());
        if (!fireList.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public Cell createPossibilitiCellOnSector(Submarine mySubmarine, Board board) {
        List<Cell> resultList = new ArrayList<>();

        // get random cell on sector without safe list around me and only my torpedo range
        List<Cell> mySectorCellList = utils.findSectorCellWithPosition(mySubmarine, board);
        // list of safe cell (around me)
        utils.createSafeCellListAroundMe(mySubmarine, board);
        List<Cell> mySafeCellList = mySubmarine.getSafeListOfCellAroundMe();
        // remove safe cell to mysecto cells
        List<Cell> mySectorWithoutsafeList = utils.removeCellsOnList(mySectorCellList, mySafeCellList);
        // get my range torpedo list
        // keep only forFireList - myrangeTorpedo -> to verify
        List<Cell> fireList = mixRangePossibilityAfterTorpedoWithMyRangeTorpedo(mySectorWithoutsafeList,mySubmarine);
        // check
//                System.err.println("potential fire following sonar: " + fireList.toString());
        System.err.println("potential fire on my sector: " + fireList.stream().count());
        // get random cell on fireList
        Cell randomfireTorpedo = utils.randomCellOnList(fireList);


        return randomfireTorpedo;
    }
}

class Utils {

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

    public void createSafeCellListAroundMe(Submarine mySubmarine, Board board) {
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
        List<Cell> listWithoutEndOfMap = removeEndOfMap(listSafeCell, board);

        // record on my submarine
        mySubmarine.setSafeListOfCellAroundMe(listWithoutEndOfMap);

        // check
//        System.err.println("safe list: " + listWithoutEndOfMap.toString());

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
}

// Objects

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
    private List<Cell> safeListOfCellAroundMe;
    private List<Cell> listTorpedoRange;
    private List<Cell> listOpponentPositionAfterTorpedo;
    private Cell opponentTorpedoExplosion;
    private List<Cell> torpedoFireList;
    private Cell myNextMove;
    private int destinationSector;


    // constructor
    public Submarine() {
    }

    public Submarine(int id, int positionX, int positionY, int mySector, int life, int lifeLoopBefore, int torpedoCooldown, int sonarCooldown, int silenceCooldown, int mineCooldown, String sonarResult, String opponentOrders, List<Cell> safeListOfCellAroundMe, List<Cell> listTorpedoRange, List<Cell> listOpponentPositionAfterTorpedo, Cell opponentTorpedoExplosion, List<Cell> torpedoFireList, Cell myNextMove, int destinationSector) {
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
        this.safeListOfCellAroundMe = safeListOfCellAroundMe;
        this.listTorpedoRange = listTorpedoRange;
        this.listOpponentPositionAfterTorpedo = listOpponentPositionAfterTorpedo;
        this.opponentTorpedoExplosion = opponentTorpedoExplosion;
        this.torpedoFireList = torpedoFireList;
        this.myNextMove = myNextMove;
        this.destinationSector = destinationSector;
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
                ", safeListOfCellAroundMe=" + safeListOfCellAroundMe +
                ", listTorpedoRange=" + listTorpedoRange +
                ", listOpponentPositionAfterTorpedo=" + listOpponentPositionAfterTorpedo +
                ", opponentTorpedoExplosion=" + opponentTorpedoExplosion +
                ", torpedoFireList=" + torpedoFireList +
                ", myNextMove=" + myNextMove +
                ", destinationSector=" + destinationSector +
                '}';
    }
}

class Cell {
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



